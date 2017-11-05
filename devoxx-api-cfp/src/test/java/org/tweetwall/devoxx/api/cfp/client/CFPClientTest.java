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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
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

        final Events events = client.getEvents();
        System.out.println("events: " + events);
        assertNotNull(events);
    }

    @Test
    public void eventIsRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Event event = client.getEvent();
        System.out.println("event: " + event);
        assertNotNull(event);
    }

    @Test
    public void proposalTypesAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final ProposalTypes proposalTypes = client.getProposalTypes();
        System.out.println("proposalTypes: " + proposalTypes);
        assertNotNull(proposalTypes);
    }

    @Test
    public void roomsAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Rooms rooms = client.getRooms();
        System.out.println("rooms: " + rooms);
        assertNotNull(rooms);
    }

    @Test
    public void schedulesAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Schedules schedules = client.getSchedules();
        System.out.println("schedules: " + schedules);
        assertNotNull(schedules);
        assertTrue(schedules.getSchedules().count() > 0);
    }

    @Test
    public void scheduleIsRetrievableForADay() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Schedule schedule = client.getSchedule("monday");
        System.out.println("schedule: " + schedule);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void scheduleIsRetrievableForADayAndRoom() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Schedule schedule = client.getSchedule("monday", "room8");
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
        System.out.println("speakers: " + Helper.convertCollectionForToString(speakers));
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

        Speaker speaker = speakerOptional
                .map(Speaker::reload)
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void speakerIsRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Speaker speaker = client.getSpeaker("8a7d68a8a2b09105c969cbae7b37019d4fa470a5");
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertSame(speaker, speaker.reload());
    }

    @Test
    public void talkInformationIsCompletable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Speaker speaker = client.getSpeaker("8a7d68a8a2b09105c969cbae7b37019d4fa470a5");
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertNotNull(speaker.getAcceptedTalks());
        assertFalse(speaker.getAcceptedTalks().isEmpty());

        final Talk incompleteTalk = speaker.getAcceptedTalks().get(0);
        assertNotNull(incompleteTalk);
        assertFalse(incompleteTalk.hasCompleteInformation());
        System.out.println("incompleteTalk: " + incompleteTalk);

        final Talk completeTalk = incompleteTalk.reload();
        assertNotNull(completeTalk);
        assertTrue(completeTalk.hasCompleteInformation());
        System.out.println("completeTalk: " + completeTalk);
    }

    @Test
    public void talkCanGetSpeakers() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Talk talk = client.getTalk("OZB-4067");
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());

        final Set<Speaker> speakers = talk.getSpeakers().stream().map(SpeakerReference::getSpeaker).collect(Collectors.toSet());
        assertSame(talk.getSpeakers().size(), speakers.size());

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                .filter(s -> "@mreinhold".equals(s.getTwitter()))
                .findAny();
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());
        assertTrue(speakerOptional.get().hasCompleteInformation());

        Speaker speaker = speakerOptional
                .map(Speaker::reload)
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void talkIsRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Talk talk = client.getTalk("OZB-4067");
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());
        assertSame(talk, talk.reload());
    }

    @Test
    public void tracksAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Tracks tracks = client.getTracks();
        System.out.println("tracks: " + tracks);
        assertNotNull(tracks);
    }

    @Test
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
    public void votingResultsDailyAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<VotingResults> votingResults = client.getVotingResultsDaily("monday");
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
        assertTrue(votingResults.isPresent());
    }
}
