/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * Provides an always current list of tweets based on the configured query. The
 * history length is not yet configurable.
 */
public class TweetStreamDataProvider implements DataProvider.NewTweetAware, DataProvider.HistoryAware {

    private static final Logger LOGGER = LogManager.getLogger(TweetStreamDataProvider.class);
    private final ReadWriteLock tweetListLock = new ReentrantReadWriteLock();
    private volatile Deque<Tweet> tweets = new ArrayDeque<>();
    private final Config config;

    private TweetStreamDataProvider(final Config config) {
        this.config = config;

        LOGGER.info("Initialize tweet stream provider");
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        LOGGER.info("New tweet received");
        addTweet(tweet, true);
    }

    @Override
    public void processHistoryTweet(final Tweet tweet) {
        LOGGER.info("New historic Tweet received: {}", tweet.getId());
        addTweet(tweet, false);
    }

    private void addTweet(final Tweet tweet, boolean prepend) {
        if (tweet.isRetweet() && config.isHideRetweets()) {
            LOGGER.info("Tweet " + tweet.getId() + " is a retweet and retweets are currently not allowed");
            return;
        }

        LOGGER.info("Add tweet {}", tweet.getId());

        tweetListLock.writeLock().lock();
        try {
            final Tweet originalTweet = tweet.getOriginTweet();

            if (tweets.stream().noneMatch(twt -> originalTweet.getId() == twt.getId()
                    && originalTweet.getUser().getScreenName().equals(twt.getUser().getScreenName()))) {
                if (prepend) {
                    tweets.addFirst(originalTweet);
                } else {
                    tweets.addLast(originalTweet);
                }
            }

            if (tweets.size() > config.getMaxTweets()) {
                tweets.removeLast();
            }
        } finally {
            tweetListLock.writeLock().unlock();
        }
    }

    public List<Tweet> getTweets() {
        tweetListLock.readLock().lock();

        try {
            return new ArrayList<>(this.tweets);
        } finally {
            tweetListLock.readLock().unlock();
        }
    }

    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public TweetStreamDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new TweetStreamDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<TweetStreamDataProvider> getDataProviderClass() {
            return TweetStreamDataProvider.class;
        }
    }

    /**
     * POJO used to configure {@link TweetStreamDataProvider}.
     */
    public static final class Config {

        /**
         * The number of tweet to produce upon request via
         * {@link TweetStreamDataProvider#getTweets()}. Defaults to {@code 4}.
         */
        private int maxTweets = 4;

        /**
         * Is accepting retweets into the datastore of the DataProvider
         * acceptable? Defaults to {@code false}.
         */
        private boolean hideRetweets = false;

        public int getMaxTweets() {
            return maxTweets;
        }

        public void setMaxTweets(final int maxTweets) {
            if (maxTweets < 0) {
                throw new IllegalArgumentException("property 'maxTweets' must not be a negative number");
            }

            this.maxTweets = maxTweets;
        }

        public boolean isHideRetweets() {
            return hideRetweets;
        }

        public void setHideRetweets(boolean hideRetweets) {
            this.hideRetweets = hideRetweets;
        }
    }
}
