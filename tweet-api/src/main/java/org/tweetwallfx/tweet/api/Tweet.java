/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
package org.tweetwallfx.tweet.api;

import org.tweetwallfx.tweet.api.entry.BasicEntry;
import java.util.Date;

public interface Tweet extends BasicEntry {

    Date getCreatedAt();

    int getFavoriteCount();

    long getId();

    long getInReplyToTweetId();

    long getInReplyToUserId();

    String getInReplyToScreenName();

    String getLang();

    int getRetweetCount();

    String getText();

    User getUser();

    boolean isRetweet();
    /**
     * Available but not implemented. {@code
     *
     * long         getCurrentUserRetweetId();
     * long[]       getContributors();
     * GeoLocation  getGeoLocation();
     * Place        getPlace();
     * Tweet        getRetweetedStatus();
     * Scopes       getScopes();
     * String       getSource();
     * String[]     getWithheldInCountries();
     * boolean      isRetweetedByMe();
     * boolean      isPossiblySensitive();
     * boolean      isFavorited();
     * boolean      isRetweeted();
     * boolean      isTruncated();
     * }
     */
}
