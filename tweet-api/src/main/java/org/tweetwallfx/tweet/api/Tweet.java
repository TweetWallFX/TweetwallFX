/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

/**
 * A Tweet as provided by a {@link Tweeter}.
 *
 * <p>
 * Beside the plain {@link #getText() text} of the Tweet this interface provides
 * access to the {@link User} that created it as well as metadata such as the
 * time of creation and the number of retweets/likes.
 */
public interface Tweet extends BasicEntry {

    /**
     * Returns the date and time at which this Tweet was created.
     *
     * @return the creation date and time of this Tweet
     */
    LocalDateTime getCreatedAt();

    /**
     * Returns the number of times this Tweet has been marked as favorite.
     *
     * @return the favorite count of this Tweet
     */
    int getFavoriteCount();

    /**
     * Returns the unique id of this Tweet.
     *
     * @return the id of this Tweet
     */
    long getId();

    /**
     * Returns the id of the Tweet this Tweet is a reply to.
     *
     * @return the id of the Tweet this Tweet replies to or {@code -1} if this
     * Tweet is not a reply
     */
    long getInReplyToTweetId();

    /**
     * Returns the id of the User this Tweet is a reply to.
     *
     * @return the id of the User this Tweet replies to or {@code -1} if this
     * Tweet is not a reply
     */
    long getInReplyToUserId();

    /**
     * Returns the screen name of the User this Tweet is a reply to.
     *
     * @return the screen name of the User this Tweet replies to or {@code null}
     * if this Tweet is not a reply
     */
    String getInReplyToScreenName();

    /**
     * Returns the language of this Tweet.
     *
     * @return the language of this Tweet
     */
    String getLang();

    /**
     * Returns the number of times this Tweet has been retweeted.
     *
     * @return the retweet count of this Tweet
     */
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

    /**
     * Returns the text of this Tweet.
     *
     * @return the text of this Tweet
     */
    String getText();

    /**
     * Returns the {@link User} that created this Tweet.
     *
     * @return the User that created this Tweet
     */
    User getUser();

    /**
     * Determines if this Tweet is a retweet.
     *
     * @return {@code true} if this Tweet is a retweet, otherwise {@code false}
     */
    boolean isRetweet();

    /**
     * Determines if the text of this Tweet has been truncated.
     *
     * @return {@code true} if the text of this Tweet has been truncated,
     * otherwise {@code false}
     */
    boolean isTruncated();

    /**
     * Returns the display text of this Tweet with reply mentions, URLs and
     * media entries removed.
     *
     * @return the enhanced display text of this Tweet
     *
     * @see #getDisplayEnhancedTextExtractor()
     */
    public default String getDisplayEnhancedText() {
        return getDisplayEnhancedTextExtractor().get();
    }

    /**
     * Returns a {@link TextExtractor} producing the display text of this Tweet
     * with an initial reply mention, URLs and media entries removed.
     *
     * @return a TextExtractor for the enhanced display text of this Tweet
     */
    public default TextExtractor getDisplayEnhancedTextExtractor() {
        final TextExtractor textExtractor = new TextExtractor(this);

        if (-1 != getInReplyToTweetId()) {
            getUserMentionEntries().stream()
                    .filter(ume -> ume.getId() == getInReplyToUserId())
                    .filter(ume -> 0 == ume.getStart())
                    .findAny()
                    .ifPresent(textExtractor::getTextWithout);
        }

        textExtractor.getTextWithout(UrlTweetEntry.class);
        textExtractor.getTextWithout(MediaTweetEntry.class);

        return textExtractor;
    }

    /**
     * Returns the emoji contained within the text of this Tweet.
     *
     * @return the list of emoji entries of this Tweet
     */
    public default List<EmojiTweetEntry> getEmojiEntries() {
        final int[] codePoints = getText().codePoints().toArray();
        final AtomicInteger offset = new AtomicInteger(0);

        return IntStream.range(0, codePoints.length)
                .filter(Character::isEmoji)
                .mapToObj(i -> {
                    final int charCount = Character.charCount(codePoints[i]);
                    final int cOffset = offset.getAndAdd(charCount - 1);
                    return new EmojiTweetEntry(new String(codePoints, i, 1), cOffset + i, charCount);
                })
                .toList();
    }

    /**
     * Returns the text of this Tweet with all entries of the given type removed.
     *
     * @param entryToRemove the type of entries to remove from the text
     *
     * @return a TextExtractor for the text of this Tweet without the given entry
     * type
     */
    public default TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
        return new TextExtractor(this).getTextWithout(entryToRemove);
    }

    /**
     * Helper producing the text of a {@link Tweet} with specific entries removed.
     */
    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class TextExtractor {

        private final Set<TweetEntry> entriesToRemove = new TreeSet<>(Comparator.comparingInt(TweetEntry::getStart).reversed());
        private final Tweet tweet;

        /**
         * Creates a new TextExtractor for the given Tweet.
         *
         * @param tweet the Tweet whose text is to be extracted
         */
        public TextExtractor(final Tweet tweet) {
            this.tweet = tweet;
        }

        /**
         * Registers a specific entry to be removed from the text.
         *
         * @param entryToRemove the entry to remove from the text
         *
         * @return this TextExtractor instance
         */
        public TextExtractor getTextWithout(final TweetEntry entryToRemove) {
            if (null != entryToRemove) {
                entriesToRemove.add(entryToRemove);
            }

            return this;
        }

        /**
         * Registers all entries of the given type to be removed from the text.
         *
         * @param entryToRemove the type of entries to remove from the text
         *
         * @return this TextExtractor instance
         */
        public TextExtractor getTextWithout(final Class<? extends TweetEntry> entryToRemove) {
            if (null != entryToRemove) {
                if (EmojiTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getEmojiEntries());
                } else if (HashtagTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getHashtagEntries());
                } else if (MediaTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getMediaEntries());
                } else if (SymbolTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getSymbolEntries());
                } else if (UrlTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getUrlEntries());
                } else if (UserMentionTweetEntry.class.isAssignableFrom(entryToRemove)) {
                    entriesToRemove.addAll(tweet.getUserMentionEntries());
                }
            }

            return this;
        }

        /**
         * Returns the text of the Tweet with all registered entries removed.
         *
         * @return the extracted text
         */
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
