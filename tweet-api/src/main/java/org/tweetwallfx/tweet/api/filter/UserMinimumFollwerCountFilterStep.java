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

import java.util.Objects;
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
 * {@link User} {@link User#getFollowersCount()}.
 *
 * In case {@link User#getFollowersCount()} is less than the amount configured
 * in {@link Config#getCount()} it is terminally rejected with
 * {@link Result#REJECTED}. Otherwise it is evaluated as
 * {@link Result#NOTHING_DEFINITE}.
 */
public class UserMinimumFollwerCountFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LogManager.getLogger(UserMinimumFollwerCountFilterStep.class);
    private final Config config;

    private UserMinimumFollwerCountFilterStep(final Config config) {
        this.config = config;
    }

    @Override
    public Result check(final Tweet tweet) {
        if (config.getCount() <= tweet.getUser().getFollowersCount()) {
            LOG.debug("Tweet(id:{}): No terminal decision found -> NOTHING_DEFINITE",
                    tweet.getId());
            return Result.NOTHING_DEFINITE;
        } else {
            LOG.info("Tweet(id:{}): Too few followers (have: {}; need: {}) -> REJECTED",
                    tweet.getId(),
                    tweet.getUser().getFollowersCount(),
                    config.getCount());
            return Result.REJECTED;
        }
    }

    /**
     * Implementation of {@link FilterStep.Factory} creating
     * {@link UserMinimumFollwerCountFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<UserMinimumFollwerCountFilterStep> getFilterStepClass() {
            return UserMinimumFollwerCountFilterStep.class;
        }

        @Override
        public UserMinimumFollwerCountFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new UserMinimumFollwerCountFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link UserMinimumFollwerCountFilterStep}.
     */
    public static final class Config {

        private Integer count;

        /**
         * Returns the number of followers a {@link User} shall have at a
         * minimum in order to not be terminally rejected.
         *
         * @return the number of followers a {@link User} shall have at a
         * minimum in order to not be terminally rejected
         */
        public Integer getCount() {
            return count;
        }

        /**
         * Sets the number of followers a {@link User} shall have at a minimum
         * in order to not be terminally rejected.
         *
         * @param count the new value
         */
        public void setCount(final Integer count) {
            Objects.requireNonNull(count, "count must not be null!");
            this.count = count;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "count", getCount()
            )) + " extends " + super.toString();
        }
    }
}
