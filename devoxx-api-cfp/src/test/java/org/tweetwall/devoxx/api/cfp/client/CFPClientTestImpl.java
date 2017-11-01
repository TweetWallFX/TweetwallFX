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
import javax.ws.rs.core.GenericType;
import org.tweetwall.devoxx.api.cfp.client.impl.RestCallHelper;

/**
 * Test impl of CFPClient working with DevoxxBE2017
 */
public class CFPClientTestImpl implements CFPClient {

    private static final String BASE_URI = "https://cfp.devoxx.be/api/";

    @Override
    public Events getEvents() {
        return RestCallHelper.getData(BASE_URI + "conferences/", Events.class);
    }

    @Override
    public Event getEvent() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17", Event.class);
    }

    @Override
    public ProposalTypes getProposalTypes() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/proposalTypes", ProposalTypes.class);
    }

    @Override
    public Rooms getRooms() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/rooms/", Rooms.class);
    }

    @Override
    public Schedule getSchedule(final String day) {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/schedules/" + day, Schedule.class);
    }

    @Override
    public Schedule getSchedule(final String day, final String room) {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/rooms/" + room + "/" + day, Schedule.class);
    }

    @Override
    public Schedules getSchedules() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/schedules/", Schedules.class);
    }

    @Override
    public List<Speaker> getSpeakers() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/speakers/", new GenericType<List<Speaker>>() {
        });
    }

    @Override
    public Speaker getSpeaker(final String speakerId) {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/speakers/" + speakerId, Speaker.class);
    }

    @Override
    public Talk getTalk(final String talkId) {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/talks/" + talkId, Talk.class);
    }

    @Override
    public Tracks getTracks() {
        return RestCallHelper.getData(BASE_URI + "conferences/DVBE17/tracks", Tracks.class);
    }

    @Override
    public VotingResults getVotingResultsOverall() {
//        return RestCallHelper.getData(BASE_URI + "voting/v1/top/talks/", VotingResults.class);
        return RestCallHelper.getData("https://cfp.devoxx.co.uk/api/voting/v1/top/talks", VotingResults.class);
    }

    @Override
    public VotingResults getVotingResultsDaily(String day) {
//        return RestCallHelper.getData(BASE_URI + "voting/v1/top/talks", VotingResults.class, Collections.singletonMap("day", day));
        return RestCallHelper.getData("https://cfp.devoxx.co.uk/api/voting/v1/top/talks", VotingResults.class, Collections.singletonMap("day", day));
    }
}
