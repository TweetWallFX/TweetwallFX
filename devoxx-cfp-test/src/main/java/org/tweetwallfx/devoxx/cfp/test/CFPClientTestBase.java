/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 TweetWallFX
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
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
import org.tweetwallfx.devoxx.cfp.impl.CFPClientSettings;
import org.tweetwallfx.devoxx.cfp.impl.ConfigurableCFPClientImpl;

public abstract class CFPClientTestBase {

    private static final Logger LOG = LogManager.getLogger(CFPClientTestBase.class);
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
        LOG.info("Test of CFP Client against live system is {}.", testingLive ? "enabled" : "disabled");

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
        LOG.info("#################### START: {} ####################", testName.getMethodName());
    }

    @After
    public void after() {
        LOG.info("####################   END: {} ####################", testName.getMethodName());
    }

    private static void ignoreIfServerUnreachable() {
        if (!CFP_REACHABLE) {
            LOG.info("CFP Server is unreachable");
            Assume.assumeTrue(false);
        }
    }

    private CFPClient getCFPClient() {
        final CFPClient client = CFPClient.getClient();
        LOG.info("client: " + client);
        assertNotNull(client);

        return client;
    }

    private Talk getConfiguredTalk() {
        final Talk talk = getCFPClient().getTalk(talkId)
                .orElseThrow(() -> new IllegalStateException("Talk unretrievable"));
        LOG.info("talk: {}", talk);
        return talk;
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

        final Events events = client.getEvents()
                .orElseThrow(() -> new IllegalStateException("Events unretrievable"));
        LOG.info("events: {}", events);
    }

    @Test
    public void eventIsRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Event event = client.getEvent()
                .orElseThrow(() -> new IllegalStateException("Event unretrievable"));
        LOG.info("event: {}", event);
    }

    @Test
    public void proposalTypesAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final ProposalTypes proposalTypes = client.getProposalTypes()
                .orElseThrow(() -> new IllegalStateException("ProposalTypes unretrievable"));
        LOG.info("proposalTypes: {}", proposalTypes);
    }

    @Test
    public void roomsAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Rooms rooms = client.getRooms()
                .orElseThrow(() -> new IllegalStateException("Rooms unretrievable"));
        LOG.info("rooms: {}", rooms);
    }

    @Test
    public void schedulesAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Schedules schedules = client.getSchedules()
                .orElseThrow(() -> new IllegalStateException("Schedules unretrievable"));
        LOG.info("schedules: {}", schedules);
        assertTrue(schedules.getSchedules().count() > 0);
    }

    @Test
    public void scheduleIsRetrievableForADay() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Schedule schedule = client.getSchedule(getConferenceDay())
                .orElseThrow(() -> new IllegalStateException("Schedule unretrievable"));
        LOG.info("schedule: {}", schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void scheduleIsRetrievableForADayAndRoom() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Schedule schedule = client.getSchedule(getConferenceDay(), getConferenceRoom())
                .orElseThrow(() -> new IllegalStateException("Schedule unretrievable"));
        LOG.info("schedule: {}", schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void speakersAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);
        LOG.info("speakers: {}", convertCollectionForToString(speakers));
    }

    @Test
    public void speakerInformationIsCompletable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);

        final Speaker speaker = speakers
                .stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
        LOG.info("speaker: {}", speaker);
        assertFalse(speaker.hasCompleteInformation());

        final Speaker speakerReload = speaker
                .reload()
                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
        LOG.info("speakerReload: {}", speakerReload);
        assertTrue(speakerReload.hasCompleteInformation());
    }

    @Test
    public void speakerIsRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Speaker speaker = client.getSpeakers().stream().findFirst()
                .map(Speaker::getUuid)
                .flatMap(client::getSpeaker)
                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
        LOG.info("speaker: {}", speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertEquals(Optional.of(speaker), speaker.reload());
    }

    @Test
    public void talkInformationIsCompletable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Speaker speaker = client.getSpeakers().stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
        LOG.info("speaker: {}", speaker);

        assertFalse(speaker.hasCompleteInformation());
        assertNotNull(speaker.getAcceptedTalks());
        assertTrue(speaker.getAcceptedTalks().isEmpty());

        final Speaker speakerReload = speaker
                .reload()
                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
        LOG.info("speakerReload: {}", speakerReload);
        assertTrue(speakerReload.hasCompleteInformation());
        assertNotNull(speakerReload.getAcceptedTalks());
        assertFalse(speakerReload.getAcceptedTalks().isEmpty());

        final Talk incompleteTalk = speakerReload.getAcceptedTalks().get(0);
        assertNotNull(incompleteTalk);
        assertFalse(incompleteTalk.hasCompleteInformation());
        LOG.info("incompleteTalk: {}", incompleteTalk);

        final Talk completeTalk = incompleteTalk.reload()
                .orElseThrow(() -> new IllegalStateException("Talk reload failed"));
        LOG.info("completeTalk: {}", completeTalk);
        assertTrue(completeTalk.hasCompleteInformation());
    }

    @Test
    public void talkCanGetSpeakers() {
        ignoreIfServerUnreachable();

        final Talk talk = getConfiguredTalk();
        assertTrue(talk.hasCompleteInformation());

        final Set<Speaker> speakers = talk
                .getSpeakers()
                .stream()
                .map(SpeakerReference::getSpeaker)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        assertFalse("Talk has Speakers", talk.getSpeakers().isEmpty());
        assertSame(talk.getSpeakers().size(), speakers.size());

        final Speaker speaker = speakers.stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
        LOG.info("speaker: {}", speaker);
        assertTrue(speaker.hasCompleteInformation());
    }

    @Test
    public void talkIsRetrievable() {
        ignoreIfServerUnreachable();

        final Talk talk = getConfiguredTalk();
        assertTrue(talk.hasCompleteInformation());
        assertEquals(Optional.of(talk), talk.reload());
    }

    @Test
    public void tracksAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Tracks tracks = client.getTracks()
                .orElseThrow(() -> new IllegalStateException("Tracks unretrievable"));
        LOG.info("tracks: {}", tracks);
    }

    @Test
    public void votingResultsOverallAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();
        Assume.assumeTrue(client.canGetVotingResults());

        client.getVotingResultsOverall()
                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
    }

    @Test
    public void votingResultsDailyAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();
        Assume.assumeTrue(client.canGetVotingResults());

        client.getVotingResultsDaily(getConferenceDay())
                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
    }

    @Test
    @Ignore
    public void speakerAvatarsAreLoadable() {
        ignoreIfServerUnreachable();
        final CFPClient client = getCFPClient();

        final Map<Boolean, List<Speaker>> avatarsLoadable = client.getSpeakers().stream().collect(Collectors.partitioningBy(speaker -> {
            System.out.println("Checking avator for " + speaker.getFirstName() + ' ' + speaker.getLastName() + " at '" + speaker.getAvatarURL() + "'");
            try (final InputStream is = new URL(speaker.getAvatarURL()).openStream()) {
                System.out.println("Succeeded");
                return null != is;
            } catch (final IOException ioe) {
                System.out.println("Succeeded");
                System.out.println("Failed loading for: " + speaker);
                ioe.printStackTrace(System.out);
                return false;
            }
        }));

        assertThat("Some avatar images are not loadable", avatarsLoadable.get(false), CoreMatchers.equalTo(new ArrayList<>()));
    }

    private static String convertCollectionForToString(final Collection<?> collection) {
        if (null == collection) {
            return null;
        }

        return collection
                .stream()
                .map(Object::toString)
                .map(s -> s.replaceAll("\n", "\n    "))
                .collect(Collectors.joining(
                        ",\n    ",
                        "[\n    ",
                        "\n]"));
    }
}
