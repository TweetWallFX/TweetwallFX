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

import java.util.stream.Stream;

public interface Tweeter {

    static Tweeter getInstance() {
        return TweeterHolder.instance();
    }

    boolean isEnabled();

    TweetStream createTweetStream(TweetFilterQuery filterQuery);

    Tweet getTweet(final long tweetId);

    User getUser(final String userId);

    Stream<User> getFriends(final User user);

    Stream<User> getFriends(final String userScreenName);

    Stream<User> getFriends(final long userId);

    Stream<User> getFollowers(final User user);

    Stream<User> getFollowers(final String userScreenName);

    Stream<User> getFollowers(final long userId);

    Stream<Tweet> search(final TweetQuery tweetQuery);

    Stream<Tweet> searchPaged(final TweetQuery tweetQuery, int numberOfPages);

    void shutdown();
}
