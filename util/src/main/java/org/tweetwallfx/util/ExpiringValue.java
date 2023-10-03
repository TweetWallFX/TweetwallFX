/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 TweetWallFX
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
package org.tweetwallfx.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A value holder of a value that may expire and thus require resetting of its
 * value.
 *
 * Value expiration is evaluated on access of the value and when valid is
 * returned immediately. Otherwise the value is loaded via the
 * {@code valueSupplier} and its validity for the next {@code validFor}
 * duration.
 */
public final class ExpiringValue<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ExpiringValue.class);
    private final Supplier<T> valueSupplier;
    private final Duration validFor;
    private transient Instant expires = Instant.MIN;
    private transient T currentValue = null;

    public ExpiringValue(final Supplier<T> valueSupplier, final Duration validFor) {
        this.valueSupplier = Objects.requireNonNull(valueSupplier, "valueSupplier must not be null");
        this.validFor = Objects.requireNonNull(validFor, "validFor must not be null");

        if (validFor.isZero() || validFor.isNegative()) {
            throw new IllegalArgumentException("validFor must not be zero or negative");
        }
    }

    /**
     * {@return the contained valid value}. If the value is no longer valid it is
     * reacquired and its validity reset prior to being returned.
     */
    public synchronized T getValue() {
        final Duration remainingValidity = Duration.between(Instant.now(), expires);

        if (remainingValidity.isNegative()) {
            LOG.info("{}: Value expired {} ago and will be reacquired", this, remainingValidity);
            // invalid and reload value
            currentValue = valueSupplier.get();
            expires = Instant.now().plus(validFor);
        } else {
            LOG.info("{}: Reusing value as it is valid for another {}", this, remainingValidity);
        }

        return currentValue;
    }
}
