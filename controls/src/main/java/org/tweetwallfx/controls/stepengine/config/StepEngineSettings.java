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
package org.tweetwallfx.controls.stepengine.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.tweetwall.util.JsonDataConverter;
import static org.tweetwall.util.ToString.*;
import org.tweetwallfx.config.ConfigurationConverter;
import org.tweetwallfx.config.ConnectionSettings;

/**
 * POJO for reading Settings concerning the HTTP Connection itself.
 */
public final class StepEngineSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "stepEngine";
    private List<StepDefinition> steps = Collections.emptyList();

    public List<StepDefinition> getSteps() {
        return steps;
    }

    public void setSteps(final List<StepDefinition> steps) {
        Objects.requireNonNull(steps, "steps must not be null!");
        this.steps = steps;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "steps", getSteps()
        )) + " extends " + super.toString();
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link ConnectionSettings#CONFIG_KEY} into {@link ConnectionSettings}.
     */
    public final static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return StepEngineSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return StepEngineSettings.class;
        }
    }

    public static final class StepDefinition {

        private String stepClassName;
        private Map<String, Object> config;

        public String getStepClassName() {
            return stepClassName;
        }

        public void setStepClassName(final String stepClassName) {
            this.stepClassName = stepClassName;
        }

        public Map<String, Object> getConfig() {
            return null == config
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(config);
        }

        public <T> T getConfig(final Class<T> typeClass) {
            return JsonDataConverter.convertFromObject(getConfig(), typeClass);
        }

        public void setConfig(final Map<String, Object> config) {
            this.config = config;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "stepClassName", getStepClassName(),
                    "config", getConfig()
            )) + " extends " + super.toString();
        }
    }
}
