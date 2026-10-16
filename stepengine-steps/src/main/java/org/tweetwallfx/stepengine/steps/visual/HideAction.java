/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2026 TweetWallFX
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
package org.tweetwallfx.stepengine.steps.visual;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import org.tweetwallfx.stepengine.api.ActionStep;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine;
import org.tweetwallfx.stepengine.api.Visualization;
import org.tweetwallfx.stepengine.api.VisualizationRegistry;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * A special Step in the {@link StepEngine}. It represents an action triggered against an existing
 * visualization implementing {@link Visualization.Hideable}.
 */
public class HideAction extends ActionStep<Visualization.Hideable> {

    /**
     * Creates a new instance.
     *
     * @param config   the configuration for this step
     * @param hideable the hideable visualization to act upon
     */
    public HideAction(Config config, Visualization.Hideable hideable) {
        super(hideable);
    }

    @Override
    public void doStep(StepEngine.MachineContext context) {
        getVisualization().doHide(context);
        context.proceed();
    }

    @Override
    public Duration preferredStepDuration(StepEngine.MachineContext context) {
        return super.preferredStepDuration(context);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link HideAction}.
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
            return new HideAction(config, VisualizationRegistry.INSTANCE.getVisualization(config.getVisualizationIdentifier(), Visualization.Hideable.class));
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(StepEngineSettings.StepDefinition stepDefinition) {
            String visualizationIdentifier = stepDefinition.getConfig(Config.class).getVisualizationIdentifier();
            return VisualizationRegistry.INSTANCE.getRequiredDataProviders(visualizationIdentifier);
        }

        @Override
        public Class<HideAction> getStepClass() {
            return HideAction.class;
        }
    }

    /**
     * POJO to configure {@link HideAction}.
     */
    public static class Config {

        private ChronoUnit unit = ChronoUnit.SECONDS;
        private long amount = 5;
        private String visualizationIdentifier = "Undefined";

        /**
         * Creates a new instance.
         */
        public Config() {
        }

        /**
         * {@return the unit of the hide duration}
         */
        public ChronoUnit getUnit() {
            return unit;
        }

        /**
         * Sets the unit of the hide duration.
         *
         * @param unit the unit of the hide duration
         */
        public void setUnit(final ChronoUnit unit) {
            this.unit = Objects.requireNonNull(unit, "unit must not be null!");
        }

        /**
         * {@return the amount of the hide duration}
         */
        public long getAmount() {
            return amount;
        }

        /**
         * Sets the amount of the hide duration.
         *
         * @param amount the amount of the hide duration
         */
        public void setAmount(final long amount) {
            this.amount = amount;
        }

        /**
         * {@return the identifier of the visualization to hide}
         */
        public String getVisualizationIdentifier() {
            return this.visualizationIdentifier;
        }

        /**
         * Sets the identifier of the visualization to hide.
         *
         * @param visualizationIdentifier the identifier of the visualization to
         *                                hide
         */
        public void setVisualizationIdentifier(String visualizationIdentifier) {
            this.visualizationIdentifier = visualizationIdentifier;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "visualizationIdentifier", getVisualizationIdentifier(),
                    "amount", getAmount(),
                    "unit", getUnit()
            )) + " extends " + super.toString();
        }
    }
}
