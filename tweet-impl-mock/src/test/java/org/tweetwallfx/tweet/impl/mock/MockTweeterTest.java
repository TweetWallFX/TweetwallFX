/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.mock.config.MockSettings;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class MockTweeterTest {
    @Mock(name = "org.tweetwallfx.tweet.impl.mock.MockTweeter")
    Logger logger;
    @Mock(name = "executor")
    ScheduledExecutorService executor;
    @Mock(name = "user")
    User user;
    @Mock(name = "tweetQuery")
    TweetQuery tweetQuery;
    @Mock(name = "filterQuery")
    TweetFilterQuery filterQuery;
    MockSettings settings;
    MockTweeter tweeter;

    @BeforeEach
    void prepare() {
        settings = new MockSettings(false, true, 3, null, null, null);
        tweeter = new MockTweeter(settings, executor);

        verify(logger).debug("Initializing with configuration: {}", settings);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger, executor, user, tweetQuery, filterQuery);
    }

    @Test
    void isEnabled() {
        assertThat(tweeter.isEnabled()).isTrue();
        MockSettings disabledSettings = new MockSettings(false, false, null, 1, null, null);
        MockTweeter disabledTweeter = new MockTweeter(disabledSettings, executor);

        assertThat(disabledTweeter.isEnabled()).isFalse();
        verify(logger).debug("Initializing with configuration: {}", disabledSettings);
        verify(logger).debug(eq("Simulate post {}"), isA(MockPost.class));
    }

    @Test
    void limitUserId() {
        assertThat(MockTweeter.limitUserId(0)).isZero();
        assertThat(MockTweeter.limitUserId(199)).isEqualTo(199);
        assertThat(MockTweeter.limitUserId(200)).isZero();
        assertThat(MockTweeter.limitUserId(399)).isEqualTo(199);
        assertThat(MockTweeter.limitUserId(400)).isZero();
        assertThat(MockTweeter.limitUserId(599)).isEqualTo(199);
    }

    @Test
    void createFemaleUser() {
        for (int userId = 0; userId < 100; userId++) {
            var user = MockTweeter.createUser(userId);
            assertThat(user.getBiggerProfileImageUrl())
                    .startsWith("https://randomuser.me/api/portraits/women/")
                    .endsWith(userId + ".jpg");
            assertThat(user.getProfileImageUrl())
                    .startsWith("https://randomuser.me/api/portraits/med/women/")
                    .endsWith(userId + ".jpg");
        }
    }
    @Test
    void createMaleUser() {
        for (int userId = 100, picId = 0; userId < 200; userId++, picId++) {
            var user = MockTweeter.createUser(userId);
            assertThat(user.getBiggerProfileImageUrl())
                    .startsWith("https://randomuser.me/api/portraits/men/")
                    .endsWith(picId + ".jpg");
            assertThat(user.getProfileImageUrl())
                    .startsWith("https://randomuser.me/api/portraits/med/men/")
                    .endsWith(picId + ".jpg");
        }
    }

    @Test
    void getTweet() {
        doNothing().when(logger).debug("getTweet({})", 123L);

        assertThat(tweeter.getTweet(123)).isNotNull().satisfies(tweet -> {
            assertThat(tweet.getId()).isEqualTo(123L);
            assertThat(tweet.getText()).isNotEmpty();
        });
    }

    @Test
    void getUser() {
        doNothing().when(logger).debug("getUser({})", "42");

        assertThat(tweeter.getUser("42")).isNotNull().satisfies(user -> {
            assertThat(user.getId()).isEqualTo(42L);
            assertThat(user.getName()).isNotEmpty();
        });
    }

    @Test
    void getFriendsForUser() {
        doNothing().when(logger).debug("getFriends({})", user);

        assertThat(tweeter.getFriends(user)).isEmpty();
    }

    @Test
    void getFriendsForUserScreenName() {
        doNothing().when(logger).debug("getFriends({})", "userScreenName");

        assertThat(tweeter.getFriends("userScreenName")).isEmpty();
    }

    @Test
    void getFriendsForUserId() {
        doNothing().when(logger).debug("getFriends({})", 4711L);

        assertThat(tweeter.getFriends(4711L)).isEmpty();
    }

    @Test
    void getFollowersForUser() {
        doNothing().when(logger).debug("getFollowers({})", user);

        assertThat(tweeter.getFollowers(user)).isEmpty();
    }

    @Test
    void getFollowersForUserScreenName() {
        doNothing().when(logger).debug("getFollowers({})", "userScreenName");

        assertThat(tweeter.getFollowers("userScreenName")).isEmpty();
    }

    @Test
    void getFollowersForUserId() {
        doNothing().when(logger).debug("getFollowers({})", 4711L);

        assertThat(tweeter.getFollowers(4711L)).isEmpty();
    }

    @Test
    void search() {
        doNothing().when(logger).debug("search({})", tweetQuery);
        when(tweetQuery.getQuery()).thenReturn("#javaIsFun or @TweetWallFx OR  @reinhapa");

        assertThat(tweeter.search(tweetQuery)).isEmpty();
    }

    @Test
    void searchPaged() {
        doNothing().when(logger).debug("searchPaged({}, {})", tweetQuery, 22);
        when(tweetQuery.getQuery()).thenReturn("#javaIsFun @reinhapa");

        assertThat(tweeter.searchPaged(tweetQuery, 22)).isEmpty();
        verify(logger).debug("Initializing with configuration: {}", settings);
    }

    @Test
    void shutdown() throws InterruptedException {
        doNothing().when(logger).debug("shutdown()");
        when(executor.isTerminated()).thenReturn(false);
        doNothing().when(executor).shutdown();
        when(executor.awaitTermination(2, TimeUnit.SECONDS)).thenReturn(false);
        when(executor.shutdownNow()).thenReturn(List.of());

        assertThatNoException().isThrownBy(tweeter::shutdown);
    }

    @Test
    void createTweetStream() {
        doNothing().when(logger).debug("createTweetStream({})", filterQuery);
        doNothing().when(logger).error("Track names not supported: {}", List.of("OR"));
        when(filterQuery.getTrack()).thenReturn(new String[]{"#vdz23", "OR", "@Devoxx", "@reinhapa", "#itWillBeFun"});

        assertThat(tweeter.createTweetStream(filterQuery)).isNotNull();

        verify(logger).debug("Starting post task with {} second interval", 3L);
        verify(executor).scheduleWithFixedDelay(notNull(), eq(3L), eq(3L), eq(TimeUnit.SECONDS));
    }
}
