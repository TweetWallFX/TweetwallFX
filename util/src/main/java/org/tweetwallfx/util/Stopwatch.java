/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 TweetWallFX
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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility method allowing for tracking the time an action takes to execute.
 */
public class Stopwatch {

    private Stopwatch() {
        // prevent instantiation
    }

    /**
     * Measures the time the execution of the given {@code supplier} takes and
     * passes the measured time along to {@code durationConsumer}.
     *
     * @param <T> The type of the value produced by {@link Supplier}
     *
     * @param supplier the {@link Supplier}
     *
     * @param durationConsumer the {@link Consumer} accepting the measured time
     *
     * @return the value produced by {@code supplier}
     */
    public static <T> T measure(final Supplier<T> supplier, final Consumer<Duration> durationConsumer) {
        Objects.requireNonNull(supplier, "supplier must not be null");
        Objects.requireNonNull(durationConsumer, "durationConsumer must not be null");
        final long startNanos = System.nanoTime();

        try {
            return supplier.get();
        } finally {
            final long endNanos = System.nanoTime();
            durationConsumer.accept(Duration.ofNanos(endNanos - startNanos));
        }
    }

    /**
     * Measures the time the execution of the given {@code runnable} takes and
     * passes the measured time along to {@code durationConsumer}.
     *
     * @param runnable the {@link Runnable}
     *
     * @param durationConsumer the {@link Consumer} accepting the measured time
     */
    public static void measure(final Runnable runnable, final Consumer<Duration> durationConsumer) {
        Objects.requireNonNull(runnable, "runnable must not be null");
        Objects.requireNonNull(durationConsumer, "durationConsumer must not be null");
        measure(
                () -> {
                    runnable.run();
                    return null;
                },
                durationConsumer);
    }
}
