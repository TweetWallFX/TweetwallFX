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

import java.util.Comparator;

/**
 * POJO of a rated talk.
 */
public interface RatedTalk extends Comparable<RatedTalk> {

    /**
     * Returns the {@link Talk} that has been rated.
     *
     * @return the {@link Talk}
     */
    Talk getTalk();

    /**
     * Returns the average rating of the talk.
     *
     * @return the average rating
     */
    double getAverageRating();

    /**
     * Returns the total rating of the talk.
     *
     * @return the total rating
     */
    int getTotalRating();

    @Override
    public default int compareTo(final RatedTalk o) {
        return Comparator
                .comparing(RatedTalk::getAverageRating)
                .thenComparing(RatedTalk::getTotalRating)
                .compare(this, o);
    }
}
