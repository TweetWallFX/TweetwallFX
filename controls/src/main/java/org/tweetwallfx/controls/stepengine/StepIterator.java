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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.json.bind.JsonbBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jörg Michelberger
 */
public class StepIterator implements Iterator<Step> {

    private static final Logger LOGGER = LogManager.getLogger(StepIterator.class);
    private Step current = null;
    private int stepIndex = 0;
    private final List<Step> steps = new ArrayList<>();

    private StepIterator(final List<Step> steps) {
        this.steps.addAll(steps);
    }

    public static StepIterator of(final Step... steps) {
        return new StepIterator(Arrays.asList(steps));
    }

    public static StepIterator of(final List<Step> steps) {
        return new StepIterator(steps);
    }

    public static StepIterator ofDefaultConfiguration() {
        StepIterator.Builder builder = new StepIterator.Builder();
        try (InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream("steps.json")) {
            StepEngineConfiguration stepEngineConfig = JsonbBuilder.create().fromJson(s, StepEngineConfiguration.class);
            stepEngineConfig.steps.forEach(className -> builder.addStep(className));
        } catch (IOException ex) {
            LOGGER.error("IO Problem loading steps description file", ex);
        }
        return builder.build();
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void applyWith(final Consumer<Step> consumer) {
        steps.forEach(consumer);
    }

    public Step getCurrent() {
        return current;
    }

    public Step getNext() {
        int getIndex = stepIndex;
        if (getIndex == steps.size()) {
            getIndex = 0;
        }
        return steps.get(getIndex);
    }

    @Override
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

        public Builder addStep(final String stepClassName) {
            final Step.Factory factory = FACTORIES.get(stepClassName);

            if (null == factory) {
                LOGGER.error("No Factory exist that can create Step '{}'", stepClassName);
            } else {
                final Step step = factory.create();

                if (null == step) {
                    LOGGER.error("Step.Factory '{}' failed to create Step '{}'", factory, stepClassName);
                } else {
                    LOGGER.info("Step.Factory '{}' created '{}'", factory, step);
                    steps.add(step);
                }
            }

            return this;
        }

        public StepIterator build() {
            return new StepIterator(steps);
        }
    }
}
