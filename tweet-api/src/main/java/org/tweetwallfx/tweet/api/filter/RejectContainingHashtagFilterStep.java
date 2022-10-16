/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2022 TweetWallFX
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
package org.tweetwallfx.tweet.api.filter;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import static org.tweetwallfx.util.ToString.createToString;

/**
 * A {@link FilterStep} handling {@link Tweet}s by checking the contained
 * {@link HashtagTweetEntry HashtagTweetEntries} {@link HashtagTweetEntry#getText()}
 * and rejects it in case it has a specific value.
 *
 * In case {@link HashtagTweetEntry#getText()} has one of the configured valies
 * in {@link Config#getHashtags()} it is terminally rejected with
 * {@link Result#REJECTED}. Otherwise it is evaluated as
 * {@link Result#NOTHING_DEFINITE}.
 */
public class RejectContainingHashtagFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LoggerFactory.getLogger(RejectFromSenderFilterStep.class);
    private final Config config;

    private RejectContainingHashtagFilterStep(final Config config) {
        this.config = config;
    }

    @Override
    public FilterStep.Result check(final Tweet tweet) {
        Tweet t = tweet;

        do {
            LOG.debug("Tweet(id:{}): Checking for Tweet(id:{}) ...",
                    tweet.getId(),
                    t.getId());

            for (final HashtagTweetEntry hashtagEntry : t.getHashtagEntries()) {
                if (config.getHashtags().contains(hashtagEntry.getText().toLowerCase(Locale.ENGLISH))) {
                    LOG.info("Tweet(id:{}): Hashtag {} for Tweet(id:{}) is blacklisted -> REJECTED",
                            tweet.getId(),
                            hashtagEntry.getText(),
                            t.getId());
                    return Result.REJECTED;
                }
            }

            LOG.debug("Tweet(id:{}): none of the Hashtags in Tweet(id:{}) is blacklisted",
                    tweet.getId(),
                    t.getId());
            t = t.getRetweetedTweet();
        } while (config.isCheckRetweeted() && null != t);

        LOG.debug("Tweet(id:{}): No terminal decision found -> NOTHING_DEFINITE",
                tweet.getId());
        return Result.NOTHING_DEFINITE;
    }

    /**
     * Implementation of {@link FilterStep.Factory} creating
     * {@link RejectContainingHashtagFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<RejectContainingHashtagFilterStep> getFilterStepClass() {
            return RejectContainingHashtagFilterStep.class;
        }

        @Override
        public RejectContainingHashtagFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new RejectContainingHashtagFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link RejectContainingHashtagFilterStep}.
     */
    public static final class Config {

        private Set<String> hashtags = Set.of();
        private boolean checkRetweeted = false;

        /**
         * Returns the hashtags for the {@link Tweet} for which any
         * {@link Tweet} containing any of them is automatically rejected.
         *
         * @return the hashtags for the {@link Tweet} for which any
         * {@link Tweet} containing any of them is automatically rejected
         */
        public Set<String> getHashtags() {
            return Set.copyOf(hashtags);
        }

        /**
         * Sets the hashtags for the {@link Tweet} for which any {@link Tweet}
         * containing any of them is automatically rejected.
         *
         * @param hashtags the new value
         */
        public void setHashtags(final Set<String> hashtags) {
            Objects.requireNonNull(hashtags, "hashtags must not be null!");
            this.hashtags = hashtags.stream()
                    .map(s -> s.toLowerCase(Locale.ENGLISH))
                    .collect(Collectors.toSet());
        }

        /**
         * Returns a boolean flag controlling whether for a retweet the
         * retweeted Tweet is also checked.
         *
         * @return boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked
         */
        public boolean isCheckRetweeted() {
            return checkRetweeted;
        }

        /**
         * Sets the boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked.
         *
         * @param checkRetweeted the new value
         */
        public void setCheckRetweeted(final boolean checkRetweeted) {
            this.checkRetweeted = checkRetweeted;
        }

        @Override
        public String toString() {
            return createToString(this, Map.of(
                    "checkRetweeted", isCheckRetweeted(),
                    "hashtags", getHashtags()
            ), super.toString());
        }
    }
}
