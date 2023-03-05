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
package org.tweetwallfx.tweet.impl.mastodon4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mastodon4j.core.MastodonClient;
import org.mastodon4j.core.MastodonException;
import org.mastodon4j.core.api.Accounts;
import org.mastodon4j.core.api.BaseMastodonApi;
import org.mastodon4j.core.api.EventStream;
import org.mastodon4j.core.api.MastodonApi;
import org.mastodon4j.core.api.Statuses;
import org.mastodon4j.core.api.Streaming;
import org.mastodon4j.core.api.entities.AccessToken;
import org.mastodon4j.core.api.entities.Event;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.mastodon4j.config.MastodonSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.ACCOUNTS;
import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.HASHTAGS;
import static org.mastodon4j.core.api.entities.Subscription.hashtag;
import static org.mastodon4j.core.api.entities.Subscription.stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createAccount;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createSearch;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createStatus;

@MockitoSettings
class MastodonTweeterTest {
    public static final String ACCESS_TOKEN_VALUE = "accessTokenValue";
    @Mock(name = "org.tweetwallfx.tweet.impl.mastodon4j.MastodonTweeter")
    Logger logger;
    @Mock(name = "client")
    MastodonApi client;
    @Mock(name = "statuses")
    Statuses statuses;
    @Mock(name = "accounts")
    Accounts accounts;
    @Mock(name = "user")
    User user;
    @Mock(name = "streaming")
    Streaming streaming;
    @Mock(name = "eventStream")
    EventStream eventStream;
    @Mock(name = "tweetQuery")
    TweetQuery tweetQuery;
    @Mock(name = "filterQuery")
    TweetFilterQuery filterQuery;
    MastodonSettings settings;
    MastodonTweeter tweeter;

    @BeforeEach
    void prepare() {
        MastodonSettings.OAuth oauth = new MastodonSettings.OAuth(ACCESS_TOKEN_VALUE);
        settings = new MastodonSettings(false, true, "https://mastodon.social", oauth);
        tweeter = new MastodonTweeter(settings, s -> client);

        verify(logger).debug("Initializing with configuration: {}", settings);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger, client, statuses, accounts, user, streaming, eventStream, tweetQuery, filterQuery);
    }

    @Test
    void createClient() {
        assertThat(MastodonTweeter.createClient(settings)).isInstanceOf(MastodonClient.class);
    }

    @Test
    void isEnabled() {
        assertThat(tweeter.isEnabled()).isTrue();
        MastodonSettings.OAuth oauth = new MastodonSettings.OAuth(ACCESS_TOKEN_VALUE);
        MastodonSettings disabledSettings = new MastodonSettings(false, false, null, oauth);
        MastodonTweeter disabledTweeter = new MastodonTweeter(disabledSettings, s -> client);

        assertThat(disabledTweeter.isEnabled()).isFalse();
        verify(logger).debug("Initializing with configuration: {}", disabledSettings);
    }

    @Test
    void getTweet() {
        doNothing().when(logger).debug("getTweet({})", 123L);
        when(client.statuses()).thenReturn(statuses);
        when(statuses.get("123")).thenReturn(createStatus("123", "some message"));

        assertThat(tweeter.getTweet(123)).isNotNull().satisfies(tweet -> {
            assertThat(tweet.getId()).isEqualTo(123L);
            assertThat(tweet.getText()).isEqualTo("some message");
        });
    }

    @Test
    void getUser() {
        doNothing().when(logger).debug("getUser({})", "42");
        when(client.accounts()).thenReturn(accounts);
        when(accounts.get("42")).thenReturn(createAccount("42", "johnDoe"));

        assertThat(tweeter.getUser("42")).isNotNull().satisfies(user -> {
            assertThat(user.getId()).isEqualTo(42L);
            assertThat(user.getName()).isEqualTo("johnDoe");
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
        when(client.search(BaseMastodonApi.QueryOptions.of("#javaIsFun").type(HASHTAGS))).thenReturn(createSearch());
        when(client.search(BaseMastodonApi.QueryOptions.of("@TweetWallFx").type(ACCOUNTS))).thenReturn(createSearch());
        when(client.search(BaseMastodonApi.QueryOptions.of("@reinhapa").type(ACCOUNTS))).thenReturn(createSearch());

        assertThat(tweeter.search(tweetQuery)).isEmpty();
    }

    @Test
    void searchPaged() {
        doNothing().when(logger).debug("searchPaged({}, {})", tweetQuery, 22);
        when(tweetQuery.getQuery()).thenReturn("#javaIsFun @reinhapa");
        when(client.search(BaseMastodonApi.QueryOptions.of("#javaIsFun").type(HASHTAGS).limit(22))).thenReturn(createSearch());
        when(client.search(BaseMastodonApi.QueryOptions.of("@reinhapa").type(ACCOUNTS).limit(22))).thenReturn(createSearch());

        assertThat(tweeter.searchPaged(tweetQuery, 22)).isEmpty();
        verify(logger).debug("Initializing with configuration: {}", settings);
    }

    @Test
    void shutdown() {
        doNothing().when(logger).debug("shutdown()");

        tweeter.shutdown();
    }

    @Test
    void shutdownOpenStream() throws MastodonException {
        when(client.streaming()).thenReturn(streaming);
        when(streaming.stream()).thenReturn(eventStream);
        doNothing().when(logger).debug("shutdown()");
        doNothing().when(eventStream).close();
        doNothing().when(logger).info("Closed stream {}", eventStream);

        assertThat(tweeter.createRegisteredStream()).isSameAs(eventStream);
        assertThatNoException().isThrownBy(tweeter::shutdown);
    }

    @Test
    void shutdownOpenStreamFails() throws MastodonException {
        MastodonException problem = new MastodonException(null);
        when(client.streaming()).thenReturn(streaming);
        when(streaming.stream()).thenReturn(eventStream);
        doNothing().when(logger).debug("shutdown()");
        doThrow(problem).when(eventStream).close();
        doNothing().when(logger).error("Error wile closing stream {}", eventStream, problem);

        assertThat(tweeter.createRegisteredStream()).isSameAs(eventStream);
        assertThatNoException().isThrownBy(tweeter::shutdown);
    }

    @Test
    void createTweetStream() {
        AccessToken accessToken = AccessToken.create(ACCESS_TOKEN_VALUE);
        List<Consumer<Event>> consumersOne = new ArrayList<>();
        List<Consumer<Event>> consumersTwo = new ArrayList<>();
        EventStream eventStreamTwo = mock("eventStreamTwo");
        doNothing().when(logger).debug("createTweetStream({})", filterQuery);
        doNothing().when(logger).error("Track names not supported: {}", List.of("OR"));
        when(filterQuery.getTrack()).thenReturn(new String[]{"#vdz23", "OR", "@Devoxx", "@reinhapa", "#itWillBeFun"});
        when(client.streaming()).thenReturn(streaming);
        when(streaming.stream()).thenReturn(eventStream, eventStreamTwo);
        doAnswer(ctx -> consumersOne.add(ctx.getArgument(0))).when(eventStream).registerConsumer(any());
        doNothing().when(eventStream).changeSubscription(hashtag(true, accessToken, "vdz23"));
        doNothing().when(eventStream).changeSubscription(hashtag(true, accessToken, "itWillBeFun"));
        doAnswer(ctx -> consumersTwo.add(ctx.getArgument(0))).when(eventStreamTwo).registerConsumer(any());
        doNothing().when(eventStreamTwo).changeSubscription(stream(true, accessToken, "public"));

        assertThat(tweeter.createTweetStream(filterQuery)).isNotNull();
    }
}
