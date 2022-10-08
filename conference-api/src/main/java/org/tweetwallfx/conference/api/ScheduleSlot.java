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

import java.util.Optional;
import java.util.OptionalInt;

/**
 * POJO of a schedule slot.
 */
public interface ScheduleSlot extends Identifiable {

    /**
     * Returns the date time range of this schedule slot.
     *
     * @return the date time range
     */
    DateTimeRange getDateTimeRange();

    /**
     * Returnsthe number of times this schedule slot was marked as a favorite.
     *
     * @return the number of time this schedule slot was marked as a favorite
     */
    OptionalInt getFavoriteCount();

    /**
     * Returns a flag indicating that this schedule slot is an overflow session
     * (i.e. the session is streamed into this room to make the session
     * accessible for more attendees).
     *
     * @return {@code true} when this schedule slot is an overflow session
     */
    boolean isOverflow();

    /**
     * Returns the room this schedule slot is held in.
     *
     * @return the room
     */
    Room getRoom();

    /**
     * Returns the {@link Talk} presented in this schedule slot.
     *
     * @return the {@link Talk}
     */
    Optional<Talk> getTalk();
}
