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
package org.tweetwallfx.tweet.impl.twitter4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.filterchain.FilterChain;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.twitter4j.config.TwitterSettings;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.v1.CursorSupport;
import twitter4j.v1.FriendsFollowersResources;
import twitter4j.v1.PagableResponseList;
import twitter4j.v1.Query;
import twitter4j.v1.QueryResult;
import twitter4j.v1.RateLimitStatus;
import twitter4j.v1.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.tweetwallfx.tweet.impl.twitter4j.TwitterOAuth.instance;
import static org.tweetwallfx.tweet.impl.twitter4j.config.TwitterSettings.CONFIG_KEY;

public class TwitterTweeter implements Tweeter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterTweeter.class);
    private static final FilterChain<Tweet> FILTER_CHAIN = FilterChain.createFilterChain(Tweet.class, "twitter");
    static final TwitterSettings TWITTER_SETTINGS = Configuration.getInstance().getConfigTyped(CONFIG_KEY, TwitterSettings.class);

    private final List<TwitterTweetStream> streamCache = new ArrayList<>();

    @Override
    public boolean isEnabled() {
        return TWITTER_SETTINGS.enabled();
    }

    @Override
    public TweetStream createTweetStream(final TweetFilterQuery tweetFilterQuery) {
        TwitterTweetStream twitterTweetStream = new TwitterTweetStream(tweetFilterQuery, FILTER_CHAIN.asPredicate());
        streamCache.add(twitterTweetStream);
        return twitterTweetStream;
    }

    @Override
    public Tweet getTweet(long tweetId) {
        try {
            return new TwitterTweet(instance().twitterV1().tweets().showStatus(tweetId));
        } catch (TwitterException ex) {
            throw new IllegalArgumentException("Error getting Status for " + tweetId, ex);
        }
    }

    @Override
    public User getUser(final String userId) {
        try {
            return new TwitterUser(instance().twitterV1().users().showUser(userId));
        } catch (TwitterException ex) {
            throw new IllegalArgumentException("Error getting User for " + userId, ex);
        }
    }

    @Override
    public Stream<User> getFriends(final User user) {
        return getFriends(user.getId());
    }

    @Override
    public Stream<User> getFriends(final String userScreenName) {
        final FriendsFollowersResources friendsFollowersResources = instance().twitterV1().friendsFollowers();
        return pagedListAsStream(
                cursorId -> friendsFollowersResources.getFriendsList(userScreenName, cursorId, 200),
                te -> new IllegalArgumentException("Error getting friends for User(screenName:" + userScreenName + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFriends(final long userId) {
        final FriendsFollowersResources friendsFollowersResources = instance().twitterV1().friendsFollowers();
        return pagedListAsStream(
                cursorId -> friendsFollowersResources.getFriendsList(userId, cursorId, 200),
                te -> new IllegalArgumentException("Error getting friends for User(id:" + userId + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFollowers(final User user) {
        return getFollowers(user.getId());
    }

    @Override
    public Stream<User> getFollowers(final String userScreenName) {
        final FriendsFollowersResources friendsFollowersResources = instance().twitterV1().friendsFollowers();
        return pagedListAsStream(
                cursorId -> friendsFollowersResources.getFollowersList(userScreenName, cursorId, 200),
                te -> new IllegalArgumentException("Error getting followers for User(screenName:" + userScreenName + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFollowers(final long userId) {
        final FriendsFollowersResources friendsFollowersResources = instance().twitterV1().friendsFollowers();
        return pagedListAsStream(
                cursorId -> friendsFollowersResources.getFollowersList(userId, cursorId, 200),
                te -> new IllegalArgumentException("Error getting followers for User(id:" + userId + ")", te),
                TwitterUser::new);
    }

    private <T extends TwitterResponse, R> Stream<R> pagedListAsStream(
            final TwitterExceptionLongFunction<PagableResponseList<T>> pageableFunction,
            final Function<TwitterException, IllegalArgumentException> exceptionConverter,
            final Function<T, R> objectConverter
    ) {
        final Iterable<R> iterable = () -> new PagedEntityIterator<>(
                pageableFunction,
                objectConverter,
                exceptionConverter);

        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Override
    public Stream<Tweet> search(final TweetQuery tweetQuery) {
        final Query query = getQuery(tweetQuery);
        final QueryResult result;

        try {
            result = instance().twitterV1().search().search(query);
        } catch (TwitterException ex) {
            LOGGER.error("Error getting QueryResult for {}", query, ex);
            return Stream.empty();
        }

        return result.getTweets().stream()
                .map(TwitterTweet::new)
                .map(Tweet.class::cast)
                .filter(FILTER_CHAIN.asPredicate());
    }

    @Override
    public Stream<Tweet> searchPaged(final TweetQuery tweetQuery, int numberOfPages) {
        final Query query = getQuery(tweetQuery);
        final Iterable<Tweet> iterable = () -> new PagedIterator(query, numberOfPages);
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(FILTER_CHAIN.asPredicate());
    }

    private static Query getQuery(final TweetQuery tweetQuery) {
        final Query query = Query.of(tweetQuery.getQuery());

        if (null != tweetQuery.getCount()) {
            query.count(tweetQuery.getCount());
        }

        if (null != tweetQuery.getLang()) {
            query.lang(tweetQuery.getLang());
        }

        if (null != tweetQuery.getLocale()) {
            query.locale(tweetQuery.getLocale());
        }

        if (null != tweetQuery.getMaxId()) {
            query.maxId(tweetQuery.getMaxId());
        }

        if (null != tweetQuery.getResultType()) {
            query.resultType(Query.ResultType.valueOf(tweetQuery.getResultType().name()));
        }

        if (null != tweetQuery.getSince()) {
            query.since(tweetQuery.getSince());
        }

        if (null != tweetQuery.getSinceId()) {
            query.sinceId(tweetQuery.getSinceId());
        }

        if (null != tweetQuery.getUntil()) {
            query.until(tweetQuery.getUntil());
        }

        return query;
    }

    private static class PagedIterator extends RateLimitIterator<Tweet> {

        private QueryResult queryResult;
        private Iterator<Status> statuses;
        private static final Logger LOGGER = LoggerFactory.getLogger("org.tweetwallfx.startup");
        private int numberOfPages;

        public PagedIterator(final Query query, int numberOfPages) {
            this.numberOfPages = --numberOfPages;
            queryNext(query);
        }

        private void queryNext(final Query query) {
            numberOfPages--;
            if (null == query) {
                statuses = null;
            } else {
                try {
                    LOGGER.trace("Querying next page: {}", query);
                    queryResult = instance().twitterV1().search().search(query);
                    if (null != queryResult) {
                        handleRateLimit(queryResult.getRateLimitStatus());
                        statuses = queryResult.getTweets().iterator();
                    }
                } catch (TwitterException ex) {
                    LOGGER.trace("Querying next page failed: {}", query, ex);
                    LOGGER.error("Error getting QueryResult for {}", query, ex);
                    queryResult = null;
                    statuses = null;
                }
            }
        }

        @Override
        public boolean hasNext() {
            if (null == statuses) {
                // no more twitter status messages
                return false;
            } else if (statuses.hasNext()) {
                // twitter status messages available
                return true;
            } else {
                if (numberOfPages == 0) {
                    return false;
                } else {
                    // query next twitter status messages page
                    queryNext(queryResult.nextQuery());
                    return null != statuses && statuses.hasNext();
                }
            }
        }

        @Override
        public Tweet next() {
            if (hasNext()) {
                return new TwitterTweet(statuses.next());
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    @Override
    public void shutdown() {
        streamCache.forEach(TwitterTweetStream::shutdown);
    }

    @FunctionalInterface
    private static interface TwitterExceptionLongFunction<R> {

        /**
         * Applies this function to the given argument.
         *
         * @param value the function argument
         * @return the function result
         */
        R apply(long value) throws TwitterException;
    }

    private abstract static class RateLimitIterator<T> implements Iterator<T> {

        protected final void handleRateLimit(final RateLimitStatus rateLimitStatus) {
            LOGGER.info("RateLimit: {}/{} resetting in {}s",
                    rateLimitStatus.getRemaining(),
                    rateLimitStatus.getLimit(),
                    rateLimitStatus.getSecondsUntilReset());

            if (TWITTER_SETTINGS.ignoreRateLimit()) {
                return;
            }

            final long delay = 500L + rateLimitStatus.getSecondsUntilReset() * 1000L;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                LOGGER.error("Sleeping for {} interrupted!", delay, ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class PagedEntityIterator<T extends TwitterResponse, R> extends RateLimitIterator<R> {
        private Iterator<T> iterator;
        private long cursorId = CursorSupport.START;
        private final TwitterExceptionLongFunction<PagableResponseList<T>> pageableFunction;
        private final Function<T, R> objectConverter;
        private final Function<TwitterException, IllegalArgumentException> exceptionConverter;
        private PagableResponseList<T> prList;

        public PagedEntityIterator(
                final TwitterExceptionLongFunction<PagableResponseList<T>> pageableFunction,
                final Function<T, R> objectConverter,
                final Function<TwitterException, IllegalArgumentException> exceptionConverter) {
            this.pageableFunction = pageableFunction;
            this.objectConverter = objectConverter;
            this.exceptionConverter = exceptionConverter;
            queryNext();
        }

        private void queryNext() {
            try {
                LOGGER.debug("Retrieving next page");
                prList = pageableFunction.apply(cursorId);
            } catch (final TwitterException ex) {
                final IllegalArgumentException re = exceptionConverter.apply(ex);
                LOGGER.error("Failed to retrieve the next pageable list", re);
                throw re;
            }

            cursorId = prList.getNextCursor();
            iterator = prList.iterator();
            handleRateLimit(prList.getRateLimitStatus());
        }

        @Override
        public boolean hasNext() {
            if (null == iterator) {
                // no more twitter status messages
                return false;
            } else if (iterator.hasNext()) {
                // twitter status messages available
                return true;
            } else {
                // query next pageable list
                queryNext();
                // check if data is available
                return null != iterator && iterator.hasNext();
            }
        }

        @Override
        public R next() {
            if (hasNext()) {
                return objectConverter.apply(iterator.next());
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
