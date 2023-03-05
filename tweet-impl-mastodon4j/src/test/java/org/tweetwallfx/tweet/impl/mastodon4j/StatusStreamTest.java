/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mastodon4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mastodon4j.core.api.entities.Status;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createStatus;

@MockitoSettings
class StatusStreamTest {
    @Mock(name = "org.tweetwallfx.tweet.impl.mastodon4j.StatusStream")
    Logger logger;
    @Mock(name = "tweetConsumer")
    Consumer<Tweet> tweetConsumer;
    StatusStream stream;

    @BeforeEach
    void prepare() {
        stream = new StatusStream();
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
        Status status = createStatus("4711", "gugus");
        doNothing().when(logger).debug("Notify status:\n{}", status);
        assertThatNoException().isThrownBy(() -> stream.accept(status));
    }

    @Test
    void accept() {
        Status status = createStatus("4711", "gugus");
        doNothing().when(logger).debug("onTweet({})", tweetConsumer);
        doNothing().when(logger).debug("Notify status:\n{}", status);
        doNothing().when(tweetConsumer).accept(new MastodonStatus(status));
        stream.onTweet(tweetConsumer);
        assertThatNoException().isThrownBy(() -> stream.accept(status));
    }
}
