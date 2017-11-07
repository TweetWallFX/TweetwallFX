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

import java.util.Collections;
import java.util.List;
import static org.tweetwall.util.ToString.*;

/**
 * Voting results.
 */
public class VotingResult {

    private int totalResults;
    private String trackId;
    private List<VotingResultTalk> talks;
    private String talkTypeId;
    private String day;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final int totalResults) {
        this.totalResults = totalResults;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(final String trackId) {
        this.trackId = trackId;
    }

    public List<VotingResultTalk> getTalks() {
        return null == talks
                ? Collections.emptyList()
                : Collections.unmodifiableList(talks);
    }

    public void setTalks(final List<VotingResultTalk> talks) {
        this.talks = talks;
    }

    public String getTalkTypeId() {
        return talkTypeId;
    }

    public void setTalkTypeId(final String talkTypeId) {
        this.talkTypeId = talkTypeId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(final String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "totalResults", getTotalResults(),
                "trackId", getTrackId(),
                "talks", getTalks(),
                "talkTypeId", getTalkTypeId(),
                "day", getDay()
        )) + " extends " + super.toString();
    }
}
