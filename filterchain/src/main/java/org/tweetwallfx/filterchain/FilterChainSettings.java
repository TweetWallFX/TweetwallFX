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
import java.util.Objects;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.ConfigurationConverter;
import org.tweetwallfx.util.ConfigurableObjectBase;
import org.tweetwallfx.util.JsonDataConverter;
import static org.tweetwallfx.util.Nullable.nullable;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * POJO for reading Settings concerning {@link FilterChain}s.
 *
 * @param chains the mapping of the {@link FilterChainDefinition}s to the name
 * of the defined {@link FilterChain}
 */
public record FilterChainSettings(
        Map<String, FilterChainDefinition> chains) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "filterchains";

    public FilterChainSettings(
            final Map<String, FilterChainDefinition> chains) {
        this.chains = Map.copyOf(Objects.requireNonNull(chains, "chains must not be null!"));
    }

    @Override
    public Map<String, FilterChainDefinition> chains() {
        return Map.copyOf(chains);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link FilterChainSettings#CONFIG_KEY} into {@link FilterChainSettings}.
     */
    public static final class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return FilterChainSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return FilterChainSettings.class;
        }
    }

    /**
     * POJO defining a {@link FilterChain}.
     *
     * @param defaultResult a boolean flag determining if the evaluated object
     * are accepted or rejected should no previous evaluation by the
     * {@link FilterStep}s have terminated the evaluation
     *
     * @param filterSteps the filter steps contained in the {@link FilterChain}
     *
     * @param domainObjectClassName the class name of the domain object being
     * evaluated
     */
    public static record FilterChainDefinition(
            Boolean defaultResult,
            List<FilterStepDefinition> filterSteps,
            String domainObjectClassName) {

        public FilterChainDefinition(
                final Boolean defaultResult,
                final List<FilterStepDefinition> filterSteps,
                final String domainObjectClassName) {
            this.defaultResult = defaultResult;
            this.filterSteps = nullable(filterSteps);
            this.domainObjectClassName = domainObjectClassName;
        }

        @Override
        public List<FilterStepDefinition> filterSteps() {
            return List.copyOf(filterSteps);
        }
    }

    /**
     * Configurable object containing configuration data (via
     * {@link #getConfig()} or {@link #getConfig(java.lang.Class)}) for a
     * {@link FilterStep} instance (identified via {@link #getStepClassName()}.
     *
     * <p>
     * Configuration can be extended by configuring the properties of the
     * {@code config} section of this definition on the root level of the
     * Configuration.
     */
    public static final class FilterStepDefinition extends ConfigurableObjectBase {

        private String stepClassName;

        /**
         * Returns the class name of the {@link FilterStep}.
         *
         * @return the class name of the {@link FilterStep}
         */
        public String getStepClassName() {
            return stepClassName;
        }

        /**
         * Sets the class name of the {@link FilterStep}.
         *
         * @param stepClassName the class name of the {@link FilterStep}
         */
        public void setStepClassName(final String stepClassName) {
            this.stepClassName = stepClassName;
        }

        @Override
        public <T> T getConfig(final Class<T> typeClass) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> specializedConfig = (Map<String, Object>) Configuration.getInstance().getConfig(typeClass.getName(), Map.of());
            final Map<String, Object> mergedConfig = Configuration.mergeMap(getConfig(), specializedConfig);
            return JsonDataConverter.convertFromObject(mergedConfig, typeClass);
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "stepClassName", getStepClassName(),
                    "config", getConfig()
            ), super.toString());
        }
    }
}
