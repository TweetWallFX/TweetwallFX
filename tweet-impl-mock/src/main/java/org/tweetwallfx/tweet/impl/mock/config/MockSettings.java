/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2026 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mock.config;

import java.util.List;
import java.util.Objects;

import org.tweetwallfx.config.ConfigurationConverter;
import static org.tweetwallfx.util.Nullable.nullable;

/**
 * POJO for reading Settings concerning the mock client.
 *
 * <p>
 * Param {@code debugEnabled} a flag indicating that the mock client is to
 * work in debug mode (defaults to {@code false})
 *
 * <p>
 * Param {@code enabled} a flag indicating that the mock client enabled
 * (defaults to {@code true})
 *
 * <p>
 * Param {@code postInterval} the base data directory where the static data resides.
 *
 * @param debugEnabled a flag indicating that the mock client is to work in
 *                     debug mode (defaults to {@code false})
 * @param enabled      a flag indicating that the mock client is enabled
 *                     (defaults to {@code true})
 * @param postInterval the interval in seconds between two simulated posts
 *                     (defaults to {@code 30})
 * @param initialPosts the number of posts created during initialization
 *                     (defaults to {@code 0})
 * @param users        the user screen names to be mentioned in simulated posts
 * @param hashtags     the hash tags to be included in simulated posts
 */
public record MockSettings(
        Boolean debugEnabled,
        Boolean enabled,
        Integer postInterval,
        Integer initialPosts,
        List<String> users,
        List<String> hashtags) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "mock";

    /**
     * Creates a new instance, replacing {@code null} values with their default
     * values and validating the post interval.
     */
    public MockSettings {
        debugEnabled = Objects.requireNonNullElse(debugEnabled, false);
        enabled = Objects.requireNonNullElse(enabled, true);
        postInterval = Objects.requireNonNullElse(postInterval, 30);
        initialPosts = Objects.requireNonNullElse(initialPosts, 0);
        users = nullable(users);
        hashtags = nullable(hashtags);
        if (Boolean.TRUE.equals(enabled) && postInterval < 2) {
            throw new IllegalArgumentException("Minimum post interval is 2 seconds");
        }
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link MockSettings#CONFIG_KEY} into {@link MockSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        /**
         * Creates a new instance.
         */
        public Converter() { }

        @Override
        public String getResponsibleKey() {
            return MockSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return MockSettings.class;
        }
    }
}
