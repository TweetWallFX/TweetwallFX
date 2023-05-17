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

import org.mastodon4j.core.api.entities.Event;
import org.mastodon4j.core.api.entities.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.util.JsonDataConverter;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventStatusConsumer implements Consumer<Event> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventStatusConsumer.class);
    private final Consumer<Status> statusConsumer;
    private final Predicate<Status> statusPredicate;

    EventStatusConsumer(Consumer<Status> statusConsumer) {
        this(statusConsumer, status -> true);
    }

    EventStatusConsumer(Consumer<Status> statusConsumer, Predicate<Status> statusPredicate) {
        this.statusConsumer = Objects.requireNonNull(statusConsumer, "statusConsumer must not be null");
        this.statusPredicate = Objects.requireNonNull(statusPredicate, "statusPredicate must not be null");
    }

    private void notifyStatusPayload(String payload) {
        LOGGER.debug("Processing payload:\n{}", payload);
        try {
            final Status status = JsonDataConverter.convertFromString(payload, Status.class);
            if (statusPredicate.test(status)) {
                statusConsumer.accept(status);
            } else {
                LOGGER.debug("Status {} not matching criteria", status.id());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to notify status", e);
        }
    }

    @Override
    public void accept(Event event) {
        switch (event.event()) {
            case "update", "status.update" -> notifyStatusPayload(event.payload());
            default -> LOGGER.debug("Ignoring event:\n{}", event);
        }
    }
}
