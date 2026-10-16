/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2026 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.dataprovider;

import static org.tweetwallfx.util.ToString.createToString;

import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.conference.api.Room;
import org.tweetwallfx.conference.api.ScheduleSlot;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.api.Talk;

/**
 * Seesion Data Pojo Helper class
 */
public class SessionData {

    private static final Logger LOG = LoggerFactory.getLogger(SessionData.class);
    private static final Comparator<SessionData> COMP = Comparator
            .comparing(SessionData::getBeginTime)
            .thenComparing(SessionData::getRoom);

    /** The room the session takes place in. */
    public final Room room;
    /** The full names of the speakers of the session. */
    public final List<String> speakers;
    /** The title of the session. */
    public final String title;
    /** The start time of the session. */
    public final Instant beginTime;
    /** The end time of the session. */
    public final Instant endTime;
    /** The speaker objects of the session. */
    public final List<Speaker> speakerObjects;
    /** The number of favourites of the session. */
    public final int favouritesCount;
    /** The URL of the avatar image of the track of the session. */
    public final String trackImageUrl;
    /** The tags assigned to the session. */
    public final List<String> tags;

    private SessionData(final ScheduleSlot slot) {
        this.room = slot.getRoom();
        // session data for schedule slots with talks only
        Talk talk = slot.getTalk().get();
        this.speakerObjects = List.copyOf(talk.getSpeakers());
        this.speakers = speakerObjects.stream().map(Speaker::getFullName).toList();
        this.title = talk.getName();
        this.beginTime = slot.getDateTimeRange().getStart();
        this.endTime = slot.getDateTimeRange().getEnd();
        // session data for schedule with talk may have a favorite count as well as the talk itself may have a favorite count
        this.favouritesCount = IntStream
                .concat(slot.getFavoriteCount().stream(), talk.getFavoriteCount().stream())
                .filter(i -> i > 0)
                .findFirst()
                .orElse(0);
        this.trackImageUrl = talk.getTrack().getAvatarURL();
        this.tags = talk.getTags();
    }

    /**
     * Creates session data for the talks of the given schedule slots which
     * start within the given time frame, sorted by begin time and room.
     *
     * @param slots              the schedule slots to convert
     * @param now                the current time
     * @param zoneId             the zone id to evaluate the times with
     * @param startsWithinMinutes the number of minutes from now within which a
     *                           session must start to be included
     * @return an immutable list of the session data of the matching slots
     */
    public static List<SessionData> from(final List<ScheduleSlot> slots, final OffsetTime now, final ZoneId zoneId, final long startsWithinMinutes) {
        List<SessionData> sessionData = slots.stream()
                .filter(slot -> slot.getTalk().isPresent())
                .filter(slot -> OffsetTime.ofInstant(slot.getDateTimeRange().getEnd(), zoneId).isAfter(now.plusMinutes(10)))
                .filter(slot -> OffsetTime.ofInstant(slot.getDateTimeRange().getStart(), zoneId).isBefore(now.plusMinutes(startsWithinMinutes)))
                .collect(Collectors.groupingBy(ScheduleSlot::getRoom))
                .entrySet().stream()
                .map(entry -> entry.getValue().getFirst())
                .map(SessionData::new)
                .sorted(SessionData.COMP)
                .toList();

        LOG.info("Possible Next Sessions ({}):\n {}", sessionData.size(), sessionData);
        return sessionData;
    }

    private Room getRoom() {
        return room;
    }

    private Instant getBeginTime() {
        return beginTime;
    }

    /**
     * {@return a string representation of this session data}
     */
    @Override
    public String toString() {
        return createToString(this, Map.of(
                "room", getRoom(),
                "speakers", speakers,
                "title", title,
                "beginTime", getBeginTime(),
                "endTime", endTime
            ),
            true);
    }
}
