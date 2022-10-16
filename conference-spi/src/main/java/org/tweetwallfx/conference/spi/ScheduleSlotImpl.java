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

import org.tweetwallfx.conference.api.DateTimeRange;
import org.tweetwallfx.conference.api.Room;
import org.tweetwallfx.conference.api.ScheduleSlot;
import org.tweetwallfx.conference.api.Talk;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public final class ScheduleSlotImpl implements ScheduleSlot {

    private final String id;
    private final boolean overflow;
    private final DateTimeRange dateTimeRange;
    private final OptionalInt favoriteCount;
    private final Room room;
    private final Optional<Talk> talk;

    private ScheduleSlotImpl(final Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.overflow = Objects.requireNonNull(builder.overflow, "overflow must not be null");
        this.dateTimeRange = Objects.requireNonNull(builder.dateTimeRange, "dateTimeRange must not be null");
        this.favoriteCount = null == builder.favoriteCount ? OptionalInt.empty() : OptionalInt.of(builder.favoriteCount);
        this.room = Objects.requireNonNull(builder.room, "room must not be null");
        this.talk = Optional.ofNullable(builder.talk);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isOverflow() {
        return overflow;
    }

    @Override
    public DateTimeRange getDateTimeRange() {
        return dateTimeRange;
    }

    @Override
    public OptionalInt getFavoriteCount() {
        return favoriteCount;
    }

    @Override
    public Room getRoom() {
        return room;
    }

    @Override
    public Optional<Talk> getTalk() {
        return talk;
    }

    @Override
    public String toString() {
        return new StringBuilder("ScheduleSlotImpl")
                .append("{id=").append(getId())
                .append(", overflow=").append(isOverflow())
                .append(", dateTimeRange=").append(getDateTimeRange())
                .append(", favoriteCount=").append(getFavoriteCount())
                .append(", room=").append(getRoom())
                .append(", talk=").append(getTalk())
                .append('}')
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private String id;
        private Boolean overflow;
        private DateTimeRange dateTimeRange;
        private Integer favoriteCount;
        private Room room;
        private Talk talk;

        private Builder() {
        }

        public Builder withId(final String id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
            return this;
        }

        public Builder withOverflow(final boolean overflow) {
            this.overflow = overflow;
            return this;
        }

        public Builder withDateTimeRange(final DateTimeRange dateTimeRange) {
            this.dateTimeRange = dateTimeRange;
            return this;
        }

        public Builder withFavoriteCount(final Integer favoriteCount) {
            this.favoriteCount = favoriteCount;
            return this;
        }

        public Builder withRoom(final Room room) {
            this.room = Objects.requireNonNull(room, "room must not be null");
            return this;
        }

        public Builder withTalk(final Talk talk) {
            this.talk = talk;
            return this;
        }

        public ScheduleSlot build() {
            return new ScheduleSlotImpl(this);
        }
    }
}
