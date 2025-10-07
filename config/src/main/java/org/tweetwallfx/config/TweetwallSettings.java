/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 TweetWallFX
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
import java.util.Set;
import static org.tweetwallfx.util.Nullable.nullable;

/**
 * POJO for reading Settings concerning the Tweetwall itself.
 *
 * <p>
 * Param {@code title} the title of the Tweetwall
 *
 * <p>
 * Param {@code stylesheetResource} the resource path containing stylesheet to
 * be read from the Classpath
 *
 * <p>
 * Param {@code stylesheetFile} the resource path containing stylesheet to be
 * read from the filesystem
 *
 * <p>
 * Param {@code query} the Query String that is to provide Tweets for this
 * Tweetwall
 *
 * <p>
 * Param {@code additionalStopWords} the additional stop words to employ
 */
public record TweetwallSettings(
        String title,
        String stylesheetResource,
        String stylesheetFile,
        String query,
        Set<String> additionalStopWords) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "tweetwall";

    public TweetwallSettings {
        Objects.requireNonNull(title, "title must not be null!");
        Objects.requireNonNull(query, "query must not be null!");
        additionalStopWords = nullable(additionalStopWords);
    }

    @Override
    public Set<String> additionalStopWords() {
        return nullable(additionalStopWords);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link TweetwallSettings#CONFIG_KEY} into {@link TweetwallSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return TweetwallSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return TweetwallSettings.class;
        }
    }
}
