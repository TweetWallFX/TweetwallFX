/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 TweetWallFX
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
package org.tweetwallfx.devoxx.cfp.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.GenericType;
import org.tweetwallfx.devoxx.api.cfp.client.CFPClient;
import org.tweetwallfx.devoxx.api.cfp.client.Event;
import org.tweetwallfx.devoxx.api.cfp.client.Events;
import org.tweetwallfx.devoxx.api.cfp.client.ProposalTypes;
import org.tweetwallfx.devoxx.api.cfp.client.Rooms;
import org.tweetwallfx.devoxx.api.cfp.client.Schedule;
import org.tweetwallfx.devoxx.api.cfp.client.Schedules;
import org.tweetwallfx.devoxx.api.cfp.client.Speaker;
import org.tweetwallfx.devoxx.api.cfp.client.Talk;
import org.tweetwallfx.devoxx.api.cfp.client.Tracks;
import org.tweetwallfx.devoxx.api.cfp.client.VotingResults;
import static org.tweetwallfx.devoxx.api.cfp.client.impl.RestCallHelper.readOptionalFrom;
import org.tweetwallfx.config.Configuration;

public class ConfigurableCFPClientImpl implements CFPClient {

    private final String baseUri;
    private final String eventBaseUri;
    private final String votingResultsUri;

    public ConfigurableCFPClientImpl() {
        final CFPClientSettings cfpClientSettings = Configuration.getInstance().getConfigTyped(
                CFPClientSettings.CONFIG_KEY,
                CFPClientSettings.class);

        baseUri = cfpClientSettings.baseUri();
        eventBaseUri = baseUri + "/conferences/" + cfpClientSettings.eventId();
        votingResultsUri = cfpClientSettings.votingResultsUri();
    }

    @Override
    public boolean canGetVotingResults() {
        return null != votingResultsUri;
    }

    @Override
    public Optional<Event> getEvent() {
        return readOptionalFrom(eventBaseUri, Event.class);
    }

    @Override
    public Optional<Events> getEvents() {
        return readOptionalFrom(baseUri + "/conferences", Events.class);
    }

    @Override
    public Optional<ProposalTypes> getProposalTypes() {
        return readOptionalFrom(eventBaseUri + "/proposalTypes", ProposalTypes.class);
    }

    @Override
    public Optional<Rooms> getRooms() {
        return readOptionalFrom(eventBaseUri + "/rooms/", Rooms.class);
    }

    @Override
    public Optional<Schedule> getSchedule(final String day) {
        return readOptionalFrom(eventBaseUri + "/schedules/" + day, Schedule.class);
    }

    @Override
    public Optional<Schedule> getSchedule(final String day, final String room) {
        return readOptionalFrom(eventBaseUri + "/rooms/" + room + "/" + day, Schedule.class);
    }

    @Override
    public Optional<Schedules> getSchedules() {
        return readOptionalFrom(eventBaseUri + "/schedules/", Schedules.class);
    }

    @Override
    public Optional<Speaker> getSpeaker(final String speakerId) {
        return readOptionalFrom(eventBaseUri + "/speakers/" + speakerId, Speaker.class);
    }

    @Override
    public List<Speaker> getSpeakers() {
        return readOptionalFrom(eventBaseUri + "/speakers/", new GenericType<List<Speaker>>() {
        }).orElse(Collections.emptyList());
    }

    @Override
    public Optional<Talk> getTalk(final String talkId) {
        return readOptionalFrom(eventBaseUri + "/talks/" + talkId, Talk.class);
    }

    @Override
    public Optional<Tracks> getTracks() {
        return readOptionalFrom(eventBaseUri + "/tracks", Tracks.class);
    }

    @Override
    public Optional<VotingResults> getVotingResultsDaily(final String day) {
        return Optional.ofNullable(votingResultsUri)
                .flatMap(uri -> readOptionalFrom(uri, Collections.singletonMap("day", day), VotingResults.class));
    }

    @Override
    public Optional<VotingResults> getVotingResultsOverall() {
        return Optional.ofNullable(votingResultsUri)
                .flatMap(uri -> readOptionalFrom(uri, VotingResults.class));
    }
}
