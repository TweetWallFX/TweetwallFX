/*
 * The MIT License
 *
 * Copyright 2017 TweetWallFX
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
package org.tweetwallfx.devoxx2017be.dataprovider;

import java.time.OffsetTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.Schedule;

/**
 * Seesion Data Pojo Helper class
 * @author Sven Reimers
 */
public class SessionData {

    public final String room;
    public final List<String> speakers;
    public final String title;
    public final String beginTime;
    final boolean isNotAllocated;
    public final String roomSetup;

    public static List<SessionData> from(Schedule schedule, OffsetTime now) {
        List<SessionData> sessionData = schedule.getSlots().stream()
                //                .filter(slot -> !slot.isNotAllocated())
                .filter(slot -> null != slot.getTalk())
                .filter(slot -> slot.getTalk().getTalkType() != "BOF (Bird of a Feather)")
                .filter(slot -> OffsetTime.parse(slot.getToTime() + "Z").isAfter(now))
                .filter(slot -> OffsetTime.parse(slot.getFromTime() + "Z").isBefore(now.plusMinutes(120)))
                .collect(Collectors.groupingBy(slot -> slot.getRoomId()))
                .entrySet().stream()
                .map(entry -> entry.getValue().stream().findFirst())
                .map(optional -> optional.orElse(null))
                .map(slot -> new SessionData(slot.getRoomName(),
                        slot.getTalk().getSpeakers().stream().map(ref -> ref.getName()).collect(Collectors.toList()),
                        slot.getTalk().getTitle(),
                        slot.getFromTime(),
                        slot.isNotAllocated(),
                        slot.getRoomSetup()))
                .sorted(SessionData.COMP)
                .collect(Collectors.toList());
        System.out.println("Possible Next Sessions (" + sessionData.size() + "):\n " + sessionData);
        return sessionData;
    }

    private static Comparator<SessionData> COMP = Comparator.comparing(SessionData::getRoomSetup).reversed().thenComparing(SessionData::getRoom);
    
    public SessionData(String room, List<String> speakers, String title, String beginTime, boolean isNotAllocated, String roomSetup) {
        this.room = room;
        this.speakers = speakers;
        this.title = title;
        this.beginTime = beginTime;
        this.isNotAllocated = isNotAllocated;
        this.roomSetup = roomSetup;
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
