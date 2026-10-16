/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

/**
 * Provides access to a source of {@link Tweet Tweets} and {@link User Users}.
 *
 * <p>
 * Implementations are discovered via {@link java.util.ServiceLoader}. Every
 * implementation that returns {@code true} from {@link #isEnabled()} is used. If
 * more than one implementation is enabled a {@link CompositeTweeter} combines
 * them; if none is enabled a no-op implementation is used which returns empty
 * results.
 *
 * <p>
 * Use {@link #getInstance()} to retrieve the active instance and
 * {@link #shutdownTweeter()} to release the resources held by it.
 */
public interface Tweeter {

    /**
     * Shuts the {@link Tweeter} returned by {@link #getInstance()} down, if one
     * has already been created.
     */
    static void shutdownTweeter() {
        TweeterHolder.shutdownTweeter();
    }

    /**
     * Returns the {@link Tweeter} instance to be used by the application.
     *
     * <p>
     * The instance is created lazily on first access by selecting the enabled
     * {@link Tweeter} implementations available via {@link java.util.ServiceLoader}.
     *
     * @return the active Tweeter instance
     */
    static Tweeter getInstance() {
        return TweeterHolder.instance();
    }

    /**
     * Determines if this {@link Tweeter} is enabled and thus is to be used.
     *
     * @return {@code true} if this Tweeter is enabled, otherwise {@code false}
     */
    boolean isEnabled();

    /**
     * Creates a {@link TweetStream} providing newly received Tweets matching the
     * given {@link TweetFilterQuery}.
     *
     * @param filterQuery the query defining the Tweets to receive
     *
     * @return the created TweetStream
     */
    TweetStream createTweetStream(TweetFilterQuery filterQuery);

    /**
     * Returns the {@link Tweet} with the given id.
     *
     * @param tweetId the id of the Tweet to return
     *
     * @return the Tweet with the given id or {@code null} if it cannot be
     * resolved
     */
    Tweet getTweet(final long tweetId);

    /**
     * Returns the {@link User} with the given id.
     *
     * @param userId the id of the User to return
     *
     * @return the User with the given id or {@code null} if it cannot be resolved
     */
    User getUser(final String userId);

    /**
     * Returns the friends (i.e. the users this user follows) of the given
     * {@link User}.
     *
     * @param user the User whose friends are to be returned
     *
     * @return a Stream of the friends of the given User
     */
    Stream<User> getFriends(final User user);

    /**
     * Returns the friends (i.e. the users this user follows) of the User with
     * the given screen name.
     *
     * @param userScreenName the screen name of the User whose friends are to be
     * returned
     *
     * @return a Stream of the friends of the User with the given screen name
     */
    Stream<User> getFriends(final String userScreenName);

    /**
     * Returns the friends (i.e. the users this user follows) of the User with
     * the given id.
     *
     * @param userId the id of the User whose friends are to be returned
     *
     * @return a Stream of the friends of the User with the given id
     */
    Stream<User> getFriends(final long userId);

    /**
     * Returns the followers of the given {@link User}.
     *
     * @param user the User whose followers are to be returned
     *
     * @return a Stream of the followers of the given User
     */
    Stream<User> getFollowers(final User user);

    /**
     * Returns the followers of the User with the given screen name.
     *
     * @param userScreenName the screen name of the User whose followers are to
     * be returned
     *
     * @return a Stream of the followers of the User with the given screen name
     */
    Stream<User> getFollowers(final String userScreenName);

    /**
     * Returns the followers of the User with the given id.
     *
     * @param userId the id of the User whose followers are to be returned
     *
     * @return a Stream of the followers of the User with the given id
     */
    Stream<User> getFollowers(final long userId);

    /**
     * Searches for {@link Tweet Tweets} matching the given {@link TweetQuery}.
     *
     * @param tweetQuery the query defining the Tweets to search for
     *
     * @return a Stream of the Tweets matching the given query
     */
    Stream<Tweet> search(final TweetQuery tweetQuery);

    /**
     * Searches for {@link Tweet Tweets} matching the given {@link TweetQuery}
     * paging through up to the given number of result pages.
     *
     * @param tweetQuery the query defining the Tweets to search for
     * @param numberOfPages the maximum number of result pages to retrieve
     *
     * @return a Stream of the Tweets matching the given query
     */
    Stream<Tweet> searchPaged(final TweetQuery tweetQuery, int numberOfPages);

    /**
     * Shuts this {@link Tweeter} down releasing any held resources.
     */
    void shutdown();
}
