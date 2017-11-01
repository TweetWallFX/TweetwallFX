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
package org.tweetwall.devoxx.api.cfp.client.impl2017be;

import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.GenericType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Event;
import org.tweetwall.devoxx.api.cfp.client.Events;
import org.tweetwall.devoxx.api.cfp.client.ProposalTypes;
import org.tweetwall.devoxx.api.cfp.client.Rooms;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwall.devoxx.api.cfp.client.Schedules;
import org.tweetwall.devoxx.api.cfp.client.Speaker;
import org.tweetwall.devoxx.api.cfp.client.Talk;
import org.tweetwall.devoxx.api.cfp.client.Tracks;
import org.tweetwall.devoxx.api.cfp.client.VotingResults;
import org.tweetwall.devoxx.api.cfp.client.impl.RestCallHelper;

/**
 * CFPClient working with DevoxxBE2017.
 */
public class CFPClientDevoxx2017BE implements CFPClient {

    private static final Logger LOGGER = LogManager.getLogger(CFPClientDevoxx2017BE.class);
    private static final String BASE_URI = "https://cfp.devoxx.be/api/";
    private static final String CONFERENCE_BASE_URI = "https://cfp.devoxx.be/api/conferences/DVBE17";

    @Override
    public Events getEvents() {
        return RestCallHelper.getData(BASE_URI + "conferences/", Events.class);
    }

    @Override
    public Event getEvent() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI, Event.class);
    }

    @Override
    public ProposalTypes getProposalTypes() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/proposalTypes", ProposalTypes.class);
    }

    @Override
    public Rooms getRooms() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/rooms/", Rooms.class);
    }

    @Override
    public Schedule getSchedule(final String day) {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/schedules/" + day, Schedule.class);
    }

    @Override
    public Schedule getSchedule(final String day, final String room) {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/rooms/" + room + "/" + day, Schedule.class);
    }

    @Override
    public Schedules getSchedules() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/schedules/", Schedules.class);
    }

    @Override
    public List<Speaker> getSpeakers() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/speakers/", new GenericType<List<Speaker>>() {
        });
    }

    @Override
    public Speaker getSpeaker(final String speakerId) {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/speakers/" + speakerId, Speaker.class);
    }

    @Override
    public Talk getTalk(final String talkId) {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/talks/" + talkId, Talk.class);
    }

    @Override
    public Tracks getTracks() {
        return RestCallHelper.getData(CONFERENCE_BASE_URI + "/tracks", Tracks.class);
    }

    @Override
    public VotingResults getVotingResultsOverall() {
        LOGGER.warn("Currently using Devoxx UK voting results! Correct to Devoxx 2017 BE Voting results!!!");
        return RestCallHelper.getData("https://cfp.devoxx.co.uk/api/voting/v1/top/talks", VotingResults.class);
    }

    @Override
    public VotingResults getVotingResultsDaily(String day) {
        LOGGER.warn("Currently using Devoxx UK voting results! Correct to Devoxx 2017 BE Voting results!!!");
        return RestCallHelper.getData("https://cfp.devoxx.co.uk/api/voting/v1/top/talks", VotingResults.class, Collections.singletonMap("day", day));
    }
}
