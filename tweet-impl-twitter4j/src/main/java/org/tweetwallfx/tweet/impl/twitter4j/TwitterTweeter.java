/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.TweetQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterTweeter extends Tweeter {
    
    private static final Logger LOGGER = Logger.getLogger(TwitterTweeter.class);
    
    private List<TwitterTweetStream> streamCache = new ArrayList<>();
    
    public TwitterTweeter() {
        TwitterOAuth.exception().addListener((observable, oldValue, newValue) -> setLatestException(newValue));
        TwitterOAuth.getConfiguration();
    }

    @Override
    public TweetStream createTweetStream(TweetFilterQuery tweetFilterQuery) {
        TwitterTweetStream twitterTweetStream = new TwitterTweetStream(tweetFilterQuery);
        streamCache.add(twitterTweetStream);
        return twitterTweetStream;
    }

    @Override
    public Stream<Tweet> search(final TweetQuery tweetQuery) {
        final Twitter twitter = new TwitterFactory(TwitterOAuth.getConfiguration()).getInstance();
        final Query query = getQuery(tweetQuery);
        final QueryResult result;

        try {
            result = twitter.search(query);
        } catch (TwitterException ex) {
            setLatestException(ex);
            LOGGER.error("Error getting QueryResult for " + query, ex);
            return Stream.empty();
        }

        return result.getTweets().stream().map(TwitterTweet::new);
    }

    @Override
    public Stream<Tweet> searchPaged(final TweetQuery tweetQuery, int numberOfPages) {
        final Query query = getQuery(tweetQuery);
        final Iterable<Tweet> iterable = () -> new PagedIterator(this, query, numberOfPages);
        return StreamSupport.stream(iterable.spliterator(), false);
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

    private static class PagedIterator implements Iterator<Tweet> {

        private final TwitterTweeter tweeter;
        private QueryResult queryResult;
        private Iterator<Status> statuses;
        private static final Logger startupLogger = Logger.getLogger("org.tweetwallfx.startup");
        private int numberOfPages;

        public PagedIterator(final TwitterTweeter tweeter, final Query query, int numberOfPages) {
            this.tweeter = tweeter;
            this.numberOfPages = --numberOfPages;
            queryNext(query);
        }

        private void queryNext(final Query query) {
            numberOfPages--;
            if (null == query) {
                statuses = null;
            } else {
                final Twitter twitter = new TwitterFactory(TwitterOAuth.getConfiguration()).getInstance();

                try {
                    startupLogger.trace("Querying next page: " + query);
                    queryResult = twitter.search(query);
                    if (null != queryResult) {
                        LOGGER.info("RateLimi: " + queryResult.getRateLimitStatus().getRemaining() + "/" + queryResult.getRateLimitStatus().getLimit() 
                                + " resetting in " + queryResult.getRateLimitStatus().getSecondsUntilReset() + "s");
                        statuses = queryResult.getTweets().iterator();
                    }
                } catch (TwitterException ex) {
                    startupLogger.trace("Querying next page failed: " + query, ex);
                    tweeter.setLatestException(ex);
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
    
}
