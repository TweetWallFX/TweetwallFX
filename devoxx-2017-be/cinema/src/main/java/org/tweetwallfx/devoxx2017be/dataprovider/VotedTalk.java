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

import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Speaker;
import org.tweetwall.devoxx.api.cfp.client.VotingResultTalk;

/**
 * Simple Wrapper for accessing VotedTalk informations.
 *
 * @author Sven Reimers
 */
public class VotedTalk {

    public final String speakers;
    public final double ratingAverageScore;
    public final int ratingTotalVotes;
    public final String proposalId;
    public final String proposalTitle;
    public final String speakerAvatar;

    public VotedTalk(final VotingResultTalk talk) {
        this(talk.getProposalsSpeakers(),
                talk.getRatingAverageScore(),
                talk.getRatingTotalVotes(),
                talk.getProposalId(),
                talk.getProposalTitle());
    }

    private VotedTalk(final String speakers, final double ratingAverageScore, final int ratingTotalVotes, final String proposalId, final String proposalTitle) {
        this.speakers = speakers;
        this.ratingAverageScore = ratingAverageScore;
        this.ratingTotalVotes = ratingTotalVotes;
        this.proposalId = proposalId;
        this.proposalTitle = proposalTitle;
        this.speakerAvatar = CFPClient.getClient()
                .getTalk(System.getProperty("org.tweetwallfx.devoxxbe17.proposal", proposalId))
                .flatMap(talk -> talk.getSpeakers().get(0).getSpeaker())
                .map(Speaker::getAvatarURL)
                .orElse(null);
    }
}
