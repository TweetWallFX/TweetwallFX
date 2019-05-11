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
package org.tweetwallfx.stepengine.steps;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

public class PauseStep implements Step {

    private final Duration pause;

    private PauseStep(final Duration pause) {
        this.pause = pause;
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

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link PauseStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public Step create(final StepEngineSettings.StepDefinition stepDefinition) {
            final Config config = stepDefinition.getConfig(Config.class);
            return new PauseStep(config.getDuration());
        }

        @Override
        public Class<PauseStep> getStepClass() {
            return PauseStep.class;
        }
    }

    public static class Config {

        private ChronoUnit unit = ChronoUnit.SECONDS;
        private long amount = 5;

        public ChronoUnit getUnit() {
            return unit;
        }

        public void setUnit(final ChronoUnit unit) {
            this.unit = Objects.requireNonNull(unit, "unit must not be null!");
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(final long amount) {
            this.amount = amount;
        }

        private Duration getDuration() {
            return Duration.of(getAmount(), getUnit());
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "amount", getAmount(),
                    "unit", getUnit()
            )) + " extends " + super.toString();
        }
    }
}
