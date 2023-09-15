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

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.tweetwallfx.conference.api.SessionType;
import org.tweetwallfx.conference.spi.util.Optionals;

public final class SessionTypeImpl implements SessionType {

    private final String id;
    private final String name;
    private final Optional<String> description;
    private final Optional<String> color;
    private final Duration duration;
    private final boolean pause;

    private SessionTypeImpl(final Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.name = Objects.requireNonNull(builder.name, "name must not be null");
        this.description = Optionals.nonEmptyString(builder.description);
        this.color = Optionals.nonEmptyString(builder.color);
        this.duration = Objects.requireNonNull(builder.duration, "duration must not be null");
        this.pause = Objects.requireNonNull(builder.pause, "pause must not be null");
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
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public Optional<String> getColor() {
        return color;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean isPause() {
        return pause;
    }

    @Override
    public String toString() {
        return createToString(this, Map.of(
                "id", getId(),
                "title", getName(),
                "description", getDescription(),
                "duration", getDuration(),
                "pause", isPause(),
                "color", getColor()
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
        private String color;
        private Duration duration;
        private Boolean pause;

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
            this.description = description;
            return this;
        }

        public Builder withColor(final String color) {
            this.color = color;
            return this;
        }

        public Builder withDuration(final Duration duration) {
            this.duration = Objects.requireNonNull(duration, "duration must not be null");
            return this;
        }

        public Builder withPause(final Boolean pause) {
            this.pause = Objects.requireNonNull(pause, "pause must not be null");
            return this;
        }

        public SessionType build() {
            return new SessionTypeImpl(this);
        }
    }
}
