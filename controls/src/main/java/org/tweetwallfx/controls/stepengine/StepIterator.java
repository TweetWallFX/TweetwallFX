/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwallfx.controls.stepengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;

/**
 * @author Jörg Michelberger
 */
class StepIterator {

    private static final Logger LOGGER = LogManager.getLogger(StepIterator.class);
    private Step current = null;
    private int stepIndex = 0;
    private final List<Step> steps;
    private final Set<Class<? extends DataProvider>> requiredDataProviders;

    private StepIterator(final List<Step> steps, final Set<Class<? extends DataProvider>> requiredDataProviders) {
        this.steps = new ArrayList<>(steps);
        this.requiredDataProviders = Collections.unmodifiableSet(requiredDataProviders);

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("StepIterator has no steps to iterate through!");
        }
    }

    static StepIterator create() {
        final Builder builder = new Builder();

        Configuration.getInstance()
                .getConfigTyped(StepEngineSettings.CONFIG_KEY, StepEngineSettings.class)
                .getSteps()
                .forEach(builder::addStep);

        return builder.build();
    }

    void applyWith(final Consumer<Step> consumer) {
        steps.forEach(consumer);
    }

    public Set<Class<? extends DataProvider>> getRequiredDataProviders() {
        return requiredDataProviders;
    }

    public Step next() {
        if (stepIndex == steps.size()) {
            //loop
            stepIndex = 0;
        }
        current = steps.get(stepIndex++);
        return current;
    }

    private static class Builder {

        private static final Map<String, Step.Factory> FACTORIES;

        static {
            LOGGER.info("loading configurations data converters");
            final Map<String, List<Step.Factory>> converters = StreamSupport
                    .stream(ServiceLoader.load(Step.Factory.class).spliterator(), false)
                    .peek(sf -> LOGGER.info("Registering Step.Factory '{}' which creates the Step '{}'", sf, sf.getStepClass().getCanonicalName()))
                    .collect(Collectors.groupingBy(sf -> sf.getStepClass().getCanonicalName()));

            // ensure there are no conflicting ConfigurationConverter registered for a specific key
            converters.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1)
                    .findAny()
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("At most one ConfigurationConverter may be registered to convert configuration data but the following ConfigurationConverters are registered: " + e.getValue());
                    });

            // only one element in the list. so use the one element in the list instead of the list
            FACTORIES = converters.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
        }

        private final List<Step> steps = new ArrayList<>();
        private final Set<Class<? extends DataProvider>> requiredDataProviders = new HashSet<>();

        private Builder addStep(final StepEngineSettings.StepDefinition stepDefinition) {
            final String stepClassName = stepDefinition.getStepClassName();
            final Step.Factory factory = FACTORIES.get(stepClassName);

            Objects.requireNonNull(factory, "Step.Factory creating '" + stepClassName + "' does not exist!");
            requiredDataProviders.addAll(factory.getRequiredDataProviders(stepDefinition));
            final Step step = factory.create(stepDefinition);

            Objects.requireNonNull(step, () -> "Step.Factory '" + factory + "' failed to create Step!");
            LOGGER.info("Step.Factory '{}' created '{}'", factory, step);
            steps.add(step);

            return this;
        }

        public StepIterator build() {
            return new StepIterator(steps, requiredDataProviders);
        }
    }
}
