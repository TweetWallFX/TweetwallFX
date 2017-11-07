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
 * The voting result of a single talk.
 */
public class VotingResultTalk {

    private String proposalId;
    private String proposalTitle;
    private String proposalTalkType;
    private String proposalTalkTypeId;
    private double ratingAverageScore;
    private int ratingTotalVotes;
    private String proposalsSpeakers;

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(final String proposalId) {
        this.proposalId = proposalId;
    }

    public String getProposalTitle() {
        return proposalTitle;
    }

    public void setProposalTitle(final String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public String getProposalTalkType() {
        return proposalTalkType;
    }

    public void setProposalTalkType(final String proposalTalkType) {
        this.proposalTalkType = proposalTalkType;
    }

    public String getProposalTalkTypeId() {
        return proposalTalkTypeId;
    }

    public void setProposalTalkTypeId(final String proposalTalkTypeId) {
        this.proposalTalkTypeId = proposalTalkTypeId;
    }

    public double getRatingAverageScore() {
        return ratingAverageScore;
    }

    public void setRatingAverageScore(final double ratingAverageScore) {
        this.ratingAverageScore = ratingAverageScore;
    }

    public int getRatingTotalVotes() {
        return ratingTotalVotes;
    }

    public void setRatingTotalVotes(final int ratingTotalVotes) {
        this.ratingTotalVotes = ratingTotalVotes;
    }

    public String getProposalsSpeakers() {
        return proposalsSpeakers;
    }

    public void setProposalsSpeakers(final String proposalsSpeakers) {
        this.proposalsSpeakers = proposalsSpeakers;
    }

    @Override
    public String toString() {
        return createToString(this, mapOf(
                mapEntry("proposalId", getProposalId()),
                mapEntry("proposalTitle", getProposalTitle()),
                mapEntry("proposalTalkType", getProposalTalkType()),
                mapEntry("proposalTalkTypeId", getProposalTalkTypeId()),
                mapEntry("ratingAverageScore", getRatingAverageScore()),
                mapEntry("ratingTotalVotes", getRatingTotalVotes()),
                mapEntry("proposalsSpeakers", getProposalsSpeakers())
        )) + " extends " + super.toString();
    }
}
