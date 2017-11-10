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
package org.tweetwallfx.controls.dataprovider;

import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;

/**
 * A Provider of data. The type of data is implementation specific.
 *
 * @author Sven Reimers
 */
public interface DataProvider {

    /**
     * A Factory creating a {@link DataProvider}.
     */
    interface Factory {

        /**
         * Returns the class of the Provider this factory will create via
         * {@link #create(org.tweetwallfx.tweet.api.TweetStream)}.
         *
         * @return the class of the Provider this factory will create
         */
        Class<? extends DataProvider> getDataProviderClass();

        /**
         * Creates a DataProvider using the provided parameters.
         *
         * @param tweetStream A {@link TweetStream}.
         *
         * @return the created DataProvider
         */
        DataProvider create(final TweetStream tweetStream);
    }

    /**
     * Interface enabling call backs for history tweets enabling the build-up of
     * historical data.
     */
    interface HistoryAware {

        /**
         * Callback to process a historic tweet
         *
         * @param tweet a historic tweet
         */
        void processTweet(final Tweet tweet);
    }

    /**
     * Returns the name of this {@link DataProvider}.
     *
     * @return the name of this {@link DataProvider}
     */
    String getName();
}
