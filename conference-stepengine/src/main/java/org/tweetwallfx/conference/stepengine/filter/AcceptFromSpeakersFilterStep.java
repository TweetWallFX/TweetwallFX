/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.filter;

import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.api.filter.AcceptFromSenderFilterStep;

/**
 * A {@link FilterStep} handling {@link Tweet}s by checking the sending
 * {@link User} {@link User#getName()} and possibly accept them if the user
 * twitter handle is one of the conferences speakers handles.
 *
 * In case {@link User#getName()} is one of the names configured in
 * {@link Config#getUserHandles()} it is terminally accepted with
 * {@link Result#ACCEPTED}. Otherwise it is evaluated as
 * {@link Result#NOTHING_DEFINITE}.
 */
public final class AcceptFromSpeakersFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LoggerFactory.getLogger(AcceptFromSpeakersFilterStep.class);
    private static final AtomicReference<SpeakerTwitterHandles> SPEAKER_HANDLES = new AtomicReference<>(null);

    private final Config config;

    private AcceptFromSpeakersFilterStep(final Config config) {
        this.config = config;
    }

    @Override
    public FilterStep.Result check(final Tweet tweet) {
        Tweet t = tweet;

        do {
            LOG.debug("Tweet(id:{}): Checking for Tweet(id:{}) ...",
                    tweet.getId(),
                    t.getId());

            if (getSpeakerTwitterHandles().twitterHandles.contains(t.getUser().getScreenName().toLowerCase(Locale.ENGLISH))) {
                LOG.info("Tweet(id:{}): User handle for Tweet(id:{}) is whitelisted -> ACCEPTED",
                        tweet.getId(),
                        t.getId());
                return Result.ACCEPTED;
            }

            LOG.debug("Tweet(id:{}): User handle for Tweet(id:{}) is not whitelisted",
                    t.getId(),
                    tweet.getId());
            t = t.getRetweetedTweet();
        } while (config.isCheckRetweeted() && null != t);

        LOG.debug("Tweet(id:{}): No terminal decision found -> NOTHING_DEFINITE",
                tweet.getId());
        return Result.NOTHING_DEFINITE;
    }

    private SpeakerTwitterHandles getSpeakerTwitterHandles() {
        SpeakerTwitterHandles speakerTwitterHandles = SPEAKER_HANDLES.get();

        if (null == speakerTwitterHandles) {
            speakerTwitterHandles = new SpeakerTwitterHandles(ConferenceClient.getClient().getSpeakers().stream()
                    .map(Speaker::getSocialMedia)
                    .map(m -> m.get("TWITTER"))
                    .filter(Objects::nonNull)
                    .toList());
            SPEAKER_HANDLES.set(speakerTwitterHandles);
        }

        return speakerTwitterHandles;
    }

    /**
     * Implementation of {@link FilterStep.Factory} creating
     * {@link AcceptFromSenderFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<AcceptFromSpeakersFilterStep> getFilterStepClass() {
            return AcceptFromSpeakersFilterStep.class;
        }

        @Override
        public AcceptFromSpeakersFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new AcceptFromSpeakersFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link AcceptFromSenderFilterStep}.
     */
    public static final class Config {

        private boolean checkRetweeted = false;

        /**
         * Returns a boolean flag controlling whether for a retweet the
         * retweeted Tweet is also checked.
         *
         * @return boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked
         */
        public boolean isCheckRetweeted() {
            return checkRetweeted;
        }

        /**
         * Sets the boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked.
         *
         * @param checkRetweeted the new value
         */
        public void setCheckRetweeted(final boolean checkRetweeted) {
            this.checkRetweeted = checkRetweeted;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "checkRetweeted", isCheckRetweeted()
            ), super.toString());
        }
    }

    private static class SpeakerTwitterHandles {

        private final Set<String> twitterHandles;

        public SpeakerTwitterHandles(final Collection<String> twitterHandles) {
            this.twitterHandles = Set.copyOf(twitterHandles);
        }
    }
}
