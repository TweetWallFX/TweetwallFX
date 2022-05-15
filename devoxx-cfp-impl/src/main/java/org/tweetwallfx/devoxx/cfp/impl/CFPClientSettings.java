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
package org.tweetwallfx.devoxx.cfp.impl;

import java.util.Objects;
import org.tweetwallfx.config.ConfigurationConverter;

/**
 * POJO for reading Settings concerning Devoxx CFP Client.
 *
 * <p>
 * Param {@code baseUri} the Base URI from where all standard calls are executed
 *
 * <p>
 * Param {@code eventId} the ID of the event on the API Server
 *
 * <p>
 * Param {@code votingResultsUri} the Query Uri from where voting results are
 * retrieved
 */
public record CFPClientSettings(
        String baseUri,
        String eventId,
        String votingResultsUri) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "devoxxCFP";

    public CFPClientSettings   {
        Objects.requireNonNull(baseUri, "baseUri must not be null!");
        Objects.requireNonNull(eventId, "eventId must not be null!");
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link CFPClientSettings#CONFIG_KEY} into {@link CFPClientSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return CFPClientSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return CFPClientSettings.class;
        }
    }
}
