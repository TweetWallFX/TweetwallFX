/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 TweetWallFX
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

import java.util.stream.Stream;

final class NoOpTweeter implements Tweeter {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public TweetStream createTweetStream(TweetFilterQuery filterQuery) {
        return consumer -> {
            // no op
        };
    }

    @Override
    public Tweet getTweet(long tweetId) {
        return null;
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public Stream<User> getFriends(User user) {
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(String userScreenName) {
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(long userId) {
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(User user) {
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(String userScreenName) {
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(long userId) {
        return Stream.empty();
    }

    @Override
    public Stream<Tweet> search(TweetQuery tweetQuery) {
        return Stream.empty();
    }

    @Override
    public Stream<Tweet> searchPaged(TweetQuery tweetQuery, int numberOfPages) {
        return Stream.empty();
    }

    @Override
    public void shutdown() {
        // no op
    }
}
