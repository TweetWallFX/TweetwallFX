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
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import twitter4j.v1.FilterQuery;
import twitter4j.v1.Status;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class TwitterTweetStream implements TweetStream, Consumer<Status> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterTweetStream.class);

    private final List<Consumer<Tweet>> tweetConsumerList = new CopyOnWriteArrayList<>();

    private final TweetFilterQuery filterQuery;
    private final Predicate<Tweet> tweetFilter;

    public TwitterTweetStream(final TweetFilterQuery filterQuery, final Predicate<Tweet> tweetFilter) {
        this.filterQuery = filterQuery;
        activateStream();
        this.tweetFilter = tweetFilter;
    }

    @Override
    public void onTweet(final Consumer<Tweet> tweetConsumer) {
        synchronized (this) {
            LOG.info("Adding tweetConsumer: {}", tweetConsumer);
            this.tweetConsumerList.add(tweetConsumer);
            LOG.info("List of tweetConsumers is now: {}", tweetConsumerList);
        }
    }

    @Override
    public void accept(Status status) {
        TwitterTweet twitterTweet = new TwitterTweet(status);
        if (tweetFilter.test(twitterTweet)) {
            synchronized (TwitterTweetStream.this) {
                LOG.info("redispatching new received tweet to {}", tweetConsumerList);
                tweetConsumerList.stream().forEach(consumer -> consumer.accept(twitterTweet));
            }
        }
    }

    private void activateStream() {
        TwitterOAuth.instance().statusConsumer(this).twitterV1().stream().filter(getFilterQuery(filterQuery));
    }

    private static FilterQuery getFilterQuery(final TweetFilterQuery tweetFilterQuery) {
        return FilterQuery.ofTrack(tweetFilterQuery.getTrack()).count(tweetFilterQuery.getCount());
    }

    void shutdown() {
        TwitterOAuth.instance().statusConsumer(null).twitterV1().stream().shutdown();;
    }
}
