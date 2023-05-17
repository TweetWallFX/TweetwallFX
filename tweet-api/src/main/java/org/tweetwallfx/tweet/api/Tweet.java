/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 TweetWallFX
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
import org.tweetwallfx.tweet.api.entry.EmojiTweetEntry;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.SymbolTweetEntry;
import org.tweetwallfx.tweet.api.entry.TweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public interface Tweet extends BasicEntry {

    LocalDateTime getCreatedAt();

    int getFavoriteCount();

    long getId();

    long getInReplyToTweetId();

    long getInReplyToUserId();

    String getInReplyToScreenName();

    String getLang();

    int getRetweetCount();

    /**
     * Returns the Tweet that has been retweeted. Concerns one level of
     * retweeting only.
     *
     * <p>
     * Considering the scenario that Tweet A was retweeted as Tweet B which was
     * in turn retweeted as Tweet C.
     *
     * <br>
     * <table>
     * <caption>Table describing which Tweet is returned when called on which
     * Tweet.</caption>
     * <thead>
     * <tr><td><b>Called on Tweet</b></td><td><b>Tweet returned</b></td></tr>
     * </thead>
     * <tbody>
     * <tr><td>Tweet A</td><td>null</td></tr>
     * <tr><td>Tweet B</td><td>Tweet A</td></tr>
     * <tr><td>Tweet C</td><td>Tweet B</td></tr>
     * </tbody>
     * </table>
     *
     * @return the original Tweet or {@code null} if this is not a retweet.
     */
    Tweet getRetweetedTweet();

    /**
     * Returns the Tweet that originated this Tweet. If this Tweet has not been
     * a retweet it is returned directly. Otherwise retweeted Tweet is resolved
     * and again evaluated until the evaluated Tweet is no longer a re-Tweet.
     *
     * <p>
     * Considering the scenario that Tweet A was retweeted as Tweet B which was
     * in turn retweeted as Tweet C.
     *
     * <br>
     * <table>
     * <caption>Table describing which Tweet is returned when called on which
     * Tweet.</caption>
     * <thead>
     * <tr><td><b>Called on Tweet</b></td><td><b>Tweet returned</b></td></tr>
     * </thead>
     * <tbody>
     * <tr><td>Tweet A</td><td>Tweet A</td></tr>
     * <tr><td>Tweet B</td><td>Tweet A</td></tr>
     * <tr><td>Tweet C</td><td>Tweet A</td></tr>
     * </tbody>
     * </table>
     *
     * @return the original Tweet or this Tweet if this Tweet is not a retweet.
     */
    Tweet getOriginTweet();

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
        final AtomicInteger offset = new AtomicInteger(0);

        return IntStream.range(0, codePoints.length)
                .filter(i -> codePoints[i] >= 0x1f000)
                .mapToObj(i -> {
                    final int charCount = Character.charCount(codePoints[i]);
                    final int cOffset = offset.getAndAdd(charCount - 1);
                    return new EmojiTweetEntry(new String(codePoints, i, 1), cOffset + i, charCount);
                })
                .toArray(i -> new EmojiTweetEntry[i]);
    }

    public default TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
        return new TextExtractor(this).getTextWithout(entryToRemove);
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
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

            final Set<Integer> indexesToFilterOut = filteredIndexes.boxed().collect(toCollection(TreeSet::new));
            final int[] chars = tweet.getText().chars().toArray();
            final int[] filteredChars = IntStream.range(0, chars.length)
                    .filter(i -> !indexesToFilterOut.contains(i))
                    .map(i -> chars[i])
                    .toArray();

            return new String(filteredChars, 0, filteredChars.length)
                    .replaceAll("  *", " ")
                    .trim();
        }
    }
}
