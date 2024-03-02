/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.dataprovider;

import java.util.List;
import org.tweetwallfx.conference.api.RatedTalk;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.api.Talk;

/**
 * Simple Wrapper for accessing VotedTalk informations.
 */
public class VotedTalk {

    public final List<Speaker> speakers;
    public final double ratingAverageScore;
    public final int ratingTotalVotes;
    public final Talk talk;
    public final List<String> tags;
    public final String trackImageUrl;

    public VotedTalk(final RatedTalk ratedTalk) {
        this.talk = ratedTalk.getTalk();
        this.speakers = ratedTalk.getTalk().getSpeakers();
        this.ratingAverageScore = ratedTalk.getAverageRating();
        this.ratingTotalVotes = ratedTalk.getTotalRating();
        this.tags = talk.getTags();
        this.trackImageUrl = talk.getTrack().getAvatarURL();
    }
}
