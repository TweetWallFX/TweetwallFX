/*
 * The MIT License
 *
 * Copyright 2018 TweetWallFX
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
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * POJO for reading Settings concerning Devoxx CFP Client.
 */
public final class CFPClientSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "devoxxCFP";
    private String baseUri;
    private String eventId;
    private String votingResultsUri;

    /**
     * Returns the Base URI from where all standard calls are executed.
     *
     * @return the Base URI from where all standard calls are executed
     */
    public String getBaseUri() {
        return Objects.requireNonNull(baseUri, "baseUri must not be null!");
    }

    /**
     * Sets the Base URI from where all standard calls are executed.
     *
     * @param baseUri the Base URI from where all standard calls are executed
     */
    public void setBaseUri(final String baseUri) {
        Objects.requireNonNull(baseUri, "baseUri must not be null!");
        this.baseUri = baseUri;
    }

    /**
     * Returns the ID of the event on the API Server.
     *
     * @return the ID of the event on the API Server
     */
    public String getEventId() {
        return Objects.requireNonNull(eventId, "eventId must not be null!");
    }

    /**
     * Sets the ID of the event on the API Server.
     *
     * @param eventId the ID of the event on the API Server
     */
    public void setEventId(final String eventId) {
        Objects.requireNonNull(eventId, "eventId must not be null!");
        this.eventId = eventId;
    }

    /**
     * Returns the Query Uri from where voting results are retrieved.
     *
     * @return the Query Uri from where voting results are retrieved
     */
    public String getVotingResultsUri() {
        return votingResultsUri;
    }

    /**
     * Sets the Query Uri from where voting results are retrieved.
     *
     * @param votingResultsUri the Query Uri from where voting results are
     * retrieved
     */
    public void setVotingResultsUri(final String votingResultsUri) {
        Objects.requireNonNull(votingResultsUri, "votingResultsUri must not be null!");
        this.votingResultsUri = votingResultsUri;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "baseUri", getBaseUri(),
                "eventId", getEventId(),
                "votingResultsUri", getVotingResultsUri()
        )) + " extends " + super.toString();
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
