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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;

@MockitoSettings
public class CompositeTweetStreamTest {
    @Mock(name = "org.tweetwallfx.tweet.api.CompositeTweetStream")
    Logger logger;
    @Mock(name = "tweetConsumerOne")
    Consumer<Tweet> tweetConsumerOne;
    @Mock(name = "tweetConsumeTwo")
    Consumer<Tweet> tweetConsumerTwo;
    @Mock(name = "tweet")
    Tweet tweet;
    CompositeTweetStream compositeTweetStream = new CompositeTweetStream();

    @AfterEach
    void verifyMocks() {
        verifyNoInteractions(tweet);
    }

    @Test
    void acceptWithoutConsumers() {
        doNothing().when(logger).info("Redispatching new received tweet to {}", new CopyOnWriteArrayList<>());
        assertThatNoException().isThrownBy(() -> compositeTweetStream.accept(tweet));
    }

    @Test
    void acceptAndOnTweet() {
        doNothing().when(logger).info("Adding tweetConsumer: {}", tweetConsumerOne);
        doNothing().when(logger).info("List of tweetConsumers is now: {}",
                new CopyOnWriteArrayList<>(List.of(tweetConsumerOne)));
        compositeTweetStream.onTweet(tweetConsumerOne);

        doNothing().when(logger).info("Adding tweetConsumer: {}", tweetConsumerTwo);
        doNothing().when(logger).info("List of tweetConsumers is now: {}",
                new CopyOnWriteArrayList<>(List.of(tweetConsumerOne, tweetConsumerTwo)));
        compositeTweetStream.onTweet(tweetConsumerTwo);

        doNothing().when(logger).info("Redispatching new received tweet to {}",
                new CopyOnWriteArrayList<>(List.of(tweetConsumerOne, tweetConsumerTwo)));
        doNothing().when(tweetConsumerOne).accept(tweet);
        doNothing().when(tweetConsumerTwo).accept(tweet);
        assertThatNoException().isThrownBy(() -> compositeTweetStream.accept(tweet));
    }
}
