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

import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.TweetQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterTweeter extends Tweeter {

    private static final Logger LOGGER = Logger.getLogger(TwitterTweeter.class);

    public TwitterTweeter() {
        TwitterOAuth.exception().addListener((observable, oldValue, newValue) -> setLatestException(newValue));
        TwitterOAuth.getConfiguration();
    }

    @Override
    public TweetStream createTweetStream() {
        return new TwitterTweetStream();
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

    private static Query getQuery(final TweetQuery tweetQuery) {
        return new Query()
                .count(tweetQuery.getCount())
                .lang(tweetQuery.getLang())
                .locale(tweetQuery.getLocale())
                .maxId(tweetQuery.getMaxId())
                .query(tweetQuery.getQuery())
                .resultType(null == tweetQuery.getResultType()
                                ? null
                                : Query.ResultType.valueOf(tweetQuery.getResultType().name()))
                .since(tweetQuery.getSince())
                .sinceId(tweetQuery.getSinceId())
                .until(tweetQuery.getUntil());
    }
}
