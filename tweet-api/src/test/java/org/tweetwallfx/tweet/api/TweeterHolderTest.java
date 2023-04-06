/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 TweetWallFX
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
package org.tweetwallfx.tweet.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
public class TweeterHolderTest {
    @Mock(name = "org.tweetwallfx.tweet.api.TweeterHolder")
    Logger logger;
    @Mock(name = "tweeterOne")
    Tweeter tweeterOne;
    @Mock(name = "tweeterTwo")
    Tweeter tweeterTwo;

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger, tweeterOne, tweeterTwo);
    }

    @Test
    void createInstanceNoImplementations() {
        final List<Tweeter> tweeters = List.of();

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> TweeterHolder.createInstance(tweeters))
                .withMessage("No implementation of Tweeter found!");
    }

    @Test
    void createInstanceSingleImplementations() {
        final List<Tweeter> tweeters = List.of(tweeterOne, tweeterTwo);
        when(tweeterOne.isEnabled()).thenReturn(true);
        doNothing().when(logger).info("Found enabled tweeter {}", tweeterOne);
        when(tweeterTwo.isEnabled()).thenReturn(false);
        doNothing().when(logger).info("Skipped disabled tweeter {}", tweeterTwo);

        assertThat(TweeterHolder.createInstance(tweeters)).isEqualTo(tweeterOne);
    }

    @Test
    void createInstanceTwoImplementations() {
        final List<Tweeter> tweeters = List.of(tweeterOne, tweeterTwo);
        when(tweeterOne.isEnabled()).thenReturn(true);
        doNothing().when(logger).info("Found enabled tweeter {}", tweeterOne);
        when(tweeterTwo.isEnabled()).thenReturn(true);
        doNothing().when(logger).info("Found enabled tweeter {}", tweeterTwo);

        assertThat(TweeterHolder.createInstance(tweeters)).isInstanceOf(CompositeTweeter.class);
    }
}
