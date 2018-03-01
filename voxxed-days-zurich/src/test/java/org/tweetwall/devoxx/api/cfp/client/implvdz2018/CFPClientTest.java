/*
 * The MIT License
 *
 * Copyright 2014-2018 TweetWallFX
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
package org.tweetwall.devoxx.api.cfp.client.implvdz2018;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Event;
import org.tweetwall.devoxx.api.cfp.client.Events;
import org.tweetwall.devoxx.api.cfp.client.ProposalTypes;
import org.tweetwall.devoxx.api.cfp.client.Rooms;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwall.devoxx.api.cfp.client.Schedules;
import org.tweetwall.devoxx.api.cfp.client.Speaker;
import org.tweetwall.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwall.devoxx.api.cfp.client.Talk;
import org.tweetwall.devoxx.api.cfp.client.Tracks;
import org.tweetwall.devoxx.api.cfp.client.VotingResults;

public class CFPClientTest {

    private static final String DAY = "thursday";
    private static final String ROOM_ID = "c_room7";
    private static final String TALK_ID = "JIW-1821";
    private static final String USER_ID = "868aee10db897b002186a5bf1512bff21cb2f4ca";
    private static final String USER_TWITTER_HANDLE = "@ixchelruiz";

    @Rule
    public TestName testName = new TestName();
    private CFPClient client;

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
        client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Test
    public void clientImplIsFound() {
        assertEquals(1, CFPClient.getClientStream().count());
    }

    @Test
    public void eventsAreRetrievable() {
        final Optional<Events> eventsOptional = client.getEvents();
        System.out.println("eventsOptional: " + eventsOptional);
        assertTrue(eventsOptional.isPresent());

        final Events events = eventsOptional
                .get();
        System.out.println("events: " + events);
        assertNotNull(events);
    }

    @Test
    public void eventIsRetrievable() {
        final Optional<Event> eventOptional = client.getEvent();
        System.out.println("eventOptional: " + eventOptional);
        assertTrue(eventOptional.isPresent());

        final Event event = eventOptional
                .get();
        System.out.println("event: " + event);
        assertNotNull(event);
    }

    @Test
    public void proposalTypesAreRetrievable() {
        final Optional<ProposalTypes> proposalTypesOptional = client.getProposalTypes();
        System.out.println("proposalTypesOptional: " + proposalTypesOptional);
        assertTrue(proposalTypesOptional.isPresent());

        final ProposalTypes proposalTypes = proposalTypesOptional
                .get();
        System.out.println("proposalTypes: " + proposalTypes);
        assertNotNull(proposalTypes);
    }

    @Test
    public void roomsAreRetrievable() {
        final Optional<Rooms> roomsOptional = client.getRooms();
        System.out.println("roomsOptional: " + roomsOptional);
        assertTrue(roomsOptional.isPresent());

        final Rooms rooms = roomsOptional
                .get();
        System.out.println("rooms: " + rooms);
        assertNotNull(rooms);
    }

    @Test
    public void schedulesAreRetrievable() {
        final Optional<Schedules> schedulesOptional = client.getSchedules();
        System.out.println("schedulesOptional: " + schedulesOptional);
        assertTrue(schedulesOptional.isPresent());

        final Schedules schedules = schedulesOptional
                .get();
        System.out.println("schedules: " + schedules);
        assertNotNull(schedules);
        assertTrue(schedules.getSchedules().count() > 0);
    }

    @Test
    public void scheduleIsRetrievableForADay() {
        final Optional<Schedule> scheduleOptional = client.getSchedule(DAY);
        System.out.println("scheduleOptional: " + scheduleOptional);
        assertTrue(scheduleOptional.isPresent());

        final Schedule schedule = scheduleOptional
                .get();
        System.out.println("schedule: " + schedule);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void scheduleIsRetrievableForADayAndRoom() {
        final Optional<Schedule> scheduleOptional = client.getSchedule(DAY, ROOM_ID);
        System.out.println("scheduleOptional: " + scheduleOptional);
        assertTrue(scheduleOptional.isPresent());

        final Schedule schedule = scheduleOptional
                .get();
        System.out.println("schedule: " + schedule);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void speakersAreRetrievable() {
        final List<Speaker> speakers = client.getSpeakers();
        System.out.println("speakers: " + convertCollectionForToString(speakers));
        assertNotNull(speakers);
    }

    @Test
    public void speakerInformationIsCompletable() {
        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                .filter(s -> USER_TWITTER_HANDLE.equals(s.getTwitter()))
                .findAny();
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());
        assertFalse(speakerOptional.get().hasCompleteInformation());

        final Optional<Speaker> speakerOptionalReload = speakerOptional
                .flatMap(Speaker::reload);
        System.out.println("speakerOptionalReload: " + speakerOptionalReload);
        assertTrue(speakerOptionalReload.isPresent());
        assertTrue(speakerOptionalReload.get().hasCompleteInformation());

        Speaker speaker = speakerOptionalReload.get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void speakerIsRetrievable() {
        final Optional<Speaker> speakerOptional = client.getSpeaker(USER_ID);
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());

        final Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertSame(speaker, speaker.reload().get());
    }

    @Test
    public void talkInformationIsCompletable() {
        final Optional<Speaker> speakerOptional = client.getSpeaker(USER_ID);
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());

        final Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertNotNull(speaker.getAcceptedTalks());
        assertFalse(speaker.getAcceptedTalks().isEmpty());

        final Talk incompleteTalk = speaker.getAcceptedTalks().get(0);
        assertNotNull(incompleteTalk);
        assertFalse(incompleteTalk.hasCompleteInformation());
        System.out.println("incompleteTalk: " + incompleteTalk);

        final Optional<Talk> incompleteTalkOptionalReload = incompleteTalk.reload();
        System.out.println("incompleteTalkOptionalReload: " + incompleteTalkOptionalReload);
        assertTrue(incompleteTalkOptionalReload.isPresent());
        assertTrue(incompleteTalkOptionalReload.get().hasCompleteInformation());

        final Talk completeTalk = incompleteTalkOptionalReload.get();
        assertNotNull(completeTalk);
        assertTrue(completeTalk.hasCompleteInformation());
        System.out.println("completeTalk: " + completeTalk);
    }

    @Test
    public void talkCanGetSpeakers() {
        final Optional<Talk> talkOptional = client.getTalk(TALK_ID);
        System.out.println("talkOptional: " + talkOptional);
        assertTrue(talkOptional.isPresent());

        final Talk talk = talkOptional
                .get();
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());

        final Set<Speaker> speakers = talk
                .getSpeakers()
                .stream()
                .map(SpeakerReference::getSpeaker)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        assertSame(talk.getSpeakers().size(), speakers.size());

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                .filter(s -> USER_TWITTER_HANDLE.equals(s.getTwitter()))
                .findAny();
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());
        assertTrue(speakerOptional.get().hasCompleteInformation());

        Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void talkIsRetrievable() {
        final Optional<Talk> talkOptional = client.getTalk(TALK_ID);
        System.out.println("talkOptional: " + talkOptional);
        assertTrue(talkOptional.isPresent());

        final Talk talk = talkOptional
                .get();
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());
        assertSame(talk, talk.reload().get());
    }

    @Test
    public void tracksAreRetrievable() {
        final Optional<Tracks> tracksOptional = client.getTracks();
        System.out.println("tracksOptional: " + tracksOptional);
        assertTrue(tracksOptional.isPresent());

        final Tracks tracks = tracksOptional
                .get();
        System.out.println("tracks: " + tracks);
        assertNotNull(tracks);
    }

    @Test
//    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsOverallAreRetrievable() {
        final Optional<VotingResults> votingResults = client.getVotingResultsOverall();
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
    }

    @Test
//    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsDailyAreRetrievable() {
        final Optional<VotingResults> votingResults = client.getVotingResultsDaily(DAY);
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
    }

    private static String convertCollectionForToString(final Collection<?> collection) {
        if (null == collection) {
            return null;
        }

        return collection
                .stream()
                .map(Object::toString)
                .map(s -> s.replaceAll("\n", "\n        "))
                .collect(Collectors.joining(
                        ",\n        ",
                        "[\n        ",
                        "\n    ]"));
    }
}
