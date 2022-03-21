/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.stepengine.steps;

import org.tweetwallfx.stepengine.api.config.AbstractConfig;
import java.util.Collection;
import java.util.Collections;
import org.tweetwallfx.stepengine.api.Controllable;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

public class GenericShutdownStep implements Step {

    private final Config config;

    private GenericShutdownStep(Config config) {
        this.config = config;
    }

    @Override
    public void doStep(StepEngine.MachineContext context) {
        context.get(config.stepToTerminate, Controllable.class).shutdown();
        context.proceed();
    }

    @Override
    public boolean requiresPlatformThread() {
        return false;
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link GenericShutdownStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public GenericShutdownStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new GenericShutdownStep(stepDefinition.getConfig(Config.class));
        }

        @Override
        public Class<GenericShutdownStep> getStepClass() {
            return GenericShutdownStep.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Collections.emptyList();
        }
    }

    public static class Config extends AbstractConfig {

        public String stepToTerminate = "Not defined - please set in configuration";
    }
}
