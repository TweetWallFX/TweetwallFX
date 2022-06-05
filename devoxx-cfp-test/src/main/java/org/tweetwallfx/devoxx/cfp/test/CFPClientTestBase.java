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
package org.tweetwallfx.devoxx.cfp.test;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class CFPClientTestBase {

    private static final Logger LOG = LogManager.getLogger(CFPClientTestBase.class);
    private static final AtomicBoolean CFP_REACHABLE = new AtomicBoolean(false);

    private final Class<? extends CFPClient> expectedCfpClient;
    private final String conferenceDay;
    private final String conferenceRoom;
    private final String talkId;

    protected CFPClientTestBase(
            final Class<? extends CFPClient> expectedCfpClient,
            final String conferenceDay,
            final String conferenceRoom,
            final String talkId) {
        this.expectedCfpClient = expectedCfpClient;
        this.conferenceDay = Objects.requireNonNull(conferenceDay, "conferenceDay must not be null");
        this.conferenceRoom = Objects.requireNonNull(conferenceRoom, "conferenceRoom must not be null");
        this.talkId = Objects.requireNonNull(talkId, "talkId must not be null");
    }

    protected static final void checkCfpReachable(final String baseUri) {
        final boolean testingLive = Boolean.getBoolean("org.tweetwallfx.tests.executeCFPClientLiveTests");
        LOG.info("Test of CFP Client against live system is {}.", testingLive ? "enabled" : "disabled");

        if (!testingLive) {
            return;
        }

        try {
            LOG.info("Checking if CFP is reachable at {}", baseUri);
            final Response response = ClientBuilder.newClient()
                    .target(baseUri)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            LOG.info("Received {}", response);
            CFP_REACHABLE.set(Response.Status.Family.SUCCESSFUL == response
                    .getStatusInfo()
                    .getFamily());
        } catch (final ProcessingException pe) {
            LogManager.getLogger(CFPClientTestBase.class).error(pe, pe);
        }
    }

    private String getConferenceDay() {
        return conferenceDay;
    }

    private String getConferenceRoom() {
        return conferenceRoom;
    }

    protected final static boolean serverReachable() {
        if (!CFP_REACHABLE.get()) {
            LOG.info("CFP Server is unreachable");
            return false;
        }
        return true;
    }

    private CFPClient getCFPClient() {
        final CFPClient client = CFPClient.getClient();
        LOG.info("client: " + client);
        assertThat(client).isNotNull();

        return client;
    }

    private Talk getConfiguredTalk() {
        final Talk talk = getCFPClient().getTalk(talkId)
                .orElseThrow(() -> new IllegalStateException("Talk unretrievable"));
        LOG.info("talk: {}", talk);
        return talk;
    }

    @Test
    void clientImplIsFound() {
        final CFPClient client = getCFPClient();
        assertThat(CFPClient.getClientStream().count()).isEqualTo(1);
        assertThat(client.getClass()).isEqualTo(expectedCfpClient);
    }

    @Test
    @EnabledIf("serverReachable")
    void eventsAreRetrievable() {
        final CFPClient client = getCFPClient();

        final Events events = client.getEvents()
                .orElseThrow(() -> new IllegalStateException("Events unretrievable"));
        LOG.info("events: {}", events);
    }

    @Test
    @EnabledIf("serverReachable")
    void eventIsRetrievable() {
        final CFPClient client = getCFPClient();

        final Event event = client.getEvent()
                .orElseThrow(() -> new IllegalStateException("Event unretrievable"));
        LOG.info("event: {}", event);
    }

    @Test
    @EnabledIf("serverReachable")
    void proposalTypesAreRetrievable() {
        final CFPClient client = getCFPClient();

        final ProposalTypes proposalTypes = client.getProposalTypes()
                .orElseThrow(() -> new IllegalStateException("ProposalTypes unretrievable"));
        LOG.info("proposalTypes: {}", proposalTypes);
    }

    @Test
    @EnabledIf("serverReachable")
    void roomsAreRetrievable() {
        final CFPClient client = getCFPClient();

        final Rooms rooms = client.getRooms()
                .orElseThrow(() -> new IllegalStateException("Rooms unretrievable"));
        LOG.info("rooms: {}", rooms);
    }

    @Test
    @EnabledIf("serverReachable")
    void schedulesAreRetrievable() {
        final CFPClient client = getCFPClient();

        final Schedules schedules = client.getSchedules()
                .orElseThrow(() -> new IllegalStateException("Schedules unretrievable"));
        LOG.info("schedules: {}", schedules);
        assertThat(schedules.getSchedules().count()).isGreaterThan(0);
    }

    @Test
    @EnabledIf("serverReachable")
    void scheduleIsRetrievableForADay() {
        final CFPClient client = getCFPClient();

        final Schedule schedule = client.getSchedule(getConferenceDay())
                .orElseThrow(() -> new IllegalStateException("Schedule unretrievable"));
        LOG.info("schedule: {}", schedule);
        assertThat(schedule.getSlots()).isEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void scheduleIsRetrievableForADayAndRoom() {
        final CFPClient client = getCFPClient();

        final Schedule schedule = client.getSchedule(getConferenceDay(), getConferenceRoom())
                .orElseThrow(() -> new IllegalStateException("Schedule unretrievable"));
        LOG.info("schedule: {}", schedule);
        assertThat(schedule.getSlots()).isNotEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void speakersAreRetrievable() {
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        assertThat(speakers).isNotNull();
        LOG.info("speakers: {}", convertCollectionForToString(speakers));
    }

    @Test
    @EnabledIf("serverReachable")
    void speakerInformationIsCompletable() {
        final CFPClient client = getCFPClient();

        final List<Speaker> speakers = client.getSpeakers();
        assertThat(speakers).isNotNull();

        final Speaker speaker = speakers
                .stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
        LOG.info("speaker: {}", speaker);
        assertThat(speaker.hasCompleteInformation()).isFalse();

        final Speaker speakerReload = speaker
                .reload()
                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
        LOG.info("speakerReload: {}", speakerReload);
        assertThat(speakerReload.hasCompleteInformation()).isTrue();
    }

    @Test
    @EnabledIf("serverReachable")
    void speakerIsRetrievable() {
        final CFPClient client = getCFPClient();

        final Speaker speaker = client.getSpeakers().stream().findFirst()
                .map(Speaker::getUuid)
                .flatMap(client::getSpeaker)
                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
        LOG.info("speaker: {}", speaker);
        assertThat(speaker.hasCompleteInformation()).isTrue();
        assertThat(speaker.reload()).contains(speaker);
    }

    @Test
    @EnabledIf("serverReachable")
    void talkInformationIsCompletable() {
        final CFPClient client = getCFPClient();

        final Speaker speaker = client.getSpeakers().stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
        LOG.info("speaker: {}", speaker);

        assertThat(speaker.hasCompleteInformation()).isFalse();
        assertThat(speaker.getAcceptedTalks()).isNull();
        assertThat(speaker.getAcceptedTalks()).isEmpty();

        final Speaker speakerReload = speaker
                .reload()
                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
        LOG.info("speakerReload: {}", speakerReload);
        assertThat(speakerReload.hasCompleteInformation()).isTrue();
        assertThat(speakerReload.getAcceptedTalks()).isNotNull();
        assertThat(speakerReload.getAcceptedTalks()).isEmpty();

        final Talk incompleteTalk = speakerReload.getAcceptedTalks().get(0);
        assertThat(incompleteTalk).isNotNull();
        assertThat(incompleteTalk.hasCompleteInformation()).isFalse();
        LOG.info("incompleteTalk: {}", incompleteTalk);

        final Talk completeTalk = incompleteTalk.reload()
                .orElseThrow(() -> new IllegalStateException("Talk reload failed"));
        LOG.info("completeTalk: {}", completeTalk);
        assertThat(completeTalk.hasCompleteInformation()).isTrue();
    }

    @Test
    @EnabledIf("serverReachable")
    void talkCanGetSpeakers() {

        final Talk talk = getConfiguredTalk();
        assertThat(talk.hasCompleteInformation()).isTrue();

        final Set<Speaker> speakers = talk
                .getSpeakers()
                .stream()
                .map(SpeakerReference::getSpeaker)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        assertThat(talk.getSpeakers()).isEmpty();
        assertThat(talk.getSpeakers().size()).isEqualTo(speakers.size());

        final Speaker speaker = speakers.stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
        LOG.info("speaker: {}", speaker);
        assertThat(speaker.hasCompleteInformation()).isTrue();
    }

    @Test
    @EnabledIf("serverReachable")
    void talkIsRetrievable() {

        final Talk talk = getConfiguredTalk();
        assertThat(talk.hasCompleteInformation()).isTrue();
        assertThat(talk.reload()).contains(talk);
    }

    @Test
    @EnabledIf("serverReachable")
    void tracksAreRetrievable() {
        final CFPClient client = getCFPClient();

        final Tracks tracks = client.getTracks()
                .orElseThrow(() -> new IllegalStateException("Tracks unretrievable"));
        LOG.info("tracks: {}", tracks);
    }

    @Test
    @EnabledIf("serverReachable")
    void votingResultsOverallAreRetrievable() {
        final CFPClient client = getCFPClient();
        assertThat(client.canGetVotingResults()).isTrue();

        final VotingResults votingResults = client.getVotingResultsOverall()
                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
        LOG.info("votingResults: {}", votingResults);
    }

    @Test
    @EnabledIf("serverReachable")
    void votingResultsDailyAreRetrievable() {
        final CFPClient client = getCFPClient();
        assertThat(client.canGetVotingResults()).isTrue();

        final VotingResults votingResults = client.getVotingResultsDaily(getConferenceDay())
                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
        LOG.info("votingResults: {}", votingResults);
    }

    @Test
    @Disabled
    @EnabledIf("serverReachable")
    void speakerAvatarsAreLoadable() {
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

        assertThat(avatarsLoadable.get(false)).isEmpty();
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
