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
import java.util.Map;
import java.util.Optional;

/**
 * POJO of a type of a speaker.
 */
public interface Speaker extends Identifiable, Avatar {

    /**
     * Returns the first name of this speaker.
     *
     * @return the first name
     */
    String getFirstName();

    /**
     * Returns the full name of this speaker.
     *
     * @return the full name
     */
    String getFullName();

    /**
     * Returns the last name of this speaker.
     *
     * @return the last name
     */
    String getLastName();

    /**
     * {@return the company name of this speaker}
     */
    Optional<String> getCompany();

    /**
     * Returns the social media ids of this speaker.
     *
     * @return the social media ids
     */
    Map<String, String> getSocialMedia();

    /**
     * Returns the talks of this speaker at this conference.
     *
     * @return the talks
     */
    List<Talk> getTalks();

    /**
     * Attempts to reload this speaker via the {@link ConferenceClient} and
     * returns the result wrapped in an {@link Optional}. Should the request
     * fail for whatever reason an empty Optional is returned.
     *
     * @return the reloaded speaker instance
     */
    Optional<Speaker> reload();
}
