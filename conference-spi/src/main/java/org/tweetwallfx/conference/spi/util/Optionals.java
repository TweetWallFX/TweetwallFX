/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2026 TweetWallFX
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
package org.tweetwallfx.conference.spi.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

/**
 * Utility methods for working with optional values.
 */
public class Optionals {

    private Optionals() {
        // prevent instantiation
    }

    /**
     * Converts an {@link Integer} to an {@link OptionalInt}.
     *
     * @param input the integer value to convert
     *
     * @return an {@link OptionalInt} containing the value if non-null, otherwise an empty {@link OptionalInt}
     */
    public static OptionalInt integer(final Integer input) {
        return null == input
                ? OptionalInt.empty()
                : OptionalInt.of(input);
    }

    /**
     * Converts a {@link String} to an {@link Optional} that contains a non-empty trimmed string.
     *
     * @param input the string value to convert
     *
     * @return an {@link Optional} containing the trimmed non-empty string if present and non-empty, otherwise an empty {@link Optional}
     */
    public static Optional<String> nonEmptyString(final String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
    }
}
