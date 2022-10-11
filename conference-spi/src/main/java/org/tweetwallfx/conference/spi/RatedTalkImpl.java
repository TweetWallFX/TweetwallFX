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

import java.util.Objects;
import org.tweetwallfx.conference.api.RatedTalk;
import org.tweetwallfx.conference.api.Talk;

public final class RatedTalkImpl implements RatedTalk {

    private final Talk talk;
    private final double averageRating;
    private final int totalRating;

    private RatedTalkImpl(final Builder builder) {
        this.talk = Objects.requireNonNull(builder.talk, "talk must not be null");
        this.averageRating = Objects.requireNonNull(builder.averageRating, "averageRating must not be null");
        this.totalRating = Objects.requireNonNull(builder.totalRating, "totalRating must not be null");
    }

    @Override
    public Talk getTalk() {
        return talk;
    }

    @Override
    public double getAverageRating() {
        return averageRating;
    }

    @Override
    public int getTotalRating() {
        return totalRating;
    }

    @Override
    public String toString() {
        return new StringBuilder("RatedTalkImpl")
                .append("{averageRating=").append(getAverageRating())
                .append(", totalRating=").append(getTotalRating())
                .append(", talk=").append(getTalk())
                .append('}')
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("CanIgnoreReturnValueSuggester")
    public static final class Builder {

        private Talk talk;
        private Double averageRating;
        private Integer totalRating;

        private Builder() {
        }

        public Builder withTalk(final Talk talk) {
            this.talk = Objects.requireNonNull(talk, "talk must not be null");
            return this;
        }

        public Builder withAverageRating(final double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public Builder withTotalRating(final int totalRating) {
            this.totalRating = totalRating;
            return this;
        }

        public RatedTalk build() {
            return new RatedTalkImpl(this);
        }
    }
}
