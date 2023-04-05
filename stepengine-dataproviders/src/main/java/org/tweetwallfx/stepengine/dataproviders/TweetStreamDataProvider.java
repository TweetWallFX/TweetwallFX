/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2023 TweetWallFX
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

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.tweetwallfx.util.Nullable.valueOrDefault;

/**
 * Provides an always current list of tweets based on the configured query. The
 * history length is not yet configurable.
 */
public class TweetStreamDataProvider implements DataProvider.NewTweetAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamDataProvider.class);
    private final ReadWriteLock tweetListLock = new ReentrantReadWriteLock();
    private final String searchText = Configuration.getInstance()
            .getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class)
            .query();
    private volatile Image latestTweetedImage;
    private volatile Deque<Tweet> tweets = new ArrayDeque<>();
    private final Config config;

    private TweetStreamDataProvider(final Config config) {
        this.config = config;

        LOGGER.info("Initialize tweet stream provider");
        List<Tweet> history = getLatestHistory();
        tweetListLock.writeLock().lock();
        try {
            history.forEach(this::appendTweet);
        } finally {
            tweetListLock.writeLock().unlock();
        }
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        LOGGER.info("New tweet received");
        prependTweet(tweet);
    }

    private void updateImage(final Tweet tweet) {
        Arrays.stream(tweet.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .findFirst()
                .ifPresent(mte
                        -> PhotoImageCache.INSTANCE.getCachedOrLoad(
                        mte,
                        sis -> latestTweetedImage = new Image(sis.getInputStream())));
    }

    private void appendTweet(final Tweet tweet) {
        addTweet(tweet, false);
    }

    private void prependTweet(final Tweet tweet) {
        addTweet(tweet, true);
    }

    private void addTweet(final Tweet tweet, boolean prepend) {
        if (tweet.isRetweet() && config.hideRetweets()) {
            LOGGER.info("Tweet {} is a retweet and retweets are currently not allowed", tweet.getId());
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
                    updateImage(originalTweet);
                } else {
                    tweets.addLast(originalTweet);
                }
            }

            if (tweets.size() > config.maxTweets()) {
                tweets.removeLast();
            }
        } finally {
            tweetListLock.writeLock().unlock();
        }
    }

    public Optional<Image> getLatestImage() {
        return Optional.ofNullable(latestTweetedImage);
    }

    public List<Tweet> getTweets() {
        tweetListLock.readLock().lock();

        try {
            return new ArrayList<>(this.tweets);
        } finally {
            tweetListLock.readLock().unlock();
        }
    }

    private List<Tweet> getLatestHistory() {
        LOGGER.info("Reinitialize history");
        return Tweeter.getInstance()
                .search(new TweetQuery()
                        .query(searchText)
                        .count(config.historySize()))
                .toList();
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
     *
     * <p>
     * Param {@code historySize} The number of the tweets to request from query
     * in order to fill up {@link TweetStreamDataProvider} upon initialization.
     * Defaults to {@code 50}.
     *
     * <p>
     * Param {@code maxTweets} The number of tweet to produce upon request via
     * {@link TweetStreamDataProvider#getTweets()}. Defaults to {@code 4}.
     *
     * <p>
     * Param {@code hideRetweets} Is accepting retweets into the datastore of
     * the DataProvider acceptable? Defaults to {@code false}.
     */
    public record Config(
            Integer historySize,
            Integer maxTweets,
            Boolean hideRetweets) {

        @SuppressWarnings("unused")
        public Config(
                final Integer historySize,
                final Integer maxTweets,
                final Boolean hideRetweets) {
            if (historySize < 0) {
                throw new IllegalArgumentException("property 'historySize' must not be a negative number");
            }
            this.historySize = valueOrDefault(historySize, 50);
            if (maxTweets < 0) {
                throw new IllegalArgumentException("property 'maxTweets' must not be a negative number");
            }
            this.maxTweets = valueOrDefault(maxTweets, 4);
            this.hideRetweets = valueOrDefault(hideRetweets, false);
        }
    }
}
