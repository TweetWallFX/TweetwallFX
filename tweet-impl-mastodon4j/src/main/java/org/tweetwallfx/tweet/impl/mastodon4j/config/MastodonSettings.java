/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mastodon4j.config;

import org.tweetwallfx.config.ConfigurationConverter;

import static org.tweetwallfx.util.Nullable.valueOrDefault;

/**
 * POJO for reading Settings concerning the mastodon client.
 *
 * <p>
 * Param {@code debugEnabled} a flag indicating that the mastodon client is to
 * work in debug mode (defaults to {@code false})
 *
 * <p>
 * Param {@code enabled} a flag indicating that the mastodon client enabled
 * (defaults to {@code true})
 *
 * <p>
 * Param {@code restUrl} the base mastodon host URL like {@code https://mastodon.social}
 *
 * <p>
 * Param {@code oauth} the OAuth setting the twitter client is to use in order
 * to connect with mastodon
 */
public record MastodonSettings(
        Boolean debugEnabled,
        Boolean enabled,
        String restUrl,
        OAuth oauth) {

    public MastodonSettings(
            final Boolean debugEnabled,
            final Boolean enabled,
            final String restUrl,
            final OAuth oauth) {
        this.debugEnabled = valueOrDefault(debugEnabled, false);
        this.enabled = valueOrDefault(enabled, true);
        this.restUrl = restUrl;
        this.oauth = oauth;
    }

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "mastodon";

    /**
     * Service implementation converting the configuration data of the root key
     * {@link MastodonSettings#CONFIG_KEY} into {@link MastodonSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return MastodonSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return MastodonSettings.class;
        }
    }

    /**
     * POJO for the OAuth setting the mastodon client is to use in order to
     * connect with mastodon.
     *
     * <p>
     * Param {@code accessToken} the access token
     */
    public static record OAuth(
            String accessToken) {
    }
}
