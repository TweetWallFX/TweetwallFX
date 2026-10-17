/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2026 TweetWallFX
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
package org.tweetwallfx.mqtt;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * JavaFX event used to signal MQTT related state changes.
 */
public class MqttEvent extends Event {

    private static final long serialVersionUID = 1L;
    /**
     * Common supertype for all MQTT event types.
     */
    public static final EventType<MqttEvent> ANY = new EventType<>(Event.ANY, "ANY");
    /**
     * Event type signalling a restart request.
     */
    public static final EventType<MqttEvent> RESTART = new EventType<>(Event.ANY, "RESTART");
    /**
     * Event type signalling a stop request.
     */
    public static final EventType<MqttEvent> STOP = new EventType<>(Event.ANY, "STOP");

    /**
     * Creates a new instance.
     *
     * @param source the source of the event
     * @param eventType the type of the event
     */
    public MqttEvent(Object source, EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }
}
