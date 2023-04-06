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

import org.mastodon4j.core.MastodonClient;
import org.mastodon4j.core.MastodonException;
import org.mastodon4j.core.api.BaseMastodonApi;
import org.mastodon4j.core.api.EventStream;
import org.mastodon4j.core.api.MastodonApi;
import org.mastodon4j.core.api.entities.Status;
import org.mastodon4j.core.api.entities.AccessToken;
import org.mastodon4j.core.api.entities.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.mastodon4j.config.MastodonSettings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.ACCOUNTS;
import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.HASHTAGS;
import static org.tweetwallfx.tweet.impl.mastodon4j.config.MastodonSettings.CONFIG_KEY;

public class MastodonTweeter implements Tweeter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MastodonTweeter.class);
    private static final Pattern ACCEPTED_KEYWORDS = Pattern.compile("([@#]).+");
    private static final Pattern KEYWORD_DELEMITER = Pattern.compile(" +");

    private final MastodonSettings settings;
    private final MastodonApi client;
    private final List<EventStream> openStreams;
    private final AccessToken accessToken;

    public MastodonTweeter() {
        this(Configuration.getInstance().getConfigTyped(CONFIG_KEY, MastodonSettings.class), MastodonTweeter::createClient);
    }

    MastodonTweeter(MastodonSettings settings, Function<MastodonSettings, MastodonApi> clientCreator) {
        LOGGER.debug("Initializing with configuration: {}", settings);
        this.settings = settings;
        this.accessToken = AccessToken.create(settings.oauth().accessToken());
        this.client = clientCreator.apply(settings);
        this.openStreams = new ArrayList<>();
    }

    static MastodonApi createClient(MastodonSettings settings) {
        return MastodonClient.create(settings.restUrl(), AccessToken.create(settings.oauth().accessToken()));
    }

    @Override
    public boolean isEnabled() {
        return settings.enabled();
    }

    @Override
    public TweetStream createTweetStream(TweetFilterQuery filterQuery) {
        LOGGER.debug("createTweetStream({})", filterQuery);
        final StatusStream statusStream = new StatusStream();
        final Map<TrackType, List<String>> trackTypeListMap = Stream.of(filterQuery.getTrack())
                .collect(Collectors.groupingBy(this::trackType, () -> new EnumMap<>(TrackType.class), Collectors.toList()));
        trackTypeListMap.forEach((trackType, values) -> {
            switch (trackType) {
                case HASHTAG -> handleHashtags(statusStream, values);
                case USER -> handleUsers(statusStream, values);
                case UNKNOWN -> LOGGER.error("Track names not supported: {}", values);
            }
        });
        return statusStream;
    }

    enum TrackType {
        UNKNOWN, HASHTAG, USER;
    }

    private TrackType trackType(String trackName) {
        if (trackName.startsWith("#")) {
            return TrackType.HASHTAG;
        } else if (trackName.startsWith("@")) {
            return TrackType.USER;
        } else {
            return TrackType.UNKNOWN;
        }
    }

    EventStream createRegisteredStream() {
        final EventStream stream = client.streaming().stream();
        openStreams.add(stream);
        return stream;
    }

    private void handleHashtags(StatusStream statusStream, List<String> hashtags) {
        final EventStream stream = createRegisteredStream();
        stream.registerConsumer(new EventStatusConsumer(statusStream));
        hashtags.stream()
                .map(hashtag -> Subscription.hashtag(true, accessToken, hashtag.substring(1)))
                .forEach(stream::changeSubscription);
    }
    private void handleUsers(StatusStream statusStream, List<String> users) {
        final EventStream stream = createRegisteredStream();
        final Predicate<Status> predicate = new UserMentionPredicate(users).or(new AccountPredicate(users));
        stream.registerConsumer(new EventStatusConsumer(statusStream, predicate));
        stream.changeSubscription(Subscription.stream(true, accessToken, "public"));
    }

    @Override
    public Tweet getTweet(long tweetId) {
        LOGGER.debug("getTweet({})", tweetId);
        try {
            return Optional.ofNullable(client.statuses().get(Long.toString(tweetId)))
                    .map(MastodonStatus::new)
                    .orElse(null);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected failure on backend", e);
            return null;
        }
    }

    @Override
    public User getUser(String userId) {
        LOGGER.debug("getUser({})", userId);
        try {
            return Optional.ofNullable(client.accounts().get(userId))
                    .map(MastodonAccount::new)
                    .orElse(null);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected failure on backend", e);
            return null;
        }
    }

    @Override
    public Stream<User> getFriends(User user) {
        LOGGER.debug("getFriends({})", user);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(String userScreenName) {
        LOGGER.debug("getFriends({})", userScreenName);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFriends(long userId) {
        LOGGER.debug("getFriends({})", userId);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(User user) {
        LOGGER.debug("getFollowers({})", user);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(String userScreenName) {
        LOGGER.debug("getFollowers({})", userScreenName);
        return Stream.empty();
    }

    @Override
    public Stream<User> getFollowers(long userId) {
        LOGGER.debug("getFollowers({})", userId);
        return Stream.empty();
    }

    @Override
    public Stream<Tweet> search(TweetQuery tweetQuery) {
        LOGGER.debug("search({})", tweetQuery);
        return KEYWORD_DELEMITER.splitAsStream(tweetQuery.getQuery())
                .flatMap(keyword -> queryStatuses(keyword, null))
                .map(MastodonStatus::new);
    }

    @Override
    public Stream<Tweet> searchPaged(TweetQuery tweetQuery, int numberOfPages) {
        LOGGER.debug("searchPaged({}, {})", tweetQuery, numberOfPages);
        return KEYWORD_DELEMITER.splitAsStream(tweetQuery.getQuery())
                .flatMap(keyword -> queryStatuses(keyword, numberOfPages))
                .map(MastodonStatus::new);
    }

    private Stream<Status> queryStatuses(String keyword, Integer numberOfPages) {
        final Matcher matcher = ACCEPTED_KEYWORDS.matcher(keyword);
        if (matcher.matches()) {
            final BaseMastodonApi.QueryOptions queryOptions = BaseMastodonApi.QueryOptions.of(keyword);
            return switch (matcher.group(1)) {
                case "#" -> queryHashtag(numberOfPages == null ? queryOptions : queryOptions.limit(numberOfPages));
                case "@" -> queryAccount(numberOfPages == null ? queryOptions : queryOptions.limit(numberOfPages));
                default -> Stream.empty();
            };
        }
        return Stream.empty();
    }

    private Stream<Status> queryAccount(BaseMastodonApi.QueryOptions queryOptions) {
        try {
            return client.search(queryOptions.type(ACCOUNTS)).accounts().stream()
                    .flatMap(account -> client.accounts().statuses(account.id()).stream());
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected failure on backend", e);
            return Stream.empty();
        }
    }

    private Stream<Status> queryHashtag(BaseMastodonApi.QueryOptions queryOptions) {
        try {
            return client.search(queryOptions.type(HASHTAGS)).hashtags().stream()
                    .flatMap(hashtag -> client.timelines().tag(hashtag.name()).stream());
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected failure on backend", e);
            return Stream.empty();
        }
    }

    @Override
    public void shutdown() {
        LOGGER.debug("shutdown()");
        openStreams.removeIf(MastodonTweeter::closeStream);
    }

    private static boolean closeStream(EventStream stream) {
        try {
            stream.close();
            LOGGER.info("Closed stream {}", stream);
        } catch (MastodonException e) {
            LOGGER.error("Error wile closing stream {}", stream, e);
        }
        return true;
    }
}
