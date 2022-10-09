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
package org.tweetwallfx.conference.test;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.Room;
import org.tweetwallfx.conference.api.ScheduleSlot;
import org.tweetwallfx.conference.api.SessionType;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.api.Talk;

public abstract class ConferenceClientTestBase {

    private static final Logger LOG = LogManager.getLogger(ConferenceClientTestBase.class);
    private static final AtomicBoolean CONFERENCE_REACHABLE = new AtomicBoolean(false);

    private final Class<? extends ConferenceClient> expectedConferenceClient;
    private final String conferenceDay;
    private final String conferenceRoom;
    private final String talkId;

    protected ConferenceClientTestBase(
            final Class<? extends ConferenceClient> expectedConferenceClient,
            final String conferenceDay,
            final String conferenceRoom,
            final String talkId) {
        this.expectedConferenceClient = expectedConferenceClient;
        this.conferenceDay = Objects.requireNonNull(conferenceDay, "conferenceDay must not be null");
        this.conferenceRoom = Objects.requireNonNull(conferenceRoom, "conferenceRoom must not be null");
        this.talkId = Objects.requireNonNull(talkId, "talkId must not be null");
    }

    protected static final void checkServerReachable(final String uri) {
        final boolean testingLive = Boolean.getBoolean("org.tweetwallfx.tests.executeConferenceClientLiveTests");
        LOG.info("Test of Conference Client against live system is {}.", testingLive ? "enabled" : "disabled");

        if (!testingLive) {
            return;
        }

        try {
            LOG.info("Checking if Conference Server is reachable at {}", uri);
            final Response response = ClientBuilder.newClient()
                    .target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            LOG.info("Received {}", response);
            CONFERENCE_REACHABLE.set(Response.Status.Family.SUCCESSFUL == response
                    .getStatusInfo()
                    .getFamily());
        } catch (final ProcessingException pe) {
            LogManager.getLogger(ConferenceClientTestBase.class).error(pe, pe);
        }
    }

    private String getConferenceDay() {
        return conferenceDay;
    }

    private String getConferenceRoom() {
        return conferenceRoom;
    }

    protected final static boolean serverReachable() {
        if (!CONFERENCE_REACHABLE.get()) {
            LOG.info("CFP Server is unreachable");
            return false;
        }
        return true;
    }

    private ConferenceClient getConferenceClient() {
        final ConferenceClient client = ConferenceClient.getClient();
        LOG.info("client: " + client);
        assertThat(client).isNotNull();

        return client;
    }

    private Talk getConfiguredTalk() {
        final Talk talk = getConferenceClient().getTalk(talkId)
                .orElseThrow(() -> new IllegalStateException("Talk unretrievable"));
        LOG.info("talk: {}", talk);
        return talk;
    }

    @Test
    void clientImplIsFound() {
        LOG.warn("##### Start: {}", "clientImplIsFound");
        final ConferenceClient client = getConferenceClient();
        assertThat(client.getClass()).isEqualTo(expectedConferenceClient);
    }

