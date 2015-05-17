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

import java.util.Arrays;
import java.util.Collection;
import org.tweetwallfx.tweet.api.entry.BasicEntry;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Consumer;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.SymbolTweetEntry;
import org.tweetwallfx.tweet.api.entry.TweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

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

    public default String getTextWithout(final Class<? extends TweetEntry>... entriesToRemove) {
        if (null == entriesToRemove || 0 == entriesToRemove.length) {
            return getText();
        }

        final Collection<Class<? extends TweetEntry>> entriesToRemoveCollection
                = new HashSet<>(Arrays.asList(entriesToRemove));

        entriesToRemoveCollection.remove(null);

        if (entriesToRemoveCollection.isEmpty()) {
            return getText();
        }

        final StringBuilder sb = new StringBuilder(getText());
        final Consumer<TweetEntry> consumer = entry -> {
            for (int i = entry.getStart(); i < entry.getEnd(); i++) {
                sb.setCharAt(i, ' ');
            }
        };

        if (entriesToRemoveCollection.contains(HashtagTweetEntry.class)) {
            Arrays.stream(getHashtagEntries()).forEach(consumer);
        }

        if (entriesToRemoveCollection.contains(MediaTweetEntry.class)) {
            Arrays.stream(getMediaEntries()).forEach(consumer);
        }

        if (entriesToRemoveCollection.contains(SymbolTweetEntry.class)) {
            Arrays.stream(getSymbolEntries()).forEach(consumer);
        }

        if (entriesToRemoveCollection.contains(UrlTweetEntry.class)) {
            Arrays.stream(getUrlEntries()).forEach(consumer);
        }

        if (entriesToRemoveCollection.contains(UserMentionTweetEntry.class)) {
            Arrays.stream(getUserMentionEntries()).forEach(consumer);
        }

        return sb.toString().replaceAll("  *", " ");
    }

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
