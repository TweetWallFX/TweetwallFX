/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 TweetWallFX
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
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * POJO of a talk.
 */
public interface Talk extends Identifiable {

    /**
     * Returns the name of this talk.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the level of audience this talk is intended at.
     *
     * @return the level of audience
     */
    String getAudienceLevel();

    /**
     * Returns the {@link SessionType} this talk is.
     *
     * @return the {@link SessionType}
     */
    SessionType getSessionType();

    /**
     * {@return the number of times this talk was marked as a favorite}
     */
    OptionalInt getFavoriteCount();

    /**
     * Returns the {@link Locale} of the language the talk will be held in.
     *
     * @return the {@link Locale}
     */
    Locale getLanguage();

    /**
     * Returns the {@link ScheduleSlot}s where this talk is given.
     *
     * @return the {@link ScheduleSlot}s
     */
    List<ScheduleSlot> getScheduleSlots();

    /**
     * Returns the {@link Speaker}s that give this talk.
     *
     * @return the {@link Speaker}s
     */
    List<Speaker> getSpeakers();

    /**
     * Returns the tags that were associated with this talk.
     *
     * @return the tags
     */
    List<String> getTags();

    /**
     * Returns the {@link Track} this talk is associated with.
     *
     * @return the {@link Track}
     */
    Track getTrack();

    /**
     * Attempts to reload this talk via the {@link ConferenceClient} and returns
     * the result wrapped in an {@link Optional}. Should the request fail for
     * whatever reason an empty Optional is returned.
     *
     * @return the reloaded talk instance
     */
    Optional<Talk> reload();
}
