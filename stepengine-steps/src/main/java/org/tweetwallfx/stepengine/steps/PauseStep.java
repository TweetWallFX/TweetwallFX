/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * Step that pauses for a configured duration before proceeding.
 */
public class PauseStep implements Step {

    private final Duration pause;
    private final String skipWhenSkipped;

    private PauseStep(final Duration pause, final String skipWhenSkipped) {
        this.pause = pause;
        this.skipWhenSkipped = skipWhenSkipped;
    }

    @Override
    public Duration preferredStepDuration(final MachineContext context) {
        return this.pause;
    }

    @Override
    public void doStep(final MachineContext context) {
        context.proceed();
    }

    @Override
    public boolean requiresPlatformThread() {
        return false;
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return null == skipWhenSkipped
                ? false
                : skipWhenSkipped.equals(context.get(Step.SKIP_TOKEN));
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link PauseStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() {
        }

        @Override
        public Step create(final StepEngineSettings.StepDefinition stepDefinition) {
            final Config config = stepDefinition.getConfig(Config.class);
            return new PauseStep(config.getDuration(), config.getSkipWhenSkipped());
        }

        @Override
        public Class<PauseStep> getStepClass() {
            return PauseStep.class;
        }
    }

    /**
     * POJO to configure {@link PauseStep}.
     */
    public static class Config {

        private ChronoUnit unit = ChronoUnit.SECONDS;
        private long amount = 5;
        private String skipWhenSkipped = null;

        /**
         * Creates a new instance.
         */
        public Config() {
        }

        /**
         * {@return the unit of the pause duration}
         */
        public ChronoUnit getUnit() {
            return unit;
        }

        /**
         * Sets the unit of the pause duration.
         *
         * @param unit the unit of the pause duration
         */
        public void setUnit(final ChronoUnit unit) {
            this.unit = Objects.requireNonNull(unit, "unit must not be null!");
        }

        /**
         * {@return the amount of the pause duration}
         */
        public long getAmount() {
            return amount;
        }

        /**
         * Sets the amount of the pause duration.
         *
         * @param amount the amount of the pause duration
         */
        public void setAmount(final long amount) {
            this.amount = amount;
        }

        private Duration getDuration() {
            return Duration.of(getAmount(), getUnit());
        }

        /**
         * Sets the token which, when skipped, causes this step to be skipped.
         *
         * @param skipWhenSkipped the token which causes this step to be skipped
         */
        public void setSkipWhenSkipped(String skipWhenSkipped) {
            this.skipWhenSkipped = Objects.requireNonNull(skipWhenSkipped, "skipWhenSkipped must not be null!");
        }

        /**
         * {@return the token which, when skipped, causes this step to be
         * skipped}
         */
        public String getSkipWhenSkipped() {
            return skipWhenSkipped;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "amount", getAmount(),
                    "unit", getUnit(),
                    "skipWhenSkipped", getSkipWhenSkipped()));
        }
    }
}
