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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mastodon4j.core.api.entities.Event;
import org.mastodon4j.core.api.entities.Status;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNotNull;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createStatus;

@MockitoSettings
class EventStatusConsumerTest {
    @Mock(name = "org.tweetwallfx.tweet.impl.mastodon4j.EventStatusConsumer")
    Logger logger;
    @Mock(name = "statusConsumer")
    Consumer<Status> statusConsumer;
    EventStatusConsumer eventStatusConsumer;

    @BeforeEach
    void prepare() {
        eventStatusConsumer = new EventStatusConsumer(statusConsumer);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger, statusConsumer);
    }

    @ParameterizedTest
    @ValueSource(strings = {"delete", "notification", "filters_changed", "conversation", "announcement",
            "announcement.reaction", "announcement.delete", "encrypted_message"})
    void acceptUnsupportedEventTypes(String eventType) {
        final Event event = new Event(null, eventType, null);
        doNothing().when(logger).debug("Ignoring event:\n{}", event);
        assertThatNoException().isThrownBy(() -> eventStatusConsumer.accept(event));
    }

    @Test
    void acceptIllegalStatusPayload() {
        final Event event = new Event(List.of(), "update", "illegal-payload");
        doNothing().when(logger).debug("Processing payload:\n{}", "illegal-payload");
        doNothing().when(logger).error(eq("Failed to notify status"), isNotNull(Throwable.class));
        assertThatNoException().isThrownBy(() -> eventStatusConsumer.accept(event));
    }

    @Test
    void acceptValidPayload() {
        final String payload = "{\"id\":\"42\",\"content\":\"gugus\",\"mentions\":[]}";
        final Event event = new Event(List.of(), "update", payload);
        doNothing().when(logger).debug("Processing payload:\n{}", payload);
        doNothing().when(statusConsumer).accept(createStatus("42", "gugus"));
        assertThatNoException().isThrownBy(() -> eventStatusConsumer.accept(event));
    }

    @Test
    void acceptValidPayloadNotMatching() {
        final Event event = new Event(List.of(), "update", "{\"id\":\"43\",\"content\":\"gaga\"}");
        doNothing().when(logger).debug("Processing payload:\n{}", "{\"id\":\"43\",\"content\":\"gaga\"}");
        doNothing().when(logger).debug("Status {} not matching criteria", "43");

        eventStatusConsumer = new EventStatusConsumer(statusConsumer, status -> "42".equals(status.id()));
        assertThatNoException().isThrownBy(() -> eventStatusConsumer.accept(event));
    }
}
