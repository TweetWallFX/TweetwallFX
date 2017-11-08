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

import static org.tweetwall.util.ToString.*;

/**
 * A
 * {@link Break break slot} is a short or long break such as lunch, coffee break
 * in the agenda. A {@link Talk talk slot} is a short description of a talk with
 * links to Speaker's profile.
 * <br><br>
 *
 * Fields are:
 * <ul>
 * <li>slotId: a unique identifier for the room</li>
 * <li>roomId: a unique identifier for the room</li>
 * <li>roomName: the room full name</li>
 * <li>day: day of week</li>
 * <li>fromTime: start time formatted as HH:mm</li>
 * <li>fromTimeMillis: start time as timestamp</li>
 * <li>toTime: end time formatted as HH:mm</li>
 * <li>toTimeMillis: end time as timestamp</li>
 * <li>break: {@link Break}</li>
 * <li>talk: {@link Talk}</li>
 * </ul>
 *
 * Fields {@code break} and {@code talk} are mutually exclusive. While one field
 * contains the corresponding object type the other is {@code null}.
 */
public class ScheduleSlot {

    /**
     * a unique identifier for the room
     */
    private String roomId;

    private boolean notAllocated;

    /**
     * start time as timestamp
     */
    private long fromTimeMillis;

    /**
     * The break description.
     */
    private Break breakObject;

    /**
     * type of room
     */
    private String roomSetup;

    /**
     * The talk description.
     */
    private Talk talk;

    /**
     * start time formatted as HH:mm
     */
    private String fromTime;

    /**
     * end time as timestamp
     */
    private long toTimeMillis;

    /**
     * end time formatted as HH:mm
     */
    private String toTime;

    /**
     * capacity of the room
     */
    private int roomCapacity;

    /**
     * the room full name
     */
    private String roomName;

    /**
     * a unique identifier for the room
     */
    private String slotId;

    /**
     * day of week
     */
    private String day;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public boolean isNotAllocated() {
        return notAllocated;
    }

    public void setNotAllocated(final boolean notAllocated) {
        this.notAllocated = notAllocated;
    }

    public long getFromTimeMillis() {
        return fromTimeMillis;
    }

    public void setFromTimeMillis(final long fromTimeMillis) {
        this.fromTimeMillis = fromTimeMillis;
    }

    public Break getBreak() {
        return breakObject;
    }

    public void setBreak(final Break breakObject) {
        this.breakObject = breakObject;
    }

    public String getRoomSetup() {
        return roomSetup;
    }

    public void setRoomSetup(final String roomSetup) {
        this.roomSetup = roomSetup;
    }

    public Talk getTalk() {
        return talk;
    }

    public void setTalk(final Talk talk) {
        this.talk = talk;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(final String fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTimeMillis() {
        return toTimeMillis;
    }

    public void setToTimeMillis(final long toTimeMillis) {
        this.toTimeMillis = toTimeMillis;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(final String toTime) {
        this.toTime = toTime;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(final int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(final String slotId) {
        this.slotId = slotId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(final String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return createToString(this, mapOf(
                mapEntry("roomId", getRoomId()),
                mapEntry("notAllocated", isNotAllocated()),
                mapEntry("fromTimeMillis", getFromTimeMillis()),
                mapEntry("break", getBreak()),
                mapEntry("roomSetup", getRoomSetup()),
                mapEntry("talk", getTalk()),
                mapEntry("fromTime", getFromTime()),
                mapEntry("toTimeMillis", getToTimeMillis()),
                mapEntry("toTime", getToTime()),
                mapEntry("roomCapacity", getRoomCapacity()),
                mapEntry("roomName", getRoomName()),
                mapEntry("slotId", getSlotId()),
                mapEntry("day", getDay())
        )) + " extends " + super.toString();
    }
}