    @Test
    @EnabledIf("serverReachable")
    void sessionTypesAreRetrievable() {
        LOG.warn("##### Start: {}", "sessionTypesAreRetrievable");
        final List<SessionType> sessionTypes = getConferenceClient().getSessionTypes();
        sessionTypes.forEach(o -> LOG.info("sessionType: {}", o));
        assertThat(sessionTypes).isNotEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void roomsAreRetrievable() {
        LOG.warn("##### Start: {}", "roomsAreRetrievable");
        final List<Room> rooms = getConferenceClient().getRooms();
        rooms.forEach(o -> LOG.info("room: {}", o));
        assertThat(rooms).isNotEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void scheduleIsRetrievableForADay() {
        LOG.warn("##### Start: {}", "scheduleIsRetrievableForADay");
        final List<ScheduleSlot> scheduleSlots = getConferenceClient().getSchedule(getConferenceDay());
        scheduleSlots.forEach(o -> LOG.info("scheduleSlot: {}", o));
        assertThat(scheduleSlots).isNotEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void scheduleIsRetrievableForADayAndRoom() {
        LOG.warn("##### Start: {}", "scheduleIsRetrievableForADayAndRoom");
        final List<ScheduleSlot> scheduleSlots = getConferenceClient().getSchedule(getConferenceDay(), getConferenceRoom());
        scheduleSlots.forEach(o -> LOG.info("scheduleSlot: {}", o));
        assertThat(scheduleSlots).isNotEmpty();
    }

    @Test
    @EnabledIf("serverReachable")
    void speakersAreRetrievable() {
        LOG.warn("##### Start: {}", "speakersAreRetrievable");
        final List<Speaker> speakers = getConferenceClient().getSpeakers();
        speakers.forEach(o -> LOG.info("speaker: {}", o));
        assertThat(speakers).isNotEmpty();
    }

//    @Test
//    @EnabledIf("serverReachable")
//    void speakerInformationIsCompletable() {
//        LOG.warn("##### Start: {}", "speakerInformationIsCompletable");
//        final ConferenceClient client = getConferenceClient();
//
//        final List<Speaker> speakers = client.getSpeakers();
//        assertThat(speakers).isNotNull();
//
//        final Speaker speaker = speakers
//                .stream()
//                .findAny()
//                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
//        LOG.info("speaker: {}", speaker);
//        assertThat(speaker.hasCompleteInformation()).isFalse();
//
//        final Speaker speakerReload = speaker
//                .reload()
//                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
//        LOG.info("speakerReload: {}", speakerReload);
//        assertThat(speakerReload.hasCompleteInformation()).isTrue();
//    }

    @Test
    @EnabledIf("serverReachable")
    void speakerIsRetrievable() {
        LOG.warn("##### Start: {}", "speakerIsRetrievable");
        final ConferenceClient client = getConferenceClient();

        final Speaker speaker = client.getSpeakers().stream().findFirst()
                .map(Speaker::getId)
                .flatMap(client::getSpeaker)
                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
        LOG.info("speaker: {}", speaker);
        assertThat(speaker.reload())
                .isNotEmpty()
                .doesNotHaveSameHashCodeAs(speaker);
    }

//    @Test
//    @EnabledIf("serverReachable")
//    void talkInformationIsCompletable() {
//        LOG.warn("##### Start: {}", "talkInformationIsCompletable");
//        final ConferenceClient client = getConferenceClient();
//
//        final Speaker speaker = client.getSpeakers().stream()
//                .findAny()
//                .orElseThrow(() -> new IllegalStateException("No Speaker found"));
//        LOG.info("speaker: {}", speaker);
//
//        assertThat(speaker.hasCompleteInformation()).isFalse();
//        assertThat(speaker.getAcceptedTalks()).isNull();
//        assertThat(speaker.getAcceptedTalks()).isEmpty();
//
//        final Speaker speakerReload = speaker
//                .reload()
//                .orElseThrow(() -> new IllegalStateException("Speaker reload failed"));
//        LOG.info("speakerReload: {}", speakerReload);
//        assertThat(speakerReload.hasCompleteInformation()).isTrue();
//        assertThat(speakerReload.getAcceptedTalks()).isNotNull();
//        assertThat(speakerReload.getAcceptedTalks()).isEmpty();
//
//        final Talk incompleteTalk = speakerReload.getAcceptedTalks().get(0);
//        assertThat(incompleteTalk).isNotNull();
//        assertThat(incompleteTalk.hasCompleteInformation()).isFalse();
//        LOG.info("incompleteTalk: {}", incompleteTalk);
//
//        final Talk completeTalk = incompleteTalk.reload()
//                .orElseThrow(() -> new IllegalStateException("Talk reload failed"));
//        LOG.info("completeTalk: {}", completeTalk);
//        assertThat(completeTalk.hasCompleteInformation()).isTrue();
//    }
//
//    @Test
//    @EnabledIf("serverReachable")
//    void talkCanGetSpeakers() {
//        LOG.warn("##### Start: {}", "talkCanGetSpeakers");
//
//        final Talk talk = getConfiguredTalk();
//        assertThat(talk.hasCompleteInformation()).isTrue();
//
//        final Set<Speaker> speakers = talk
//                .getSpeakers()
//                .stream()
//                .map(SpeakerReference::getSpeaker)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toSet());
//        assertThat(talk.getSpeakers()).isEmpty();
//        assertThat(talk.getSpeakers().size()).isEqualTo(speakers.size());
//
//        final Speaker speaker = speakers.stream()
//                .findAny()
//                .orElseThrow(() -> new IllegalStateException("Speaker unretrievable"));
//        LOG.info("speaker: {}", speaker);
//        assertThat(speaker.hasCompleteInformation()).isTrue();
//    }

    @Test
    @EnabledIf("serverReachable")
    void talkIsRetrievable() {
        LOG.warn("##### Start: {}", "talkIsRetrievable");
        final Talk talk = getConfiguredTalk();
        assertThat(talk.reload())
                .isNotEmpty()
                .doesNotHaveSameHashCodeAs(talk);
    }

    @Test
    @EnabledIf("serverReachable")
    void talksAreRetrievable() {
        LOG.warn("##### Start: {}", "talksAreRetrievable");
        final List<Talk> talks = getConferenceClient().getTalks();
        talks.forEach(o -> LOG.info("talk: {}", o));
        assertThat(talks).isNotEmpty();
    }

//    @Test
//    @EnabledIf("serverReachable")
//    void votingResultsOverallAreRetrievable() {
//        LOG.warn("##### Start: {}", "votingResultsOverallAreRetrievable");
//        final ConferenceClient client = getConferenceClient();
//        assertThat(client.canGetVotingResults()).isTrue();
//
//        final VotingResults votingResults = client.getVotingResultsOverall()
//                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
//        LOG.info("votingResults: {}", votingResults);
//    }
//
//    @Test
//    @EnabledIf("serverReachable")
//    void votingResultsDailyAreRetrievable() {
//        LOG.warn("##### Start: {}", "votingResultsDailyAreRetrievable");
//        final ConferenceClient client = getConferenceClient();
//        assertThat(client.canGetVotingResults()).isTrue();
//
//        final VotingResults votingResults = client.getVotingResultsDaily(getConferenceDay())
//                .orElseThrow(() -> new IllegalStateException("VotingResults unretrievable"));
//        LOG.info("votingResults: {}", votingResults);
//    }

    @Test
    @Disabled
    @EnabledIf("serverReachable")
    void speakerAvatarsAreLoadable() {
        LOG.warn("##### Start: {}", "speakerAvatarsAreLoadable");
        final ConferenceClient client = getConferenceClient();

        final Map<Boolean, List<Speaker>> avatarsLoadable = client.getSpeakers().stream().collect(Collectors.partitioningBy(speaker -> {
            System.out.println("Checking avator for " + speaker.getFullName() + " at '" + speaker.getAvatarURL() + "'");
            try (final InputStream is = new URL(speaker.getAvatarURL()).openStream()) {
                System.out.println("Succeeded");
                return null != is;
            } catch (final IOException ioe) {
                System.out.println("Failed loading for: " + speaker);
                ioe.printStackTrace(System.out);
                return false;
            }
        }));

        assertThat(avatarsLoadable.get(false)).isEmpty();
    }
}
