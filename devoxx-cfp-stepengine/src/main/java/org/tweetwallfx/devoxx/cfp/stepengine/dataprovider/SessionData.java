/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 TweetWallFX
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
package org.tweetwallfx.devoxx.cfp.stepengine.dataprovider;

import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.devoxx.api.cfp.client.Schedule;
import org.tweetwallfx.devoxx.api.cfp.client.ScheduleSlot;
import org.tweetwallfx.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwallfx.util.StringNumberComparator;

/**
 * Seesion Data Pojo Helper class
 */
public class SessionData {

    private static final Logger LOG = LogManager.getLogger(SessionData.class);
    private static final Comparator<SessionData> COMP = Comparator.comparing(SessionData::getRoomSetup)
            .reversed()
            .thenComparing(SessionData::getRoom, StringNumberComparator.INSTANCE);

    public final String room;
    public final List<String> speakers;
    public final String title;
    public final String beginTime;
    public final String endTime;
    public final boolean isNotAllocated;
    public final String roomSetup;
    public final List<SpeakerReference> speakerObjects;
    public final int favouritesCount;
    public final String trackImageUrl;

    private SessionData(final ScheduleSlot slot) {
        this.room = slot.getRoomName();
        this.speakers = slot.getTalk().getSpeakers().stream().map(SpeakerReference::getName).collect(Collectors.toList());
        this.speakerObjects = Collections.unmodifiableList(new ArrayList<>(slot.getTalk().getSpeakers()));
        this.title = slot.getTalk().getTitle();
        this.beginTime = slot.getFromTime();
        this.isNotAllocated = slot.isNotAllocated();
        this.roomSetup = slot.getRoomSetup();
        this.endTime = slot.getToTime();
        this.favouritesCount = slot.getTalk().getFavoritesCount();
        this.trackImageUrl = slot.getTalk().getTrackImageURL();
    }

    /**
     * For testing purposes only.
     */
    static List<SessionData> from(final Schedule schedule, OffsetTime now, ZoneId zoneId) {
        return from(schedule.getSlots(), now, zoneId);
    }

    public static List<SessionData> from(List<ScheduleSlot> slots, OffsetTime now, ZoneId zoneId) {
        List<SessionData> sessionData = slots.stream()
                .filter(slot -> null != slot.getTalk())
                .filter(slot -> OffsetTime.ofInstant(Instant.ofEpochMilli(slot.getToTimeMillis()), zoneId).isAfter(now.plusMinutes(10)))
                .collect(Collectors.groupingBy(ScheduleSlot::getRoomId))
                .entrySet().stream()
                .map(entry -> entry.getValue().stream().findFirst())
                .map(optional -> optional.orElse(null))
                .map(SessionData::new)
                .sorted(SessionData.COMP)
                .collect(Collectors.toList());
        final Optional<String> min = sessionData.stream().map(sd -> sd.beginTime).min(Comparator.naturalOrder());
        if (min.isPresent()) {
            sessionData = sessionData.stream().filter(sd -> sd.beginTime.equals(min.get())).collect(Collectors.toList());
        }
        LOG.info("Possible Next Sessions ({}):\n {}", sessionData.size(), sessionData);
        return sessionData;
    }

    private String getRoom() {
        return room;
    }

    private String getRoomSetup() {
        return roomSetup;
    }

    @Override
    public String toString() {
        return "SessionData{" + "room=" + room + ", speakers=" + speakers + ", title=" + title + ", beginTime=" + beginTime + ", isNotAllocated=" + isNotAllocated + '}';
    }
}
