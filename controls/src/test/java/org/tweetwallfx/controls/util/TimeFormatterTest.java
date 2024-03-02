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
package org.tweetwallfx.controls.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TimeFormatterTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                arguments(
                        Duration.ZERO,
                        Locale.ENGLISH,
                        "moments ago"
                ),
                arguments(
                        Duration.ZERO,
                        Locale.GERMAN,
                        "gerade eben"
                ),
                arguments(
                        Duration.ofSeconds(10),
                        Locale.ENGLISH,
                        "moments ago"
                ),
                arguments(
                        Duration.ofSeconds(30),
                        Locale.ENGLISH,
                        "moments ago"
                ),
                arguments(
                        Duration.ofSeconds(55),
                        Locale.ENGLISH,
                        "moments ago"
                ),
                arguments(
                        Duration.ofSeconds(187),
                        Locale.ENGLISH,
                        "3 minutes ago"
                ),
                arguments(
                        Duration.ofSeconds(307),
                        Locale.ENGLISH,
                        "5 minutes ago"
                ),
                arguments(
                        Duration.ofSeconds(607),
                        Locale.ENGLISH,
                        "10 minutes ago"
                )
        );
    }

    @ParameterizedTest(name = "{1}: {0} -> {2}")
    @MethodSource("parameters")
    public void testFormatNaturalInstant(Duration duration, Locale locale, String expectedFormattedString) {
        assertThat(TimeFormatter.formatNatural(Instant.now().minus(duration), locale))
                .isEqualTo(expectedFormattedString);
    }
}
