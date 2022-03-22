/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 TweetWallFX
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
package org.tweetwallfx.stepengine.api.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.ConfigurationConverter;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine;
import org.tweetwallfx.stepengine.api.Visualization;
import org.tweetwallfx.util.ConfigurableObjectBase;
import org.tweetwallfx.util.JsonDataConverter;
import static org.tweetwallfx.util.Nullable.nullable;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * POJO for reading Settings concerning the {@link StepEngine}.
 *
 * @param steps list containing the definitions for the steps in the
 * {@link StepEngine}
 *
 * @param dataProviderSettings list of settings for {@link DataProvider}
 * instances
 *
 * @param visualizationSettings list of settings for {@link Visualization}
 * instances
 */
public record StepEngineSettings(
        List<StepDefinition> steps,
        List<DataProviderSetting> dataProviderSettings,
        Map<String, VisualizationSetting> visualizationSettings) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "stepEngine";

    public StepEngineSettings(
            final List<StepDefinition> steps,
            final List<DataProviderSetting> dataProviderSettings,
            final Map<String, VisualizationSetting> visualizationSettings) {
        this.steps = List.copyOf(Objects.requireNonNull(steps, "steps must not be null"));
        this.dataProviderSettings = nullable(dataProviderSettings);
        this.visualizationSettings = nullable(visualizationSettings);
    }

    @Override
    public List<StepDefinition> steps() {
        return List.copyOf(steps);
    }

    @Override
    public List<DataProviderSetting> dataProviderSettings() {
        return List.copyOf(dataProviderSettings);
    }

    @Override
    public Map<String, VisualizationSetting> visualizationSettings() {
        return Map.copyOf(visualizationSettings);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link StepEngineSettings#CONFIG_KEY} into {@link StepEngineSettings}.
     */
    public static final class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return StepEngineSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return StepEngineSettings.class;
        }
    }

    /**
     * Configurable object containing configuration data (via
     * {@link #getConfig()} or {@link #getConfig(java.lang.Class)}) for a
     * {@link Step} instance (identified via {@link #getStepClassName()}.
     */
    public static final class StepDefinition extends ConfigurableObjectBase {

        private String stepClassName;

        /**
         * Returns the class name of the {@link Step}.
         *
         * @return the class name of the {@link Step}
         */
        public String getStepClassName() {
            return stepClassName;
        }

        /**
         * Sets the class name of the {@link Step}.
         *
         * @param stepClassName the class name of the {@link Step}
         */
        public void setStepClassName(final String stepClassName) {
            this.stepClassName = stepClassName;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "stepClassName", getStepClassName(),
                    "config", getConfig()
            ), super.toString());
        }
    }

    /**
     * Configurable object containing configuration data (via
     * {@link #getConfig()} or {@link #getConfig(java.lang.Class)}) for a
     * {@link DataProvider} instance (identified via
     * {@link #getDataProviderClassName()}.
     *
     * <p>
     * Configuration can be extended by configuring the properties of the
     * {@code config} section of this definition on the root level of the
     * Configuration.
     */
    public static final class DataProviderSetting extends ConfigurableObjectBase {

        private String dataProviderClassName;

        /**
         * Returns the class name of the {@link DataProvider}.
         *
         * @return the class name of the {@link DataProvider}
         */
        public String getDataProviderClassName() {
            return dataProviderClassName;
        }

        /**
         * Sets the class name of the {@link DataProvider}
         *
         * @param dataProviderClassName the class name of the
         * {@link DataProvider}
         */
        public void setDataProviderClassName(final String dataProviderClassName) {
            this.dataProviderClassName = dataProviderClassName;
        }

        @Override
        public <T> T getConfig(final Class<T> typeClass) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> specializedConfig = (Map<String, Object>) Configuration.getInstance().getConfig(typeClass.getName(), Collections.emptyMap());
            final Map<String, Object> mergedConfig = Configuration.mergeMap(getConfig(), specializedConfig);
            return JsonDataConverter.convertFromObject(mergedConfig, typeClass);
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "dataProviderClassName", getDataProviderClassName(),
                    "config", getConfig()
            ), super.toString());
        }
    }

    /**
     * Configurable object containing configuration data (via
     * {@link #getConfig()} or {@link #getConfig(java.lang.Class)}) for a
     * {@link Visualization} instance (identified via
     * {@link #getVisualizationClassName()}.
     *
     * <p>
     * Configuration can be extended by configuring the properties of the
     * {@code config} section of this definition on the root level of the
     * Configuration.
     */
    public static final class VisualizationSetting extends ConfigurableObjectBase {

        private String visualizationClassName;

        /**
         * Returns the class name of the {@link Visualization}.
         *
         * @return the class name of the {@link Visualization}
         */
        public String getVisualizationClassName() {
            return visualizationClassName;
        }

        /**
         * Sets the class name of the {@link Visualization}
         *
         * @param visualizationClassName the class name of the
         * {@link Visualization}
         */
        public void setVisualizationClassName(final String visualizationClassName) {
            this.visualizationClassName = visualizationClassName;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "visualizationClassName", getVisualizationClassName(),
                    "config", getConfig()
            ), super.toString());
        }
    }
}
