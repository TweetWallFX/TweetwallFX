/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwallfx.devoxx2017be.dataprovider;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

/**
 * Provides an always current list of tweets based on the configured query. The
 * history length is not yet configurable.
 *
 * @author Sven Reimers
 */
public class TweetStreamDataProvider implements DataProvider.NewTweetAware {

    private static final Logger LOGGER = LogManager.getLogger(TweetStreamDataProvider.class);
    private static final int HISTORY_SIZE = 25;
    private final ReadWriteLock tweetListLock = new ReentrantReadWriteLock();
    private volatile Deque<Tweet> tweets = new ArrayDeque<>();
    private volatile Image latestTweetedImage = null;
    private final String searchText = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class).getQuery();

    private TweetStreamDataProvider() {
        List<Tweet> history = getLatestHistory();
        tweetListLock.writeLock().lock();
        try {
            history.forEach(this::addTweet);
        } finally {
            tweetListLock.writeLock().unlock();
        }
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        LOGGER.info("new Tweet received");
        addTweet(tweet);
    }

    private void updateImage(final Tweet tweet) {
        if (tweet.getUser().getFollowersCount() > 50) {
            Arrays.stream(tweet.getMediaEntries())
                    .filter(MediaTweetEntryType.photo::isType)
                    .findFirst().ifPresent(me -> {
                        String url;
                        switch (me.getSizes().keySet().stream().max(Comparator.naturalOrder()).get()) {
                            case 0:
                                url = me.getMediaUrl() + ":thumb";
                                break;
                            case 1:
                                url = me.getMediaUrl() + ":small";
                                break;
                            case 2:
                                url = me.getMediaUrl() + ":medium";
                                break;
                            case 3:
                                url = me.getMediaUrl() + ":large";
                                break;
                            default:
                                throw new RuntimeException("Illegal value");
                        }
                        latestTweetedImage = new Image(url);
                    });
        }
    }

    private void addTweet(final Tweet tweet) {
        if (tweet.getUser().getFollowersCount() > 50) {
            tweetListLock.writeLock().lock();
            try {
                if (!tweet.getUser().getScreenName().equals("1120blackfriday")) {
                    if (tweet.isRetweet()) {
                        Tweet originalTweet = tweet.getRetweetedTweet();
                        if (tweets.stream().noneMatch(twt -> originalTweet.getId() == twt.getId())) {
                            tweets.addFirst(originalTweet);
                            updateImage(originalTweet);
                        }
                    } else {
                        tweets.addFirst(tweet);
                        updateImage(tweet);
                    }
                    if (tweets.size() > 4) {
                        tweets.removeLast();
                    }
                }
            } finally {
                tweetListLock.writeLock().unlock();
            }
        }
    }

    public Optional<Image> getLatestImage() {
        return Optional.ofNullable(latestTweetedImage);
    }

    public List<Tweet> getTweets() {
        try {
            tweetListLock.readLock().lock();
            return new ArrayList<>(this.tweets);
        } finally {
            tweetListLock.readLock().unlock();
        }
    }

    private List<Tweet> getLatestHistory() {
        LOGGER.info("Reinit the history");
        return Tweeter.getInstance().search(new TweetQuery()
                .query(searchText)
                .count(HISTORY_SIZE)).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "TweetStream";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public TweetStreamDataProvider create() {
            return new TweetStreamDataProvider();
        }

        @Override
        public Class<TweetStreamDataProvider> getDataProviderClass() {
            return TweetStreamDataProvider.class;
        }
    }
}
