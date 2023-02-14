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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class CompositeTweeter implements Tweeter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTweeter.class);

    private final List<Tweeter> tweeters;

    CompositeTweeter(List<Tweeter> tweeters) {
        this.tweeters = tweeters;
    }

    <T> T getFirst(Function<Tweeter, T> action) {
        for (Tweeter tweeter : tweeters) {
            T result = action.apply(tweeter);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    <T> Stream<T> flatMap(Function<Tweeter, Stream<T>> action) {
        Stream<T> result = Stream.empty();
        for (Tweeter tweeter : tweeters) {
            result = Stream.concat(result, action.apply(tweeter));
        }
        return result;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public TweetStream createTweetStream(TweetFilterQuery filterQuery) {
        final CompositeTweetStream compositeTweetStream = new CompositeTweetStream();
        for (Tweeter tweeter : tweeters) {
            try {
                tweeter.createTweetStream(filterQuery).onTweet(compositeTweetStream);
            } catch (Throwable t) {
                LOGGER.error("Failed create tweet with query {} on tweeter {}", filterQuery, tweeter, t);
            }
        }
        return compositeTweetStream;
    }

    @Override
    public Tweet getTweet(long tweetId) {
        return getFirst(tweeter -> tweeter.getTweet(tweetId));
    }

    @Override
    public User getUser(String userId) {
        return getFirst(tweeter -> tweeter.getUser(userId));
    }

    @Override
    public Stream<User> getFriends(User user) {
        return flatMap(tweeter -> tweeter.getFollowers(user));
    }

    @Override
    public Stream<User> getFriends(String userScreenName) {
        return flatMap(tweeter -> tweeter.getFriends(userScreenName));
    }

    @Override
    public Stream<User> getFriends(long userId) {
        return flatMap(tweeter -> tweeter.getFriends(userId));
    }

    @Override
    public Stream<User> getFollowers(User user) {
        return flatMap(tweeter -> tweeter.getFollowers(user));
    }

    @Override
    public Stream<User> getFollowers(String userScreenName) {
        return flatMap(tweeter -> tweeter.getFollowers(userScreenName));
    }

    @Override
    public Stream<User> getFollowers(long userId) {
        return flatMap(tweeter -> tweeter.getFollowers(userId));
    }

    @Override
    public Stream<Tweet> search(TweetQuery tweetQuery) {
        return flatMap(tweeter -> tweeter.search(tweetQuery));
    }

    @Override
    public Stream<Tweet> searchPaged(TweetQuery tweetQuery, int numberOfPages) {
        return flatMap(tweeter -> tweeter.searchPaged(tweetQuery, numberOfPages));
    }

    @Override
    public void shutdown() {
        for (Tweeter tweeter : tweeters) {
            try {
                tweeter.shutdown();
            } catch (Throwable t) {
                LOGGER.error("Failed to shutdown tweeter {}", tweeter, t);
            }
        }
    }

    static final class CompositeTweetStream implements TweetStream, Consumer<Tweet> {
        private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTweetStream.class);

        private final List<Consumer<Tweet>> tweetConsumerList = new CopyOnWriteArrayList<>();

        @Override
        public void onTweet(Consumer<Tweet> tweetConsumer) {
            synchronized (this) {
                LOGGER.info("Adding tweetConsumer: {}", tweetConsumer);
                this.tweetConsumerList.add(tweetConsumer);
                LOGGER.info("List of tweetConsumers is now: {}", tweetConsumerList);
            }
        }

        @Override
        public void accept(Tweet tweet) {
            synchronized (this) {
                LOGGER.info("Redispatching new received tweet to {}", tweetConsumerList);
                tweetConsumerList.stream().forEach(consumer -> consumer.accept(tweet));
            }
        }
    }
}
