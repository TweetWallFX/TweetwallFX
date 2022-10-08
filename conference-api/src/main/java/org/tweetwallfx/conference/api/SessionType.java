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

import java.time.Duration;
import java.util.Optional;

/**
 * POJO of a type of a session type.
 */
public interface SessionType extends Identifiable {

    /**
     * Returns the name of this session type.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the description of this session type.
     *
     * @return the description
     */
    Optional<String> getDescription();

    /**
     * Returns the color to be used when displaying this session type.
     *
     * @return the color
     */
    Optional<String> getColor();

    /**
     * Returns the duration of this session type.
     *
     * @return the duration
     */
    Duration getDuration();

    /**
     * Returns a flag indicating that this session type is a pause type.
     *
     * @return {@code true} is sessions of this type are a pause session and
     * {@code false} otherwise
     */
    boolean isPause();
}
