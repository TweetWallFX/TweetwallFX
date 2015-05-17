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

import java.util.function.Consumer;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

final class TwitterTweetStream implements TweetStream {

    private Consumer<Tweet> tweetConsumer = null;

    @Override
    public void onTweet(final Consumer<Tweet> tweetConsumer) {
        synchronized (this) {
            this.tweetConsumer = tweetConsumer;
        }
    }

    @Override
    public void filter(final TweetFilterQuery filterQuery) {
        final TwitterStream twitterStream = new TwitterStreamFactory(TwitterOAuth.getInstance().readOAuth()).getInstance();

        twitterStream.addListener(new StatusAdapter() {

            @Override
            public void onStatus(Status status) {
                synchronized (TwitterTweetStream.this) {
                    if (null != tweetConsumer) {
                        tweetConsumer.accept(new TwitterTweet(status));
                    }
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
}
