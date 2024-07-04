/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 TweetWallFX
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
package org.tweetwallfx.emoji;

import java.util.ArrayList;
import java.util.List;

public final class Emojify {

    private Emojify() {
        // prevent instantiation
    }

    public static List<Object> tokenizeStringToTextAndEmoji(final String message) {
        final List<Object> tokens = new ArrayList<>();
        final int[] codePoints = message.codePoints().toArray();
        int idxStart = 0;

        for (int idx = 0; idx < codePoints.length; idx++) {
            var cp = codePoints[idx];

            if (Character.isEmoji(cp)) {
                // we have an emoji
                if (idx != idxStart) {
                    // prior to the emoji is a non-empty text
                    // add non-emoji text to tokens
                    tokens.add(new String(codePoints, idxStart, idx - idxStart));
                }

                // add a Twemoji for the emoji to tokens
                tokens.add(new Twemoji(Integer.toHexString(cp)));
                // move start of text to next codePoint index
                idxStart = idx + 1;
            }
        }

        if (idxStart != codePoints.length) {
            // in case regular text finished up the message (i.e. emoji was not the last character of message)
            tokens.add(new String(codePoints, idxStart, codePoints.length - idxStart));
        }

        return tokens;
    }
}
