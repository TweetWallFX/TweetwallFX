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

import java.io.Serializable;
import java.time.OffsetTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwall.devoxx.api.cfp.client.ScheduleSlot;

/**
 * Seesion Data Pojo Helper class
 *
 * @author Sven Reimers
 */
public class SessionData {

    private static final Comparator<SessionData> COMP = Comparator.comparing(SessionData::getRoomSetup)
            .reversed()
            .thenComparing(SessionData::getRoom, new RoomComparator());
    
    private static class RoomComparator implements Comparator<String>, Serializable {

        private static final long serialVersionUID = -1; 
        
        @Override
        public int compare(String o1, String o2) {
            String[] room1_split = o1.split(" ");
            String[] room2_split = o2.split(" ");
            int room_part1 = room1_split[0].compareTo(room2_split[0]);
            if (room_part1 == 0) {
                return Integer.compare(Integer.parseInt(room1_split[1]), Integer.parseInt(room2_split[1]));
            } else {
                return room_part1;
            }
        }
    }            
    
    
    public final String room;
    public final List<String> speakers;
    public final String title;
    public final String beginTime;
    final boolean isNotAllocated;
    public final String roomSetup;

    private SessionData(final ScheduleSlot slot) {
        this.room = slot.getRoomName();
        this.speakers = slot.getTalk().getSpeakers().stream().map(ref -> ref.getName()).collect(Collectors.toList());
        this.title = slot.getTalk().getTitle();
        this.beginTime = slot.getFromTime();
        this.isNotAllocated = slot.isNotAllocated();
        this.roomSetup = slot.getRoomSetup();
    }

    /**
     * For testing purposes only.
     */
    static List<SessionData> from(final Schedule schedule, OffsetTime now) {
        return from(schedule.getSlots(), now);
    }

    public static List<SessionData> from(List<ScheduleSlot> slots, OffsetTime now) {
        List<SessionData> sessionData = slots.stream()
                //                .filter(slot -> !slot.isNotAllocated())
                .filter(slot -> null != slot.getTalk())
                .filter(slot -> OffsetTime.parse(slot.getToTime() + "Z").isAfter(now.plusMinutes(10)))
                .collect(Collectors.groupingBy(slot -> slot.getRoomId()))
                .entrySet().stream()
                .map(entry -> entry.getValue().stream().findFirst())
                .map(optional -> optional.orElse(null))
                .map(SessionData::new)
                .sorted(SessionData.COMP)
                .collect(Collectors.toList());
        Optional<String> min = sessionData.stream().map(sd -> sd.beginTime).min(Comparator.naturalOrder());
        if (min.isPresent())
            sessionData = sessionData.stream().filter(sd -> sd.beginTime.equals(min.get())).collect(Collectors.toList());
        System.out.println("Possible Next Sessions (" + sessionData.size() + "):\n " + sessionData);
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
