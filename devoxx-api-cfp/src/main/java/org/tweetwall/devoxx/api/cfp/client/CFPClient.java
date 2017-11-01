/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwall.devoxx.api.cfp.client;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Client handling the Event prepared for by an CFP.
 */
public interface CFPClient {

    /**
     * Lists all Events the Client API has available.
     *
     * @return a list of all events
     */
    Events getEvents();

    /**
     * See the Event this Client works with.
     *
     * @return the Event
     */
    Event getEvent();

    /**
     * Show the list of rooms.
     *
     * @return the list of all rooms
     */
    Rooms getRooms();

    /**
     * All Speakers having one or more talks at the Event.
     *
     * @return all Speakers with talks at the event
     */
    List<Speaker> getSpeakers();

    /**
     * The speaker with a specific {@code id}.
     *
     * @param speakerId the id of the Speaker
     *
     * @return the Speaker
     */
    Speaker getSpeaker(final String speakerId);

    /**
     * The Talk identified by its unique {@code talkId}.
     *
     * @param talkId the unique ID of a talk
     *
     * @return the requested talk
     */
    Talk getTalk(final String talkId);

    /**
     * Stream of all available {@link CFPClient CFPClients}.
     *
     * @return Stream containing all available CFPClients
     */
    static Stream<CFPClient> getClientStream() {
        return StreamSupport.stream(
                ServiceLoader.load(CFPClient.class).spliterator(),
                false);
    }

    /**
     * The first {@link CFPClient} found.
     *
     * @throws IllegalArgumentException in case no {@link CFPClient} is found
     *
     * @return the {@link CFPClient}
     */
    static CFPClient getClient() {
        return getClientStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No DevoxxCFPClient instances found!"));
    }
}
