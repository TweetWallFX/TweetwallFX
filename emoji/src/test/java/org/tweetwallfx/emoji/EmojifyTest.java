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
package org.tweetwallfx.emoji;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class EmojifyTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                arguments(
                        "An 😀awesome 😃string with a few 😉emojis!",
                        new Object[]{
                                "An ",
                                new Twemoji("1f600"),
                                "awesome ",
                                new Twemoji("1f603"),
                                "string with a few ",
                                new Twemoji("1f609"),
                                "emojis!"
                        }
                ),
                arguments(
                        "😀 Awesome emojis! 😉",
                        new Object[]{
                                new Twemoji("1f600"),
                                " Awesome emojis! ",
                                new Twemoji("1f609")
                        }
                )
        );
    }

    @ParameterizedTest(name = "{0} > {1}")
    @MethodSource("parameters")
    void tokenizeStringToTextAndEmoji(String inputString, Object[] results) {
        assertThat(Emojify.tokenizeStringToTextAndEmoji(inputString))
                .containsExactly(results);
    }
}
