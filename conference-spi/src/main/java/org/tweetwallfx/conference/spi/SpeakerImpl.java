/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 TweetWallFX
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.api.Talk;

public final class SpeakerImpl implements Speaker {

    private final String id;
    private final String firstName;
    private final String fullName;
    private final String lastName;
    private final Optional<String> company;
    private final String avatarURL;
    private final Map<String, String> socialMedia;
    private final List<Talk> talks;

    private SpeakerImpl(final Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.firstName = Objects.requireNonNull(builder.firstName, "firstName must not be null");
        this.fullName = Objects.requireNonNull(builder.fullName, "fullName must not be null");
        this.lastName = Objects.requireNonNull(builder.lastName, "lastName must not be null");
        this.company = Optional.ofNullable(builder.company);
        this.avatarURL = Objects.requireNonNull(builder.avatarURL, "avatarURL must not be null");
        this.socialMedia = Map.copyOf(builder.socialMedia);
        this.talks = List.copyOf(builder.talks);
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Map<String, String> getSocialMedia() {
        return Map.copyOf(socialMedia);
    }

    @Override
    public List<Talk> getTalks() {
        return List.copyOf(talks);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Optional<String> getCompany() {
        return company;
    }

    @Override
    public String getAvatarURL() {
        return avatarURL;
    }

    @Override
    public Optional<Speaker> reload() {
        return ConferenceClient.getClient().getSpeaker(id);
    }

    @Override
    public String toString() {
        return createToString(this, Map.of(
                "id", getId(),
                "firstName", getFirstName(),
                "fullName", getFullName(),
                "lastName", getLastName(),
                "company", getCompany(),
                "avatarURL", getAvatarURL(),
                "socialMedia", getSocialMedia(),
                "talks", getTalks()
            ),
            true);
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String firstName;
        private String fullName;
        private String lastName;
        private Map<String, String> socialMedia = new TreeMap<>();
        private List<Talk> talks = new ArrayList<>();
        private String id;
        private String company;
        private String avatarURL;

        private Builder() {
        }

        public Builder addTalk(final Talk talk) {
            talks.add(Objects.requireNonNull(talk, "talk must not be null"));
            return this;
        }

        public Builder addSocialMedia(final String socialMediaName, final String socialMediaValue) {
            Objects.requireNonNull(socialMediaName, "socialMediaName must not be null");
            Objects.requireNonNull(socialMediaValue, "socialMediaValue must not be null");
            socialMedia.put(socialMediaName.toUpperCase(Locale.ENGLISH), socialMediaValue);
            return this;
        }

        public Builder withId(final String id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
            return this;
        }

        public Builder withFirstName(final String firstName) {
            this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
            return this;
        }

        public Builder withFullName(final String fullName) {
            this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
            return this;
        }

        public Builder withCompany(final String company) {
            this.company = company;
            return this;
        }

        public Builder withAvatarURL(final String avatarURL) {
            this.avatarURL = Objects.requireNonNull(avatarURL, "avatarURL must not be null");
            return this;
        }

        public Builder withTalks(final Collection<? extends Talk> talks) {
            this.talks.clear();
            if (null != talks) {
                talks.forEach(this::addTalk);
            }
            return this;
        }

        public Speaker build() {
            return new SpeakerImpl(this);
        }
    }
}
