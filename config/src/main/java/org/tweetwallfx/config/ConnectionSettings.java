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
package org.tweetwallfx.config;

import static org.tweetwall.util.ToString.*;

/**
 * POJO for reading Settings concerning the HTTP Connection itself.
 */
public final class ConnectionSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "connectionSettings";
    private Proxy proxy;

    /**
     * Returns the Proxy settings to use with HTTP connections.
     *
     * @return the Proxy settings to use with HTTP connections
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Sets the Proxy settings to use with HTTP connections.
     *
     * @param proxy the Proxy settings to use with HTTP connections
     */
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "proxy", getProxy()
        )) + " extends " + super.toString();
    }

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
     */
    public static final class Proxy {

        private String host = "";
        private int port = -1;
        private String user = "";
        private String password = "";

        /**
         * Returns the host of proxy server to use.
         *
         * @return the host of proxy server to use
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the host of proxy server to use.
         *
         * @param host the host of proxy server to use
         */
        public void setHost(final String host) {
            this.host = host;
        }

        /**
         * Returns the port number of the proxy server to use.
         *
         * @return the port number of the proxy server to use
         */
        public int getPort() {
            return port;
        }

        /**
         * Sets the port number of the proxy server to use.
         *
         * @param port the port number of the proxy server to use
         */
        public void setPort(final int port) {
            this.port = port;
        }

        /**
         * Returns the user name to use for the proxy connection.
         *
         * @return the user name to use for the proxy connection
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the user name to use for the proxy connection.
         *
         * @param user the user name to use for the proxy connection
         */
        public void setUser(final String user) {
            this.user = user;
        }

        /**
         * Returns the password to use for the proxy connection.
         *
         * @return the password to use for the proxy connection
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the password to use for the proxy connection.
         *
         * @param password the password to use for the proxy connection
         */
        public void setPassword(final String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "host", getHost(),
                    "port", getPort(),
                    "user", getUser(),
                    "password", getPassword()
            )) + " extends " + super.toString();
        }
    }
}
