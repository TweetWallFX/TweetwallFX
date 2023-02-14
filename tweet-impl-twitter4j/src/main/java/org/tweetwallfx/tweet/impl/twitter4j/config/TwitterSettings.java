/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 TweetWallFX
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
package org.tweetwallfx.tweet.impl.twitter4j.config;

import java.util.Map;
import org.tweetwallfx.config.ConfigurationConverter;
import static org.tweetwallfx.util.Nullable.nullable;
import static org.tweetwallfx.util.Nullable.valueOrDefault;

/**
 * POJO for reading Settings concerning the twitter client.
 *
 * <p>
 * Param {@code debugEnabled} a flag indicating that the twitter client is to
 * work in debug mode (defaults to {@code  false})
 *
 * <p>
 * Param {@code enabled} a flag indicating that the twitter client enabled
 * (defaults to {@code true})
 *
 * <p>
 * Param {@code extendedConfig} the extended configuration of the twitter client
 *
 * <p>
 * Param {@code extendedMode} a flag indicating if the twitter client is to use
 * the extended mode (defaults to {@code  false})
 *
 * <p>
 * Param {@code oauth} the OAuth setting the twitter client is to use in order
 * to connect with twitter
 *
 * <p>
 * Param {@code ignoreRateLimit} a flag indicating that the twitter rate
 * limitations shall be ignored (defaults to {@code  true})
 */
public record TwitterSettings(
        Boolean debugEnabled,
        Boolean enabled,
        Map<String, Object> extendedConfig,
        Boolean extendedMode,
        OAuth oauth,
        Boolean ignoreRateLimit) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "twitter";

    public TwitterSettings(
            final Boolean debugEnabled,
            final Boolean enabled,
            final Map<String, Object> extendedConfig,
            final Boolean extendedMode,
            final OAuth oauth,
            final Boolean ignoreRateLimit) {
        this.debugEnabled = valueOrDefault(debugEnabled, false);
        this.enabled = valueOrDefault(enabled, true);
        this.extendedConfig = nullable(extendedConfig);
        this.extendedMode = valueOrDefault(extendedMode, false);
        this.oauth = oauth;
        this.ignoreRateLimit = valueOrDefault(ignoreRateLimit, true);
    }

    @Override
    public Map<String, Object> extendedConfig() {
        return Map.copyOf(extendedConfig);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link TwitterSettings#CONFIG_KEY} into {@link TwitterSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return TwitterSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return TwitterSettings.class;
        }
    }

    /**
     * POJO for the OAuth setting the twitter client is to use in order to
     * connect with twitter.
     *
     * <p>
     * Param {@code consumerKey} the consumer key
     *
     * <p>
     * Param {@code consumerSecret} the consumer secret
     *
     * <p>
     * Param {@code accessToken} the access token
     *
     * <p>
     * Param {@code accessTokenSecret} the access token secret
     */
    public static record OAuth(
            String consumerKey,
            String consumerSecret,
            String accessToken,
            String accessTokenSecret) {
    }
}
