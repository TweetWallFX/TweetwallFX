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
package org.tweetwallfx.tweet.api.config;

import java.util.Collections;
import java.util.Map;
import org.tweetwallfx.config.ConfigurationConverter;
import static org.tweetwall.util.ToString.*;

/**
 * POJO for reading Settings concerning the twitter client.
 */
public final class TwitterSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "twitter";
    private boolean debugEnabled = false;
    private Map<String, Object> extendedConfig;
    private boolean extendedMode = false;
    private OAuth oauth;

    /**
     * Returns a flag indicating that the twitter client is to work in debug
     * mode.
     *
     * @return a flag indicating that the twitter client is to work in debug
     * mode
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * Sets a flag indicating that the twitter client is to work in debug mode.
     *
     * @param debugEnabled a flag indicating that the twitter client is to work
     * in debug mode
     */
    public void setDebugEnabled(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    /**
     * Returns the extended configuration of the twitter client.
     *
     * @return the extended configuration of the twitter client
     */
    public Map<String, Object> getExtendedConfig() {
        return null == extendedConfig
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(extendedConfig);
    }

    /**
     * Sets the extended configuration of the twitter client.
     *
     * @param extendedConfig the extended configuration of the twitter client
     */
    public void setExtendedConfig(final Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }

    /**
     * Returns a flag indicating if the twitter client is to use the extended
     * mode.
     *
     * @return a flag indicating if the twitter client is to use the extended
     * mode
     */
    public boolean isExtendedMode() {
        return extendedMode;
    }

    /**
     * Set a flag indicating if the twitter client is to use the extended mode.
     *
     * @param extendedMode a flag indicating if the twitter client is to use the
     * extended mode
     */
    public void setExtendedMode(final boolean extendedMode) {
        this.extendedMode = extendedMode;
    }

    /**
     * Returns the OAuth setting the twitter client is to use in order to
     * connect with twitter.
     *
     * @return the OAuth setting the twitter client is to use in order to
     * connect with twitter
     */
    public OAuth getOauth() {
        return oauth;
    }

    /**
     * Returns the OAuth setting the twitter client is to use in order to
     * connect with twitter.
     *
     * @param oauth the OAuth setting the twitter client is to use in order to
     * connect with twitter
     */
    public void setOauth(final OAuth oauth) {
        this.oauth = oauth;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "debugEnabled", isDebugEnabled(),
                "extendedConfig", getExtendedConfig(),
                "extendedMode", isExtendedMode(),
                "oauth", getOauth()
        )) + " extends " + super.toString();
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
     */
    public static final class OAuth {

        private String consumerKey;
        private String consumerSecret;
        private String accessToken;
        private String accessTokenSecret;

        /**
         * Returns the consumer key.
         *
         * @return the consumer key
         */
        public String getConsumerKey() {
            return consumerKey;
        }

        /**
         * Sets the consumer key.
         *
         * @param consumerKey the consumer key
         */
        public void setConsumerKey(final String consumerKey) {
            this.consumerKey = consumerKey;
        }

        /**
         * Returns the consumer secret.
         *
         * @return the consumer secret
         */
        public String getConsumerSecret() {
            return consumerSecret;
        }

        /**
         * Sets the consumer secret.
         *
         * @param consumerSecret the consumer secret
         */
        public void setConsumerSecret(final String consumerSecret) {
            this.consumerSecret = consumerSecret;
        }

        /**
         * Returns the access token.
         *
         * @return the access token
         */
        public String getAccessToken() {
            return accessToken;
        }

        /**
         * Sets the access token.
         *
         * @param accessToken the access token
         */
        public void setAccessToken(final String accessToken) {
            this.accessToken = accessToken;
        }

        /**
         * Returns the access token secret.
         *
         * @return the access token secret
         */
        public String getAccessTokenSecret() {
            return accessTokenSecret;
        }

        /**
         * Sets the access token secret.
         *
         * @param accessTokenSecret the access token secret
         */
        public void setAccessTokenSecret(final String accessTokenSecret) {
            this.accessTokenSecret = accessTokenSecret;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "consumerKey", getConsumerKey(),
                    "consumerSecret", getConsumerSecret(),
                    "accessToken", getAccessToken(),
                    "accessTokenSecret", getAccessTokenSecret()
            )) + " extends " + super.toString();
        }
    }
}
