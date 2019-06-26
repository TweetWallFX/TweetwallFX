/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 TweetWallFX
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
import org.tweetwallfx.config.ConnectionSettings;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine;
import org.tweetwallfx.util.ConfigurableObjectBase;
import org.tweetwallfx.util.JsonDataConverter;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * POJO for reading Settings concerning the {@link StepEngine}.
 */
public final class StepEngineSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "stepEngine";
    private List<StepDefinition> steps = Collections.emptyList();
    private List<DataProviderSetting> dataProviderSettings = Collections.emptyList();

    /**
     * Returns a list containing the definitions for the steps in the
     * {@link StepEngine}.
     *
     * @return a list containing the definitions for the steps in the
     * {@link StepEngine}
     */
    public List<StepDefinition> getSteps() {
        return steps;
    }

    /**
     * Sets a list containing the definitions for the steps in the
     * {@link StepEngine}.
     *
     * @param steps a list containing the definitions for the steps in the
     * {@link StepEngine}
     */
    public void setSteps(final List<StepDefinition> steps) {
        Objects.requireNonNull(steps, "steps must not be null!");
        this.steps = steps;
    }

    /**
     * Returns a list of settings for {@link DataProvider} instances.
     *
     * @return a list of settings for {@link DataProvider} instances
     */
    public List<DataProviderSetting> getDataProviderSettings() {
        return dataProviderSettings;
    }

    /**
     * Sets a list of settings for {@link DataProvider} instances.
     *
     * @param dataProviderSettings a list of settings for {@link DataProvider}
     * instances
     */
    public void setDataProviderSettings(final List<DataProviderSetting> dataProviderSettings) {
        this.dataProviderSettings = dataProviderSettings;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "dataProviderSettings", getDataProviderSettings(),
                "steps", getSteps()
        ), super.toString());
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
}
