/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 TweetWallFX
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

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * A {@link FilterStep} handling {@link Tweet}s by checking that the length of
 * any hashtag in the {@link Tweet} does not exceed a certain number. If it does
 * the {@link Tweet} is rejected.
 *
 * In case the length of any Hashtag exceeds the configured length from
 * {@link Config#getMaxLength()} it is terminally rejected with
 * {@link Result#REJECTED}. Otherwise it is evaluated as
 * {@link Result#NOTHING_DEFINITE}.
 */
public class MaxHashtagLengthFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LogManager.getLogger(MaxHashtagLengthFilterStep.class);
    private final Config config;

    private MaxHashtagLengthFilterStep(Config config) {
        this.config = config;
    }

    @Override
    public FilterStep.Result check(final Tweet tweet) {
        Tweet t = tweet;

        do {
            LOG.debug("Tweet(id:{}): Checking for Tweet(id:{}) ...",
                    tweet.getId(),
                    t.getId());

            final List<HashtagTweetEntry> htes = Arrays.stream(t.getHashtagEntries())
                    .filter(hte -> hte.getText().length() > config.getMaxLength())
                    .toList();

            if (!htes.isEmpty()) {
                LOG.info("Tweet(id:{}): Hashtags in Tweet(id:{}) exceed allowed length of {} -> REJECTED",
                        tweet.getId(),
                        t.getId(),
                        config.getMaxLength());
                return Result.REJECTED;
            }

            LOG.debug("Tweet(id:{}): Hashtags in Tweet(id:{}) do not exeed allowed limit",
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
     * {@link MaxHashtagLengthFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<MaxHashtagLengthFilterStep> getFilterStepClass() {
            return MaxHashtagLengthFilterStep.class;
        }

        @Override
        public MaxHashtagLengthFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new MaxHashtagLengthFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link MaxHashtagLengthFilterStep}.
     */
    public static final class Config {

        private boolean checkRetweeted = true;
        private int maxLength = 30;

        /**
         * Returns the length of a hashtag that when exceeding it willl cause
         * the {@link Tweet} to be rejected.
         *
         * @return the length of a hashtag that when exceeding it willl cause
         * the {@link Tweet} to be rejected
         */
        public int getMaxLength() {
            return maxLength;
        }

        /**
         * Sets the length of a hashtag that when exceeding it willl cause the
         * {@link Tweet} to be rejected
         *
         * @param maxLength the new value
         */
        public void setMaxLength(final int maxLength) {
            this.maxLength = maxLength;
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
            return createToString(this, map(
                    "checkRetweeted", isCheckRetweeted(),
                    "maxLength", getMaxLength()
            )) + " extends " + super.toString();
        }
    }
}
