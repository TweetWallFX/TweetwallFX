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
package org.tweetwallfx.config;

import java.util.Objects;

/**
 * POJO for reading Settings concerning the HTTP Connection itself.
 *
 * @param proxy the Proxy settings to use with HTTP connections
 */
public final record ConnectionSettings(
        Proxy proxy) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "connectionSettings";

    /**
     * Service implementation converting the configuration data of the root key
     * {@link ConnectionSettings#CONFIG_KEY} into {@link ConnectionSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return ConnectionSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return ConnectionSettings.class;
        }
    }

    /**
     * POJO containing the proxy setting to use when working with HTTP
     * Connections.
     *
     * @param host the host of proxy server to use
     *
     * @param port the port number of the proxy server to use
     *
     * @param user the user name to use for the proxy connection
     *
     * @param password the password to use for the proxy connection
     */
    public static record Proxy(
            String host,
            Integer port,
            String user,
            String password) {

        public Proxy(
                final String host,
                final Integer port,
                final String user,
                final String password) {
            this.host = Objects.requireNonNull(host, "host must not be null");
            this.port = Objects.requireNonNull(port, "port must not be null");
            this.user = user;
            this.password = password;

            if (Objects.nonNull(user) ^ Objects.nonNull(password)) {
                throw new IllegalArgumentException("user and password values have to be set or unset at the sametime");
            }
        }
    }
}
