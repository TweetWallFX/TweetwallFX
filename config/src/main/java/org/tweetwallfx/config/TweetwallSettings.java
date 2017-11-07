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

import java.util.Objects;
import static org.tweetwall.util.ToString.*;

/**
 * POJO for reading Settings concerning the Tweetwall itself.
 */
public final class TweetwallSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "tweetwall";
    private String title;
    private String stylesheetResource;
    private String stylesheetFile;
    private String query;

    /**
     * Return the title of the Tweetwall.
     *
     * @return the title
     */
    public String getTitle() {
        return Objects.requireNonNull(title, "title must not be null!");
    }

    /**
     * Sets the title of the Tweetwall.
     *
     * @param title the new title
     */
    public void setTitle(final String title) {
        this.title = Objects.requireNonNull(title, "title must not be null!");
    }

    /**
     * Returns the resource path containing stylesheet to be read from the
     * Classpath.
     *
     * @return the resource path containing stylesheet to be read from the
     * Classpath
     */
    public String getStylesheetResource() {
        return stylesheetResource;
    }

    /**
     * Sets the resource path containing stylesheet to be read from the
     * Classpath.
     *
     * @param stylesheetResource the resource path containing stylesheet to be
     * read from the Classpath
     */
    public void setStylesheetResource(final String stylesheetResource) {
        this.stylesheetResource = stylesheetResource;
    }

    /**
     * Returns the resource path containing stylesheet to be read from
     * the filesystem.
     *
     * @return the resource path containing stylesheet to be read from
     * the filesystem
     */
    public String getStylesheetFile() {
        return stylesheetFile;
    }

    /**
     * Sets the resource path containing stylesheet to be read from
     * the filesystem.
     *
     * @param stylesheetFile the resource path containing stylesheet to be read from
     * the filesystem
     */
    public void setStylesheetFile(final String stylesheetFile) {
        this.stylesheetFile = stylesheetFile;
    }

    /**
     * Returns the Query String that is to provide Tweets for this Tweetwall.
     *
     * @return the Query String that is to provide Tweets for this Tweetwall
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the Query String that is to provide Tweets for this Tweetwall.
     *
     * @param query the Query String that is to provide Tweets for this
     * Tweetwall
     */
    public void setQuery(final String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "title", getTitle(),
                "stylesheetResource", getStylesheetResource(),
                "stylesheetFile", getStylesheetFile(),
                "query", getQuery()
        )) + " extends " + super.toString();
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
