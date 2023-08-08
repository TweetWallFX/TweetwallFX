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

import java.util.Map;
import java.util.Objects;

import org.tweetwallfx.conference.api.Track;

public final class TrackImpl implements Track {

    private final String avatarURL;
    private final String id;
    private final String description;
    private final String name;

    private TrackImpl(final Builder builder) {
        this.avatarURL = Objects.requireNonNull(builder.avatarURL, "avatarURL must not be null");
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.description = Objects.requireNonNull(builder.description, "description must not be null");
        this.name = Objects.requireNonNull(builder.name, "name must not be null");
    }

    @Override
    public String getAvatarURL() {
        return avatarURL;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return createToString(this, Map.of(
                "id", getId(),
                "name", getName(),
                "description", getDescription(),
                "avatarURL", getAvatarURL()
            ),
            true);
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String id;
        private String name;
        private String description;
        private String avatarURL;

        private Builder() {
        }

        public Builder withId(final String id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
            return this;
        }

        public Builder withName(final String name) {
            this.name = Objects.requireNonNull(name, "name must not be null");
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = Objects.requireNonNull(description, "description must not be null");
            return this;
        }

        public Builder withAvatarURL(final String avatarURL) {
            this.avatarURL = Objects.requireNonNull(avatarURL, "avatarURL must not be null");
            return this;
        }

        public Track build() {
            return new TrackImpl(this);
        }
    }
}
