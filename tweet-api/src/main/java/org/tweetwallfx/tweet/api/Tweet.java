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
import org.tweetwallfx.tweet.api.entry.BasicEntry;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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

    Tweet getRetweetedTweet();

    String getText();

    User getUser();

    boolean isRetweet();

    public default TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
        return new TextExtractor(this).getTextWithout(entryToRemove);
    }

    public static final class TextExtractor {

        private final Set<Class<? extends TweetEntry>> entriesToRemove = new HashSet<>();
        private final Tweet tweet;

        public TextExtractor(final Tweet tweet) {
            this.tweet = tweet;
        }

        public TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
            if (null != entryToRemove) {
                entriesToRemove.add(entryToRemove);
            }

            return this;
        }

        public String get() {
            if (entriesToRemove.isEmpty()) {
                return tweet.getText();
            }

            final StringBuilder sb = new StringBuilder(tweet.getText());
            final Consumer<TweetEntry> consumer = entry -> {
                for (int i = entry.getStart(); i < entry.getEnd(); i++) {
                    sb.setCharAt(i, ' ');
                }
            };

            if (entriesToRemove.contains(HashtagTweetEntry.class)) {
                Arrays.stream(tweet.getHashtagEntries()).forEach(consumer);
            }

            if (entriesToRemove.contains(MediaTweetEntry.class)) {
                Arrays.stream(tweet.getMediaEntries()).forEach(consumer);
            }

            if (entriesToRemove.contains(SymbolTweetEntry.class)) {
                Arrays.stream(tweet.getSymbolEntries()).forEach(consumer);
            }

            if (entriesToRemove.contains(UrlTweetEntry.class)) {
                Arrays.stream(tweet.getUrlEntries()).forEach(consumer);
            }

            if (entriesToRemove.contains(UserMentionTweetEntry.class)) {
                Arrays.stream(tweet.getUserMentionEntries()).forEach(consumer);
            }

            return sb.toString().replaceAll("  *", " ");
        }
    }

    /**
     * Available but not implemented. {@code
     *
     * long         getCurrentUserRetweetId();
     * long[]       getContributors();
     * GeoLocation  getGeoLocation();
     * Place        getPlace();
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
