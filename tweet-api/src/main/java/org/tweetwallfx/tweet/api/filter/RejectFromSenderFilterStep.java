/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 TweetWallFX
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

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * A {@link FilterStep} handling {@link Tweet}s by checking the sending
 * {@link User} {@link User#getName()} and possibly reject them.
 *
 * In case {@link User#getName()} is one of the names configured in
 * {@link Config#getUserHandles()} it is terminally rejected with
 * {@link Result#REJECTED}. Otherwise it is evaluated as
 * {@link Result#NOTHING_DEFINITE}.
 */
public class RejectFromSenderFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LogManager.getLogger(RejectFromSenderFilterStep.class);
    private final Config config;

    private RejectFromSenderFilterStep(final Config config) {
        this.config = config;
    }

    @Override
    public FilterStep.Result check(final Tweet tweet) {
        Tweet t = tweet;

        do {
            LOG.info("Tweet(id:{}): Checking for Tweet(id:{}) ...", t.getId(), tweet.getId());

            if (config.getUserHandles().contains(t.getUser().getScreenName().toLowerCase(Locale.ENGLISH))) {
                LOG.info("Tweet(id:{}): User handle for Tweet(id:{}) is blacklisted -> REJECTED", t.getId(), tweet.getId());
                return Result.REJECTED;
            }

            LOG.info("Tweet(id:{}): User handle for Tweet(id:{}) is not blacklisted", t.getId(), tweet.getId());
            t = t.getRetweetedTweet();
        } while (config.isCheckRetweeted() && null != t);

        LOG.info("Tweet(id:{}): No terminal decision found -> NOTHING_DEFINITE", tweet.getId());
        return Result.NOTHING_DEFINITE;
    }

    /**
     * Implementation of {@link FilterStep.Factory} creating
     * {@link RejectFromSenderFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<RejectFromSenderFilterStep> getFilterStepClass() {
            return RejectFromSenderFilterStep.class;
        }

        @Override
        public RejectFromSenderFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new RejectFromSenderFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link RejectFromSenderFilterStep}.
     */
    public static final class Config {

        private Set<String> userHandles = Collections.emptySet();
        private boolean checkRetweeted = false;

        /**
         * Returns the handles for the {@link User} for which any {@link Tweet}
         * send by them is automatically rejected.
         *
         * @return the handles for the {@link User} for which any {@link Tweet}
         * send by them is automatically rejected
         */
        public Set<String> getUserHandles() {
            return userHandles;
        }

        /**
         * Sets the handles for the {@link User} for which any {@link Tweet}
         * send by them is automatically accepted.
         *
         * @param userHandles the new value
         */
        public void setUserHandles(final Set<String> userHandles) {
            Objects.requireNonNull(userHandles, "userHandles must not be null!");
            this.userHandles = userHandles.stream()
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
            return createToString(this, map(
                    "checkRetweeted", isCheckRetweeted(),
                    "userHandles", getUserHandles()
            )) + " extends " + super.toString();
        }
    }
}
