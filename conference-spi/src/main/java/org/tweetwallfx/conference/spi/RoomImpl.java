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

import java.util.Map;
import java.util.Objects;

import org.tweetwallfx.conference.api.Room;

/**
 * Default implementation of {@link Room}.
 */
public final class RoomImpl implements Room {

    private final String id;
    private final String name;
    private final int capacity;
    private final double weight;

    private RoomImpl(final Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.name = Objects.requireNonNull(builder.name, "name must not be null");
        this.capacity = Objects.requireNonNull(builder.capacity, "capacity must not be null");
        this.weight = builder.weight;
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
    public int getCapacity() {
        return capacity;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return createToString(this, Map.of(
                "id", getId(),
                "name", getName(),
                "capacity", getCapacity(),
                "weight", getWeight()
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
     * Builder for creating instances of {@link RoomImpl}.
     */
    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String id;
        private String name;
        private Integer capacity;
        private double weight = Double.MAX_VALUE;

        private Builder() {
        }

        /**
         * Sets the id of the room.
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
         * Sets the name of the room.
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
         * Sets the capacity of the room.
         *
         * @param capacity the capacity to set
         *
         * @return this builder instance
         */
        public Builder withCapacity(final Integer capacity) {
            this.capacity = Objects.requireNonNull(capacity, "capacity must not be null");
            return this;
        }

        /**
         * Sets the weight of the room.
         *
         * @param weight the weight to set
         *
         * @return this builder instance
         */
        public Builder withWeight(final Double weight) {
            if (null != weight) {
                this.weight = weight;
            }
            return this;
        }

        /**
         * Builds and returns a new instance of {@link RoomImpl}.
         *
         * @return a new {@link RoomImpl} instance
         */
        public Room build() {
            return new RoomImpl(this);
        }
    }
}
