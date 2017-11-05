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
import java.util.Comparator;
import org.tweetwallfx.tweet.api.entry.BasicEntry;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.toSet;
import java.util.stream.IntStream;
import org.tweetwallfx.tweet.api.entry.EmojiTweetEntry;
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

    boolean isTruncated();

    public default String getDisplayEnhancedText() {
        return getDisplayEnhancedTextExtractor().get();
    }

    public default TextExtractor getDisplayEnhancedTextExtractor() {
        final TextExtractor textExtractor = new TextExtractor(this);

        if (-1 != getInReplyToTweetId()) {
            Arrays.stream(getUserMentionEntries())
                    .filter(ume -> ume.getId() == getInReplyToUserId())
                    .filter(ume -> 0 == ume.getStart())
                    .findAny()
                    .ifPresent(textExtractor::getTextWithout);
        }

        textExtractor.getTextWithout(UrlTweetEntry.class);
        textExtractor.getTextWithout(MediaTweetEntry.class);

        return textExtractor;
    }

    public default EmojiTweetEntry[] getEmojiEntries() {
        final int[] codePoints = getText().codePoints().toArray();

        return IntStream.range(0, codePoints.length)
                .filter(i -> codePoints[i] >= 0x1f000)
                .mapToObj(i -> new EmojiTweetEntry(new String(codePoints, i, 1), i))
                .toArray(i -> new EmojiTweetEntry[i]);
    }

    public default TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
        return new TextExtractor(this).getTextWithout(entryToRemove);
    }

    public static final class TextExtractor {

        private final Set<TweetEntry> entriesToRemove = new TreeSet<>(Comparator.comparing(TweetEntry::getStart).reversed());
        private final Tweet tweet;

        public TextExtractor(final Tweet tweet) {
            this.tweet = tweet;
        }

        public TextExtractor getTextWithout(final TweetEntry entryToRemove) {
            if (null != entryToRemove) {
                entriesToRemove.add(entryToRemove);
            }

            return this;
        }

        public TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
            if (null != entryToRemove) {
                if (EmojiTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getEmojiEntries()));
                } else if (HashtagTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getHashtagEntries()));
                } else if (MediaTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getMediaEntries()));
                } else if (SymbolTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getSymbolEntries()));
                } else if (UrlTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getUrlEntries()));
                } else if (UserMentionTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(Arrays.asList(tweet.getUserMentionEntries()));
                }
            }

            return this;
        }

        public String get() {
            if (entriesToRemove.isEmpty()) {
                return tweet.getText();
            }

            IntStream filteredIndexes = IntStream.empty();
            for (TweetEntry tweetEntry : entriesToRemove) {
                final IntStream nextFilter;

                if (tweetEntry.getStart() == tweetEntry.getEnd()) {
                    nextFilter = IntStream.of(tweetEntry.getStart());
                } else {
                    nextFilter = IntStream.range(tweetEntry.getStart(), tweetEntry.getEnd());
                }

                filteredIndexes = IntStream.concat(filteredIndexes, nextFilter);
            }

            final Set<Integer> indexesToFilterOut = filteredIndexes.boxed().collect(toSet());
            final int[] codePoints = tweet.getText().codePoints().toArray();
            final int[] filteredCodePoints = IntStream.range(0, codePoints.length)
                    .filter(i -> !indexesToFilterOut.contains(i))
                    .map(i -> codePoints[i])
                    .toArray();

            return new String(filteredCodePoints, 0, filteredCodePoints.length)
                    .replaceAll("  *", " ")
                    .trim();
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
     * }
     */
}
