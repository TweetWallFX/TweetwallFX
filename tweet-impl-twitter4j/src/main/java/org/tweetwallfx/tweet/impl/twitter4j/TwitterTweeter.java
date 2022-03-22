/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 TweetWallFX
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.filterchain.FilterChain;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.api.config.TwitterSettings;
import twitter4j.CursorSupport;
import twitter4j.PagableResponseList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterResponse;
import twitter4j.conf.Configuration;

public class TwitterTweeter extends Tweeter {

    private static final Logger LOGGER = LogManager.getLogger(TwitterTweeter.class);
    private static final FilterChain<Tweet> FILTER_CHAIN = FilterChain.createFilterChain(Tweet.class, "twitter");
    private final List<TwitterTweetStream> streamCache = new ArrayList<>();

    private static Twitter getTwitter() {
        return new TwitterFactory(TwitterOAuth.getConfiguration()).getInstance();
    }

    @Override
    public TweetStream createTweetStream(final TweetFilterQuery tweetFilterQuery) {
        TwitterTweetStream twitterTweetStream = new TwitterTweetStream(tweetFilterQuery, FILTER_CHAIN.asPredicate());
        streamCache.add(twitterTweetStream);
        return twitterTweetStream;
    }

    @Override
    public Tweet getTweet(long tweetId) {
        final Twitter twitter = getTwitter();
        try {
            return new TwitterTweet(twitter.showStatus(tweetId));
        } catch (TwitterException ex) {
            throw new IllegalArgumentException("Error getting Status for " + tweetId, ex);
        }
    }

    @Override
    public User getUser(final String userId) {
        final Twitter twitter = getTwitter();

        try {
            return new TwitterUser(twitter.showUser(userId));
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
        final Twitter twitter = getTwitter();

        return pagedListAsStream(
                cursorId -> twitter.getFriendsList(userScreenName, cursorId, 200),
                te -> new IllegalArgumentException("Error getting friends for User(screenName:" + userScreenName + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFriends(final long userId) {
        final Twitter twitter = getTwitter();

        return pagedListAsStream(
                cursorId -> twitter.getFriendsList(userId, cursorId, 200),
                te -> new IllegalArgumentException("Error getting friends for User(id:" + userId + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFollowers(final User user) {
        return getFollowers(user.getId());
    }

    @Override
    public Stream<User> getFollowers(final String userScreenName) {
        final Twitter twitter = getTwitter();

        return pagedListAsStream(
                cursorId -> twitter.getFollowersList(userScreenName, cursorId, 200),
                te -> new IllegalArgumentException("Error getting followers for User(screenName:" + userScreenName + ")", te),
                TwitterUser::new);
    }

    @Override
    public Stream<User> getFollowers(final long userId) {
        final Twitter twitter = getTwitter();

        return pagedListAsStream(
                cursorId -> twitter.getFollowersList(userId, cursorId, 200),
                te -> new IllegalArgumentException("Error getting followers for User(id:" + userId + ")", te),
                TwitterUser::new);
    }

    private <T extends TwitterResponse, R> Stream<R> pagedListAsStream(
            final TwitterExceptionLongFunction<PagableResponseList<T>> pageableFunction,
            final Function<TwitterException, ? extends RuntimeException> exceptionConverter,
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
        final Twitter twitter = getTwitter();
        final Query query = getQuery(tweetQuery);
        final QueryResult result;

        try {
            result = twitter.search(query);
        } catch (TwitterException ex) {
            LOGGER.error("Error getting QueryResult for " + query, ex);
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
        final Query query = new Query();

        if (null != tweetQuery.getCount()) {
            query.setCount(tweetQuery.getCount());
        }

        if (null != tweetQuery.getLang()) {
            query.setLang(tweetQuery.getLang());
        }

        if (null != tweetQuery.getLocale()) {
            query.setLocale(tweetQuery.getLocale());
        }

        if (null != tweetQuery.getMaxId()) {
            query.setMaxId(tweetQuery.getMaxId());
        }

        if (null != tweetQuery.getQuery()) {
            query.setQuery(tweetQuery.getQuery());
        }

        if (null != tweetQuery.getResultType()) {
            query.setResultType(Query.ResultType.valueOf(tweetQuery.getResultType().name()));
        }

        if (null != tweetQuery.getSince()) {
            query.setSince(tweetQuery.getSince());
        }

        if (null != tweetQuery.getSinceId()) {
            query.setSinceId(tweetQuery.getSinceId());
        }

        if (null != tweetQuery.getUntil()) {
            query.setUntil(tweetQuery.getUntil());
        }

        return query;
    }

    private static class PagedIterator extends RateLimitIterator<Tweet> {

        private QueryResult queryResult;
        private Iterator<Status> statuses;
        private static final Logger LOGGER = LogManager.getLogger("org.tweetwallfx.startup");
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
                Configuration configuration = TwitterOAuth.getConfiguration();
                if (null == configuration) {
                    queryResult = null;
                    statuses = null;
                    return;
                }
                final Twitter twitter = new TwitterFactory(configuration).getInstance();

                try {
                    LOGGER.trace("Querying next page: " + query);
                    queryResult = twitter.search(query);
                    if (null != queryResult) {
                        handleRateLimit(queryResult.getRateLimitStatus());
                        statuses = queryResult.getTweets().iterator();
                    }
                } catch (TwitterException ex) {
                    LOGGER.trace("Querying next page failed: " + query, ex);
                    LOGGER.error("Error getting QueryResult for " + query, ex);
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

            final TwitterSettings twitterSettings = org.tweetwallfx.config.Configuration.getInstance()
                    .getConfigTyped(TwitterSettings.CONFIG_KEY, TwitterSettings.class);

            if (twitterSettings.ignoreRateLimit()) {
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
        private final Function<TwitterException, ? extends RuntimeException> exceptionConverter;
        private PagableResponseList<T> prList;

        public PagedEntityIterator(
                final TwitterExceptionLongFunction<PagableResponseList<T>> pageableFunction,
                final Function<T, R> objectConverter,
                final Function<TwitterException, ? extends RuntimeException> exceptionConverter) {
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
                final RuntimeException re = exceptionConverter.apply(ex);
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
