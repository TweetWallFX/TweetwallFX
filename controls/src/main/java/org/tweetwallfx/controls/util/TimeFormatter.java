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

import java.time.Instant;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * Utility for formatting time
 */
public class TimeFormatter {

    private TimeFormatter() {
    }

    /**
     * Formats the given {@code date} in natural relative time format (to the
     * current time) easy to understand by a human (e.g. "3 minutes ago" for
     * {@code formatNatural(Instant.now().minus(Duration.ofMinutes(3)), Locale.ENGLISH)}.
     *
     * @param instant the instant to format
     *
     * @param locale the local to use when formatting
     *
     * @return the formatted string
     */
    public static String formatNatural(Instant instant, Locale locale) {
        return new PrettyTime(locale).format(instant);
    }
}
