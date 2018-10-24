/*
 * The MIT License
 *
 * Copyright 2017-2018 TweetWallFX
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
package org.tweetwallfx.devoxx.cfp.test;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.devoxx.api.cfp.client.CFPClient;
import org.tweetwallfx.devoxx.api.cfp.client.Event;
import org.tweetwallfx.devoxx.api.cfp.client.Events;
import org.tweetwallfx.devoxx.api.cfp.client.ProposalTypes;
import org.tweetwallfx.devoxx.api.cfp.client.Rooms;
import org.tweetwallfx.devoxx.api.cfp.client.Schedule;
import org.tweetwallfx.devoxx.api.cfp.client.Schedules;
import org.tweetwallfx.devoxx.api.cfp.client.Speaker;
import org.tweetwallfx.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwallfx.devoxx.api.cfp.client.Talk;
import org.tweetwallfx.devoxx.api.cfp.client.Tracks;
import org.tweetwallfx.devoxx.api.cfp.client.VotingResults;
import org.tweetwallfx.devoxx.cfp.impl.CFPClientSettings;
import org.tweetwallfx.devoxx.cfp.impl.ConfigurableCFPClientImpl;

public abstract class CFPClientTestBase {

    private static final boolean CFP_REACHABLE = isCfpReachable();

    @Rule
    public TestName testName = new TestName();
    private final String conferenceDay;
    private final String conferenceRoom;
    private final String talkId;

    protected CFPClientTestBase(final String conferenceDay, final String conferenceRoom, final String talkId) {
        this.conferenceDay = Objects.requireNonNull(conferenceDay, "conferenceDay must not be null");
        this.conferenceRoom = Objects.requireNonNull(conferenceRoom, "conferenceRoom must not be null");
        this.talkId = Objects.requireNonNull(talkId, "talkId must not be null");
    }

    private static boolean isCfpReachable() {
        final boolean testingLive = Boolean.getBoolean("org.tweetwallfx.tests.executeCFPClientLiveTests");
        System.out.println(String.format(
                "Test of CFP Client against live system is %s.",
                testingLive ? "enabled" : "disabled"));

        try {
            return testingLive && Response.Status.Family.SUCCESSFUL == ClientBuilder.newClient()
                    .target(Configuration.getInstance().getConfigTyped(
                            CFPClientSettings.CONFIG_KEY,
                            CFPClientSettings.class).getBaseUri())
                    .request(MediaType.APPLICATION_JSON)
                    .get()
                    .getStatusInfo()
                    .getFamily();
        } catch (final ProcessingException pe) {
            LogManager.getLogger(CFPClientTestBase.class).error(pe, pe);
            return false;
        }
    }

    private String getConferenceDay() {
        return conferenceDay;
    }

    private String getConferenceRoom() {
        return conferenceRoom;
    }

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    private static void ignoreIfServerUnreachable() {
        if (!CFP_REACHABLE) {
            System.out.println("CFP Server is unreachable");
            Assume.assumeTrue(false);
        }
    }

    private CFPClient getCFPClient() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        return client;
    }

    @Test
    public void clientImplIsFound() {
        final CFPClient client = getCFPClient();
        assertEquals(1, CFPClient.getClientStream().count());
        assertEquals(ConfigurableCFPClientImpl.class, client.getClass());
    }

    @Test
    public void eventsAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Schedule> scheduleOptional = client.getSchedule(getConferenceDay());
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Schedule> scheduleOptional = client.getSchedule(getConferenceDay(), getConferenceRoom());
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        System.out.println("speakers: " + convertCollectionForToString(speakers));
        assertNotNull(speakers);
    }

    @Test
    public void speakerInformationIsCompletable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);

        final Optional<Speaker> speakerOptional = speakers
                .stream()
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Speaker> speakerOptional = client.getSpeakers().stream().findFirst()
                .map(Speaker::getUuid)
                .flatMap(client::getSpeaker);
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Speaker> speakerOptional = client.getSpeakers().stream().findFirst()
                .map(Speaker::getUuid)
                .flatMap(client::getSpeaker);
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Talk> talkOptional = client.getTalk(talkId);
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Talk> talkOptional = client.getTalk(talkId);
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
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Optional<Tracks> tracksOptional = client.getTracks();
        System.out.println("tracksOptional: " + tracksOptional);
        assertTrue(tracksOptional.isPresent());

        final Tracks tracks = tracksOptional
                .get();
        System.out.println("tracks: " + tracks);
        assertNotNull(tracks);
    }

    @Test
    public void votingResultsOverallAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();
        Assume.assumeTrue(client.canGetVotingResults());

        final Optional<VotingResults> votingResults = client.getVotingResultsOverall();
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
    }

    @Test
    public void votingResultsDailyAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();
        Assume.assumeTrue(client.canGetVotingResults());

        final Optional<VotingResults> votingResults = client.getVotingResultsDaily(getConferenceDay());
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
