/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.conference.api;

import java.util.List;
import java.util.Optional;

/**
 * Client handling information retrieval for a conference.
 */
public interface ConferenceClient {

    /**
     * Returns the name of this conference.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the {@link SessionType} of this conference.
     *
     * @return the {@link SessionType}
     */
    List<SessionType> getSessionTypes();

    /**
     * Returns the {@link Room}s of this conference. Should the request fail for
     * whatever reason en empty list is returned.
     *
     * @return the {@link Room}s
     */
    List<Room> getRooms();

    /**
     * Returns the {@link ScheduleSlot}s of the requested conferenceDay. Should
     * the request fail for whatever reason en empty list is returned.
     *
     * @param conferenceDay the day for which to retrieve the schedule
     *
     * @return the {@link ScheduleSlot}s
     */
    List<ScheduleSlot> getSchedule(final String conferenceDay);

    /**
     * Returns the {@link ScheduleSlot}s of the requested {@code conferenceDay}
     * and {@code roomName}. Should the request fail for whatever reason en
     * empty list is returned.
     *
     * @param conferenceDay the day for which to retrieve the schedule
     *
     * @param roomName the name for which to retrieve the schedule
     *
     * @return the {@link ScheduleSlot}s
     */
    List<ScheduleSlot> getSchedule(String conferenceDay, String roomName);

    /**
     * Returns the {@link Speaker}s of this conference. Should the request fail
     * for whatever reason en empty list is returned.
     *
     * @return the {@link Speaker}s
     */
    List<Speaker> getSpeakers();

    /**
     * Returns the {@link Speaker} with the given {@code speakerId}. Should the
     * request fail for whatever reason en empty Optional is returned.
     *
     * @param speakerId the id of the {@link Speaker}
     *
     * @return the {@link Speaker}
     */
    Optional<Speaker> getSpeaker(final String speakerId);

    /**
     * Returns the {@link Talk}s of this conference. Should the request fail for
     * whatever reason en empty list is returned.
     *
     * @return the {@link Talk}s
     */
    List<Talk> getTalks();

    /**
     * Returns the {@link Talk} with the given {@code talkId}. Should the
     * request fail for whatever reason en empty Optional is returned.
     *
     * @param talkId the id of the {@link Talk}
     *
     * @return the {@link Talk}
     */
    Optional<Talk> getTalk(final String talkId);

    /**
     * Returns the {@link Track}s of this conference. Should the request fail
     * for whatever reason en empty list is returned.
     *
     * @return the {@link Track}s
     */
    List<Track> getTracks();

    /**
     * Returns the {@link RatingClient} for this conference. Should the
     * conference not support rating then an empty {@link Optional} is returned.
     *
     * @return the {@link RatingClient}.
     */
    Optional<RatingClient> getRatingClient();

    /**
     * The first {@link ConferenceClient} found.
     *
     * @throws IllegalArgumentException in case no {@link ConferenceClient} is
     * found
     *
     * @return the found {@link ConferenceClient}
     */
    static ConferenceClient getClient() {
        return Util.getClient();
    }
}
