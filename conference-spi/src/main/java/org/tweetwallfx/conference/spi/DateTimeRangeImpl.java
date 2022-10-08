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
import java.time.Instant;
import java.util.Objects;

/**
 * POJO of a date/time range.
 */
public final class DateTimeRangeImpl implements DateTimeRange {

    private final Instant end;
    private final Instant start;

    private DateTimeRangeImpl(final Builder builder) {
        this.end = Objects.requireNonNull(builder.end, "end must not be null");
        this.start = Objects.requireNonNull(builder.start, "start must not be null");
    }

    @Override
    public Instant getEnd() {
        return end;
    }

    @Override
    public Instant getStart() {
        return start;
    }

    @Override
    public String toString() {
        return new StringBuilder("DateTimeRangeImpl")
                .append("{end=").append(getEnd())
                .append(", start=").append(getStart())
                .append('}')
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private Instant end;
        private Instant start;

        private Builder() {
        }

        public Builder withEnd(final Instant end) {
            this.end = Objects.requireNonNull(end, "end must not be null");
            return this;
        }

        public Builder withStart(final Instant start) {
            this.start = Objects.requireNonNull(start, "start must not be null");
            return this;
        }

        public DateTimeRange build() {
            return new DateTimeRangeImpl(this);
        }
    }
}
