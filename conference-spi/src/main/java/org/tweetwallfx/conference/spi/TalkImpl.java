/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 TweetWallFX
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
package org.tweetwallfx.conference.spi;

import static org.tweetwallfx.util.ToString.createToString;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;

import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.DateTimeRange;
import org.tweetwallfx.conference.api.ScheduleSlot;
import org.tweetwallfx.conference.api.SessionType;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.api.Talk;
import org.tweetwallfx.conference.api.Track;
import org.tweetwallfx.conference.spi.util.Optionals;

/**
 * Default implementation of {@link Talk}.
 */
public final class TalkImpl implements Talk {

    private final String id;
    private final String name;
    private final String audienceLevel;
    private final SessionType sessionType;
    private final OptionalInt favoriteCount;
    private final Locale language;
    private final List<ScheduleSlot> scheduleSlots;
    private final List<Speaker> speakers;
    private final List<String> tags;
    private final Track track;

    private TalkImpl(final Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.name = Objects.requireNonNull(builder.name, "name must not be null");
        this.audienceLevel = Objects.requireNonNull(builder.audienceLevel, "audienceLevel must not be null");
        this.sessionType = Objects.requireNonNull(builder.sessionType, "sessionType must not be null");
        this.favoriteCount = Optionals.integer(builder.favoriteCount);
        this.language = Objects.requireNonNull(builder.language, "language must not be null");
        this.scheduleSlots = List.copyOf(builder.scheduleSlots);
        this.speakers = List.copyOf(builder.speakers);
        this.tags = List.copyOf(builder.tags);
        this.track = Objects.requireNonNull(builder.track, "track must not be null");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAudienceLevel() {
        return audienceLevel;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public OptionalInt getFavoriteCount() {
        return favoriteCount;
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    public List<ScheduleSlot> getScheduleSlots() {
        return List.copyOf(scheduleSlots);
    }

    @Override
    public List<Speaker> getSpeakers() {
        return List.copyOf(speakers);
    }

    @Override
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    @Override
    public Track getTrack() {
        return track;
    }

    @Override
    public Optional<Talk> reload() {
        return ConferenceClient.getClient().getTalk(id);
    }

    @Override
    public String toString() {
        return createToString(this, Map.of(
                "id", getId(),
                "name", getName(),
                "audienceLevel", getAudienceLevel(),
                "sessionType", getSessionType(),
                "favoriteCount", getFavoriteCount(),
                "language", getLanguage(),
                "scheduleSlots", getScheduleSlots(),
                "speakers", getSpeakers(),
                "tags", getTags(),
                "track", getTrack()
            ),
            true);
    }

    /**
     * Creates and returns a new instance of {@link Builder}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating instances of {@link TalkImpl}.
     */
    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String id;
        private String name;
        private String audienceLevel;
        private SessionType sessionType;
        private Integer favoriteCount;
        private Locale language;
        private Set<ScheduleSlot> scheduleSlots = new TreeSet<>(Comparator
                .comparing(ScheduleSlot::getDateTimeRange, Comparator.comparing(DateTimeRange::getStart))
                .thenComparing(ScheduleSlot::getRoom));
        private Set<Speaker> speakers = new TreeSet<>(Comparator
                .comparing(Speaker::getFullName));
        private Set<String> tags = new TreeSet<>(Comparator
                .comparing(s -> s.toLowerCase(Locale.ENGLISH)));
        private Track track;

        private Builder() {
        }

        /**
         * Adds a schedule slot to the talk.
         *
         * @param scheduleSlot the schedule slot to add
         *
         * @return this builder instance
         */
        public Builder addScheduleSlot(final ScheduleSlot scheduleSlot) {
            scheduleSlots.add(Objects.requireNonNull(scheduleSlot, "scheduleSlot must not be null"));
            return this;
        }

        /**
         * Adds a speaker to the talk.
         *
         * @param speaker the speaker to add
         *
         * @return this builder instance
         */
        public Builder addSpeaker(final Speaker speaker) {
            speakers.add(Objects.requireNonNull(speaker, "speaker must not be null"));
            return this;
        }

        /**
         * Adds a tag to the talk.
         *
         * @param tag the tag to add
         *
         * @return this builder instance
         */
        public Builder addTag(final String tag) {
            tags.add(Objects.requireNonNull(tag, "tag must not be null").trim());
            return this;
        }

        /**
         * Sets the id of the talk.
         *
         * @param id the id to set
         *
         * @return this builder instance
         */
        public Builder withId(final String id) {
            this.id = Objects.requireNonNull(id, "id must not be null").trim();
            return this;
        }

        /**
         * Sets the name of the talk.
         *
         * @param name the name to set
         *
         * @return this builder instance
         */
        public Builder withName(final String name) {
            this.name = Objects.requireNonNull(name, "name must not be null").trim();
            return this;
        }

        /**
         * Sets the audience level of the talk.
         *
         * @param audienceLevel the audience level to set
         *
         * @return this builder instance
         */
        public Builder withAudienceLevel(final String audienceLevel) {
            this.audienceLevel = Objects.requireNonNull(audienceLevel, "audienceLevel must not be null").trim();
            return this;
        }

        /**
         * Sets the session type of the talk.
         *
         * @param sessionType the session type to set
         *
         * @return this builder instance
         */
        public Builder withSessionType(final SessionType sessionType) {
            this.sessionType = Objects.requireNonNull(sessionType, "sessionType must not be null");
            return this;
        }

        /**
         * Sets the favorite count for the talk.
         *
         * @param favoriteCount the favorite count to set
         *
         * @return this builder instance
         */
        public Builder withFavoriteCount(final Integer favoriteCount) {
            this.favoriteCount = favoriteCount;
            return this;
        }

        /**
         * Sets the language of the talk.
         *
         * @param language the language to set
         *
         * @return this builder instance
         */
        public Builder withLanguage(final Locale language) {
            this.language = Objects.requireNonNull(language, "language must not be null");
            return this;
        }

        /**
         * Sets the schedule slots for the talk.
         *
         * @param scheduleSlots the collection of schedule slots to set
         *
         * @return this builder instance
         */
        public Builder withScheduleSlots(final Collection<? extends ScheduleSlot> scheduleSlots) {
            this.scheduleSlots.clear();
            if (null != scheduleSlots) {
                scheduleSlots.forEach(this::addScheduleSlot);
            }
            return this;
        }

        /**
         * Sets the speakers for the talk.
         *
         * @param speakers the collection of speakers to set
         *
         * @return this builder instance
         */
        public Builder withSpeakers(final Collection<? extends Speaker> speakers) {
            this.speakers.clear();
            if (null != speakers) {
                speakers.forEach(this::addSpeaker);
            }
            return this;
        }

        /**
         * Sets the tags for the talk.
         *
         * @param tags the collection of tags to set
         *
         * @return this builder instance
         */
        public Builder withTags(final Collection<? extends String> tags) {
            this.tags.clear();
            if (null != tags) {
                tags.forEach(this::addTag);
            }
            return this;
        }

        /**
         * Sets the track of the talk.
         *
         * @param track the track to set
         *
         * @return this builder instance
         */
        public Builder withTrack(final Track track) {
            this.track = Objects.requireNonNull(track, "track must not be null");
            return this;
        }

        /**
         * Builds and returns a new instance of {@link TalkImpl}.
         *
         * @return a new {@link TalkImpl} instance
         */
        public Talk build() {
            return new TalkImpl(this);
        }
    }
}
