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

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class CompositeTweeterTest {
    @Mock(name = "org.tweetwallfx.tweet.api.CompositeTweeter")
    Logger logger;
    @Mock(name = "tweeterOne")
    Tweeter tweeterOne;
    @Mock(name = "tweeterTwo")
    Tweeter tweeterTwo;
    @Mock(name = "filterQery")
    TweetFilterQuery filterQery;
    @Mock(name = "tweetStreamOne")
    TweetStream tweetStreamOne;
    @Mock(name = "tweetStreamTwo")
    TweetStream tweetStreamTwo;
    @Mock(name = "tweetOne")
    Tweet tweetOne;
    @Mock(name = "tweetTwo")
    Tweet tweetTwo;
    @Mock(name = "tweetQuery")
    TweetQuery tweetQuery;
    @Mock(name = "user")
    User user;
    @Mock(name = "userOne")
    User userOne;
    @Mock(name = "userTwo")
    User userTwo;

    CompositeTweeter compositeTweeter;

    @BeforeEach
    void prepare() {
        compositeTweeter = new CompositeTweeter(List.of(tweeterOne, tweeterTwo));
    }

    @AfterEach
    void verifyMocks() {
        verifyNoInteractions(tweetOne, tweetTwo, userOne, userTwo);
        verifyNoMoreInteractions(logger, tweeterOne, tweeterTwo, filterQery, tweetStreamOne, tweetStreamTwo, user);
    }

    @Test
    void isEnabled() {
        assertThat(compositeTweeter.isEnabled()).isTrue();
    }

    @Test
    void createTweetStream() {
        RuntimeException problem = new RuntimeException("some problem");
        when(tweeterOne.createTweetStream(filterQery)).thenReturn(tweetStreamOne);
        when(tweeterTwo.createTweetStream(filterQery)).thenThrow(problem);

        assertThat(compositeTweeter.createTweetStream(filterQery))
                .isInstanceOf(CompositeTweetStream.class)
                .extracting(CompositeTweetStream.class::cast)
                .satisfies(compositeStream -> {
                    verify(tweetStreamOne).onTweet(compositeStream);
                });
        verify(logger).error("Failed create tweet with query {} on tweeter {}", filterQery, tweeterTwo, problem);
    }

    @Test
    void getTweet() {
        when(tweeterOne.getTweet(42L)).thenReturn(null);
        when(tweeterTwo.getTweet(42L)).thenReturn(null, tweetOne);

        assertThat(compositeTweeter.getTweet(42L)).isNull();
        assertThat(compositeTweeter.getTweet(42L)).isEqualTo(tweetOne);
    }

    @Test
    void getUser() {
        when(tweeterOne.getUser("johnDoe")).thenReturn(user);

        assertThat(compositeTweeter.getUser("johnDoe")).isEqualTo(user);
    }

    @Test
    void getFriendsForUser() {
        when(tweeterOne.getFriends(user)).thenReturn(Stream.of(userOne));
        when(tweeterTwo.getFriends(user)).thenReturn(Stream.of(userTwo));

        assertThat(compositeTweeter.getFriends(user)).containsExactly(userOne, userTwo);
    }

    @Test
    void getFriendsForScreenName() {
        when(tweeterOne.getFriends("johnDoe")).thenReturn(Stream.of());
        when(tweeterTwo.getFriends("johnDoe")).thenReturn(Stream.of(userTwo));

        assertThat(compositeTweeter.getFriends("johnDoe")).containsExactly(userTwo);
    }

    @Test
    void getFriendsForUserId() {
        when(tweeterOne.getFriends(42L)).thenReturn(Stream.of(userOne));
        when(tweeterTwo.getFriends(42L)).thenReturn(Stream.of());

        assertThat(compositeTweeter.getFriends(42L)).containsExactly(userOne);
    }

    @Test
    void getFollowersForUser() {
        when(tweeterOne.getFollowers(user)).thenReturn(Stream.of(userTwo));
        when(tweeterTwo.getFollowers(user)).thenReturn(Stream.of(userOne));

        assertThat(compositeTweeter.getFollowers(user)).containsExactly(userTwo, userOne);
    }

    @Test
    void getFollowersForScreenName() {
        when(tweeterOne.getFollowers("johnDoe")).thenReturn(Stream.of(userOne));
        when(tweeterTwo.getFollowers("johnDoe")).thenReturn(Stream.of());

        assertThat(compositeTweeter.getFollowers("johnDoe")).containsExactly(userOne);
    }

    @Test
    void getFollowersForUserId() {
        when(tweeterOne.getFollowers(42L)).thenReturn(Stream.of());
        when(tweeterTwo.getFollowers(42L)).thenReturn(Stream.of(userTwo));

        assertThat(compositeTweeter.getFollowers(42L)).containsExactly(userTwo);
    }

    @Test
    void search() {
        when(tweeterOne.search(tweetQuery)).thenReturn(Stream.of(tweetOne));
        when(tweeterTwo.search(tweetQuery)).thenReturn(Stream.of(tweetTwo));

        assertThat(compositeTweeter.search(tweetQuery)).containsExactly(tweetOne, tweetTwo);
    }

    @Test
    void searchPaged() {
        when(tweeterOne.searchPaged(tweetQuery, 2)).thenReturn(Stream.of(tweetTwo));
        when(tweeterTwo.searchPaged(tweetQuery, 2)).thenReturn(Stream.of(tweetOne));

        assertThat(compositeTweeter.searchPaged(tweetQuery, 2)).containsExactly(tweetTwo, tweetOne);
    }

    @Test
    void shutdown() {
        RuntimeException problem = new RuntimeException("some problem");
        doNothing().when(tweeterOne).shutdown();
        doThrow(problem).when(tweeterTwo).shutdown();

        assertThatNoException().isThrownBy(compositeTweeter::shutdown);
        verify(logger).error("Failed to shutdown tweeter {}", tweeterTwo, problem);
    }
}
