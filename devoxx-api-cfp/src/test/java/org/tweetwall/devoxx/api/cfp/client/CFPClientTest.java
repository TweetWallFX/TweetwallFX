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

public class CFPClientTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Test
    public void clientImplIsFound() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);
        assertEquals(1, CFPClient.getClientStream().count());
    }

    @Test
    public void eventsAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Schedule> scheduleOptional = client.getSchedule("monday");
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Schedule> scheduleOptional = client.getSchedule("monday", "room8");
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final List<Speaker> speakers = client.getSpeakers();
        System.out.println("speakers: " + convertCollectionForToString(speakers));
        assertNotNull(speakers);
    }

    @Test
    public void speakerInformationIsCompletable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                .filter(s -> "@mreinhold".equals(s.getTwitter()))
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Speaker> speakerOptional = client.getSpeaker("8a7d68a8a2b09105c969cbae7b37019d4fa470a5");
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Speaker> speakerOptional = client.getSpeaker("8a7d68a8a2b09105c969cbae7b37019d4fa470a5");
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Talk> talkOptional = client.getTalk("OZB-4067");
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
                .filter(s -> "@mreinhold".equals(s.getTwitter()))
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Talk> talkOptional = client.getTalk("OZB-4067");
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
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Tracks> tracksOptional = client.getTracks();
        System.out.println("tracksOptional: " + tracksOptional);
        assertTrue(tracksOptional.isPresent());

        final Tracks tracks = tracksOptional
                .get();
        System.out.println("tracks: " + tracks);
        assertNotNull(tracks);
    }

    @Test
    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsOverallAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<VotingResults> votingResults = client.getVotingResultsOverall();
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
        assertTrue(votingResults.isPresent());
    }

    @Test
    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsDailyAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<VotingResults> votingResults = client.getVotingResultsDaily("monday");
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
        assertTrue(votingResults.isPresent());
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
