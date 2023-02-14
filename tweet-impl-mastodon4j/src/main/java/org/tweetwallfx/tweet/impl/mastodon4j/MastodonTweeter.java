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
package org.tweetwallfx.tweet.impl.mastodon4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.mastodon4j.config.MastodonSettings;

import java.util.stream.Stream;

import static org.tweetwallfx.tweet.impl.mastodon4j.config.MastodonSettings.CONFIG_KEY;

public class MastodonTweeter implements Tweeter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MastodonTweeter.class);
    static final MastodonSettings MASTODON_SETTINGS = Configuration.getInstance().getConfigTyped(CONFIG_KEY, MastodonSettings.class);

    public MastodonTweeter() {
        LOGGER.debug("Initializing with configuration: {}", MASTODON_SETTINGS);
    }

    @Override
    public boolean isEnabled() {
        return MASTODON_SETTINGS.enabled();
    }

    @Override
    public TweetStream createTweetStream(TweetFilterQuery filterQuery) {
        LOGGER.debug("createTweetStream({})", filterQuery);
        return tweetConsumer -> LOGGER.debug("onTweet({})", tweetConsumer);
    }

    @Override
    public Tweet getTweet(long tweetId) {
        LOGGER.debug("getTweet({})", tweetId);
        return null;
    }

    @Override
    public User getUser(String userId) {
        LOGGER.debug("getUser({})", userId);
        return null;
    }

    @Override
    public Stream<User> getFriends(User user) {
        LOGGER.debug("getFriends({})", user);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(String userScreenName) {
        LOGGER.debug("getFriends({})", userScreenName);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(long userId) {
        LOGGER.debug("getFriends({})", userId);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(User user) {
        LOGGER.debug("getFollowers({})", user);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(String userScreenName) {
        LOGGER.debug("getFollowers({})", userScreenName);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(long userId) {
        LOGGER.debug("getFollowers({})", userId);
        return Stream.empty();
    }

    @Override
    public Stream<Tweet> search(TweetQuery tweetQuery) {
        LOGGER.debug("search({})", tweetQuery);
        return Stream.empty();
    }

    @Override
    public Stream<Tweet> searchPaged(TweetQuery tweetQuery, int numberOfPages) {
        LOGGER.debug("searchPaged({}, {})", tweetQuery, numberOfPages);
        return Stream.empty();
    }

    @Override
    public void shutdown() {
        LOGGER.debug("shutdown()");
    }
}
