/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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

import org.tweetwallfx.conference.api.Room;
import java.util.Objects;

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
        return new StringBuilder("RoomImpl")
                .append("{id=").append(getId())
                .append(", name=").append(getName())
                .append(", capacity=").append(getCapacity())
                .append(", weight=").append(getWeight())
                .append('}')
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String id;
        private String name;
        private Integer capacity;
        private double weight = Double.MAX_VALUE;

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

        public Builder withCapacity(final Integer capacity) {
            this.capacity = Objects.requireNonNull(capacity, "capacity must not be null");
            return this;
        }

        public Builder withWeight(final Double weight) {
            if (null != weight) {
                this.weight = weight;
            }
            return this;
        }

        public Room build() {
            return new RoomImpl(this);
        }
    }
}
