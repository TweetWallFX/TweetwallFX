/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2025 TweetWallFX
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
package org.tweetwallfx.config;

import java.util.List;
import static org.tweetwallfx.util.Nullable.nullable;

/**
 * POJO for reading Settings concerning the Configuration itself.
 *
 * <p>
 * Param {@code additionalConfigurationURLs} is list of string representations
 * of URLs containing additional fragments for {@link Configuration}
 */
public record ConfigurationSettings(
        List<String> additionalConfigurationURLs) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "configuration";

    public ConfigurationSettings {
        additionalConfigurationURLs = nullable(additionalConfigurationURLs);
    }

    @Override
    public List<String> additionalConfigurationURLs() {
        return nullable(additionalConfigurationURLs);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link ConfigurationSettings#CONFIG_KEY} into
     * {@link ConfigurationSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return ConfigurationSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return ConfigurationSettings.class;
        }
    }
}
