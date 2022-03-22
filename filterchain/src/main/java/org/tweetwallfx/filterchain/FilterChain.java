/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.filterchain;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;

/**
 * Chain of {@link FilterStep}s each evaluating an object and returning a
 * (possibly) terminal evaluation result. Upon encountering a terminal
 * evaluation result the evaluation terminates determines if the evaluated
 * object is accepted or rejected.
 *
 * @param <T> the type of the evaluated object
 */
public class FilterChain<T> {

    private static final Logger LOGGER = LogManager.getLogger(FilterChain.class);
    private static final Map<Class<?>, Map<String, FilterStep.Factory>> FACTORIES = StreamSupport
            .stream(ServiceLoader.load(FilterStep.Factory.class).spliterator(), false)
            .peek(fsf
                    -> LOGGER.info(
                    "Registering FilterStep.Factory '{}' which creates the FilterStep '{}' for the domain object '{}'",
                    fsf,
                    fsf.getFilterStepClass().getCanonicalName(),
                    fsf.getDomainObjectClass().getCanonicalName()))
            .collect(Collectors.groupingBy(
                    FilterStep.Factory::getDomainObjectClass,
                    Collectors.toMap(
                            fsf -> fsf.getFilterStepClass().getCanonicalName(),
                            Function.identity())));
    private final List<FilterStep<T>> filterSteps;
    private final boolean defaultResult;

    private FilterChain(
            final List<FilterStep<T>> filterSteps,
            final boolean defaultResult) {
        this.filterSteps = filterSteps;
        this.defaultResult = defaultResult;
    }

    /**
     * Creates a {@link FilterChain} with the given {@code name} and testing the
     * provided {@code domainObjectClass} based on what has been configured via
     * {@link FilterChainSettings}.
     *
     * @param <T> the type of the domain object of the {@link FilterChain}
     *
     * @param domainObjectClass the domain object class of the
     * {@link FilterChain}
     *
     * @param name the name of the configured {@link FilterChain}
     *
     * @return the created {@link FilterChain}
     */
    public static <T> FilterChain<T> createFilterChain(final Class<T> domainObjectClass, final String name) {
        Objects.requireNonNull(domainObjectClass, "domainObjectClass must not be null!");
        Objects.requireNonNull(name, "name must not be null!");
        final FilterChainSettings settings = Configuration.getInstance().getConfigTyped(
                FilterChainSettings.CONFIG_KEY,
                FilterChainSettings.class);

        final FilterChainSettings.FilterChainDefinition filterChainDefinition = settings.chains().get(name);
        Objects.requireNonNull(filterChainDefinition, "FilterChainDefinition with name '" + name + "' does not exist!");

        if (!domainObjectClass.getName().equals(filterChainDefinition.domainObjectClassName())) {
            throw new IllegalStateException("The Class name of the domain objects for FilterChainDefinition with name '"
                    + name
                    + "' do not match with requested domainClass (domainClass: '"
                    + domainObjectClass.getName()
                    + "'; filterChainDefinition.domainObjectClassName: '"
                    + filterChainDefinition.domainObjectClassName()
                    + "')");
        }

        final Map<String, FilterStep.Factory> domainObjectFilterStepFactories = FACTORIES.computeIfAbsent(domainObjectClass, doc -> {
            throw new NoSuchElementException("No FilterStep.Factory instance exist handling domainObjectClass '" + domainObjectClass.getCanonicalName() + "'.");
        });

        return new FilterChain<>(
                filterChainDefinition.filterSteps().stream()
                        .map(fsd
                                -> Objects.requireNonNull(
                                domainObjectFilterStepFactories.get(fsd.getStepClassName()),
                                "FilterStep.Factory for filterStepClassName '" + fsd.getStepClassName() + "' does not exist!")
                                .create(fsd))
                        .map(fs -> {
                            @SuppressWarnings("unchecked")
                            final FilterStep<T> fs2 = (FilterStep<T>) fs;
                            return fs2;
                        })
                        .collect(Collectors.toList()),
                filterChainDefinition.defaultResult()
        );
    }

    /**
     * Produces a {@link Predicate} based on this FilterChain.
     *
     * @return a {@link Predicate} based on this FilterChain
     */
    public Predicate<T> asPredicate() {
        return this::process;
    }

    private boolean process(final T t) {
        return filterSteps.stream()
                .peek(fs -> LOGGER.info("Checking {} with {}", t.getClass().getName(), fs.getClass().getName()))
                .map(fs -> fs.check(t))
                .peek(r -> LOGGER.info("Checking {} determined {}", t.getClass().getName(), r))
                .filter(FilterStep.Result::isTerminal)
                .peek(r -> LOGGER.info("Checking {} determined terminally {}", t.getClass().getName(), r))
                .findFirst()
                .map(FilterStep.Result::isAccepted)
                .orElseGet(() -> {
                    LOGGER.info("Found nothing definitive for {}. -> {}",
                            t.getClass().getName(),
                            defaultResult ? "ACCEPT" : "REJECT");
                    return defaultResult;
                });
    }
}
