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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

final class CompositeTweetStream implements TweetStream, Consumer<Tweet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTweetStream.class);

    private final List<Consumer<Tweet>> tweetConsumerList = new CopyOnWriteArrayList<>();

    @Override
    public void onTweet(Consumer<Tweet> tweetConsumer) {
        synchronized (this) {
            LOGGER.info("Adding tweetConsumer: {}", tweetConsumer);
            tweetConsumerList.add(tweetConsumer);
            LOGGER.info("List of tweetConsumers is now: {}", tweetConsumerList);
        }
    }

    @Override
    public void accept(Tweet tweet) {
        synchronized (this) {
            LOGGER.info("Redispatching new received tweet to {}", tweetConsumerList);
            tweetConsumerList.stream().forEach(consumer -> consumer.accept(tweet));
        }
    }
}
