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
import java.util.Optional;
import javax.ws.rs.core.GenericType;
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
import static org.tweetwall.devoxx.api.cfp.client.impl.RestCallHelper.*;

/**
 * CFPClient working with DevoxxBE2017.
 */
public class CFPClientDevoxx2017BE implements CFPClient {

    private static final String BASE_URI = "https://cfp.devoxx.be/api/";
    private static final String CONFERENCE_BASE_URI = "https://cfp.devoxx.be/api/conferences/DVBE17";

    @Override
    public Optional<Events> getEvents() {
        return getOptionalResponse(BASE_URI + "conferences/")
                .flatMap(response -> readOptionalFrom(response, Events.class));
    }

    @Override
    public Optional<Event> getEvent() {
        return getOptionalResponse(CONFERENCE_BASE_URI)
                .flatMap(response -> readOptionalFrom(response, Event.class));
    }

    @Override
    public Optional<ProposalTypes> getProposalTypes() {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/proposalTypes")
                .flatMap(response -> readOptionalFrom(response, ProposalTypes.class));
    }

    @Override
    public Optional<Rooms> getRooms() {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/rooms/")
                .flatMap(response -> readOptionalFrom(response, Rooms.class));
    }

    @Override
    public Optional<Schedule> getSchedule(final String day) {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/schedules/" + day)
                .flatMap(response -> readOptionalFrom(response, Schedule.class));
    }

    @Override
    public Optional<Schedule> getSchedule(final String day, final String room) {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/rooms/" + room + "/" + day)
                .flatMap(response -> readOptionalFrom(response, Schedule.class));
    }

    @Override
    public Optional<Schedules> getSchedules() {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/schedules/")
                .flatMap(response -> readOptionalFrom(response, Schedules.class));
    }

    @Override
    public List<Speaker> getSpeakers() {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/speakers/")
                .flatMap(response -> readOptionalFrom(response, new GenericType<List<Speaker>>() {
        }))
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Speaker> getSpeaker(final String speakerId) {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/speakers/" + speakerId)
                .flatMap(response -> readOptionalFrom(response, Speaker.class));
    }

    @Override
    public Optional<Talk> getTalk(final String talkId) {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/talks/" + talkId)
                .flatMap(response -> readOptionalFrom(response, Talk.class));
    }

    @Override
    public Optional<Tracks> getTracks() {
        return getOptionalResponse(CONFERENCE_BASE_URI + "/tracks")
                .flatMap(response -> readOptionalFrom(response, Tracks.class));
    }

    @Override
    public Optional<VotingResults> getVotingResultsOverall() {
        return getOptionalResponse("https://cfp.devoxx.be/api/voting/v1/top/talks")
                .flatMap(response -> readOptionalFrom(response, VotingResults.class));
    }

    @Override
    public Optional<VotingResults> getVotingResultsDaily(final String day) {
        return getOptionalResponse("https://cfp.devoxx.be/api/voting/v1/top/talks", Collections.singletonMap("day", day))
                .flatMap(response -> readOptionalFrom(response, VotingResults.class));
    }
}
