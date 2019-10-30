/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
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
package org.tweetwallfx.stepengine.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * @author Jörg Michelberger
 */
class StepIterator {

    private static final Logger LOGGER = LogManager.getLogger(StepIterator.class);
    private int stepIndex = 0;
    private final List<Step> steps;
    private final Map<Step, Collection<Class<? extends DataProvider>>> requiredDataProviders;

    private StepIterator(final List<Step> steps, final Map<Step, Collection<Class<? extends DataProvider>>> requiredDataProviders) {
        this.steps = new ArrayList<>(steps);
        this.requiredDataProviders = Collections.unmodifiableMap(requiredDataProviders);

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("StepIterator has no steps to iterate through!");
        }
    }

    Collection<Class<? extends DataProvider>> getRequiredDataProviders(final Step step) {
        return requiredDataProviders.getOrDefault(step, Collections.emptyList());
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

    Set<Class<? extends DataProvider>> getRequiredDataProviders() {
        return requiredDataProviders.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    Step next() {
        if (stepIndex == steps.size()) {
            //loop
            stepIndex = 0;
        }

        return steps.get(stepIndex++);
    }

    private static class Builder {

        private static final Map<String, Step.Factory> FACTORIES = StreamSupport
                .stream(ServiceLoader.load(Step.Factory.class).spliterator(), false)
                .peek(sf -> LOGGER.info("Registering Step.Factory '{}' which creates the Step '{}'", sf, sf.getStepClass().getCanonicalName()))
                .collect(Collectors.toMap(
                        sf -> sf.getStepClass().getCanonicalName(),
                        Function.identity()));
        private final List<Step> steps = new ArrayList<>();
        private final Map<Step, Collection<Class<? extends DataProvider>>> requiredDataProviders = new HashMap<>();

        private Builder addStep(final StepEngineSettings.StepDefinition stepDefinition) {
            final String stepClassName = stepDefinition.getStepClassName();
            final Step.Factory factory = FACTORIES.get(stepClassName);

            Objects.requireNonNull(factory, "Step.Factory creating '" + stepClassName + "' does not exist!");
            final Step step = factory.create(stepDefinition);
            requiredDataProviders.put(step, Collections.unmodifiableList(new ArrayList<>(factory.getRequiredDataProviders(stepDefinition))));

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
