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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

final class TwitterTweetStream implements TweetStream {

    private static final Logger log = LogManager.getLogger(TwitterTweetStream.class);
    
    private final List<Consumer<Tweet>> tweetConsumerList = new CopyOnWriteArrayList<>();

    private final TweetFilterQuery filterQuery;
    private TwitterStream twitterStream;

    public TwitterTweetStream(TweetFilterQuery filterQuery) {
        this.filterQuery = filterQuery;
        activateStream();
    }
    
    @Override
    public void onTweet(final Consumer<Tweet> tweetConsumer) {
        synchronized (this) {
            log.info("Adding tweetConsumer: " + tweetConsumer);
            this.tweetConsumerList.add(tweetConsumer);
            log.info("List of tweetConsumers is now: " + tweetConsumerList);
        }
    }

    private void activateStream() {
        Configuration configuration = TwitterOAuth.getConfiguration();
        if (null == configuration) return;
        twitterStream = new TwitterStreamFactory(configuration).getInstance();

        twitterStream.addListener(new StatusAdapter() {

            @Override
            public void onStatus(Status status) {
                synchronized (TwitterTweetStream.this) {
                    log.info("redispatching new received tweet to " + tweetConsumerList);
                    TwitterTweet twitterTweet = new TwitterTweet(status);
                    tweetConsumerList.stream().forEach(consumer -> consumer.accept(twitterTweet));
                }
            }
        });
        twitterStream.filter(getFilterQuery(filterQuery));
    }

    private static FilterQuery getFilterQuery(final TweetFilterQuery tweetFilterQuery) {
        return new FilterQuery()
                .count(tweetFilterQuery.getCount())
                .track(tweetFilterQuery.getTrack());
    }
    
    void shutdown() {
        twitterStream.shutdown();
    }
}
