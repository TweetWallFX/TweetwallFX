/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings
class MockPostStreamTest {
    @Mock(name = "org.tweetwallfx.tweet.impl.mock.MockPostStream")
    Logger logger;
    @Mock(name = "tweetConsumer")
    Consumer<Tweet> tweetConsumer;
    MockPostStream stream;

    @BeforeEach
    void prepare() {
        stream = new MockPostStream();
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger, tweetConsumer);
    }

    @Test
    void onTweet() {
        doNothing().when(logger).debug("onTweet({})", tweetConsumer);
        assertThatNoException().isThrownBy(() -> stream.onTweet(tweetConsumer));
    }

    @Test
    void acceptNoConsumers() {
        Tweet post = createPost();
        doNothing().when(logger).debug("Notify post:\n{}", post);
        assertThatNoException().isThrownBy(() -> stream.accept(post));
    }

    @Test
    void accept() {
        Tweet post = createPost();
        doNothing().when(logger).debug("onTweet({})", tweetConsumer);
        doNothing().when(logger).debug("Notify post:\n{}", post);
        doNothing().when(tweetConsumer).accept(post);
        stream.onTweet(tweetConsumer);
        assertThatNoException().isThrownBy(() -> stream.accept(post));
    }

    private Tweet createPost() {
        return new MockPost(4711, "gugus", null,
                LocalDateTime.of(2024, 3, 3, 11, 20, 0), null,
                2, 3);
    }
}
