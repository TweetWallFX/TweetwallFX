/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.dataprovider;

import static org.tweetwallfx.util.Nullable.nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.RatedTalk;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * DataProvider Implementation for Top Talks Today
 */
public final class TopTalksTodayDataProvider implements DataProvider, DataProvider.Scheduled {

    private List<VotedTalk> votedTalks = Collections.emptyList();
    private final Config config;
    private volatile boolean initialized = false;

    private TopTalksTodayDataProvider(final Config config) {
        this.config = config;
    }

    @Override
    public ScheduledConfig getScheduleConfig() {
        return config;
    }

    @Override
    public boolean requiresInitialization() {
        return true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void run() {
        // TODO: find a way for today's talks to be filtered from the overall list
        String actualDayName = LocalDateTime.now(ZoneId.systemDefault())
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        List<RatedTalk> votingResults = ConferenceClient.getClient()
                .getRatingClient()
                .map(rc -> rc.getRatedTalks(System.getProperty("org.tweetwallfx.scheduledata.day", actualDayName)))
                .orElse(Collections.emptyList());
        votedTalks = votingResults.stream()
                .sorted(Comparator.reverseOrder())
                .filter(rt -> rt.getTotalRating() >= config.minTotalVotes)
                .limit(config.nrVotes)
                .map(VotedTalk::new)
                .toList();
        initialized = true;
    }

    public List<VotedTalk> getFilteredSessionData() {
        return nullable(votedTalks);
    }

    /**
     * Implementation of {@link DataProvider.Factory} as Service implementation
     * creating {@link TopTalksTodayDataProvider}.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public TopTalksTodayDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new TopTalksTodayDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<TopTalksTodayDataProvider> getDataProviderClass() {
            return TopTalksTodayDataProvider.class;
        }
    }

    /**
     * POJO used to configure {@link TopTalksTodayDataProvider}.
     *
     * <p>
     * Param {@code nrVotes} The number of votes to produce at most. Defaults to
     * {@code 5}.
     *
     * <p>
     * Param {@code initialDelay} The type of scheduling to perform. Defaults to
     * {@link ScheduleType#FIXED_RATE}.
     *
     * <p>
     * Param {@code initialDelay} Delay until the first execution in seconds.
     * Defaults to {@code 0L}.
     *
     * <p>
     * Param {@code scheduleDuration} Fixed rate of / delay between consecutive
     * executions in seconds. Defaults to {@code 300L}.
     *
     * <p>
     * Param {@code minTotalVotes} Minimum number of total votes for a rated
     * talk to be displayed. Defaults to {@code 10}.
     */
    public static record Config(
            Integer nrVotes,
            ScheduleType scheduleType,
            Long initialDelay,
            Long scheduleDuration,
            Integer minTotalVotes) implements ScheduledConfig {

        public Config {
            nrVotes = Objects.requireNonNullElse(nrVotes, 5);
            if (nrVotes < 0) {
                throw new IllegalArgumentException("property 'nrVotes' must not be a negative number");
            }
            scheduleType = Objects.requireNonNullElse(scheduleType, ScheduleType.FIXED_RATE);
            initialDelay = Objects.requireNonNullElse(initialDelay, 0L);
            scheduleDuration = Objects.requireNonNullElse(scheduleDuration, 300L);
            minTotalVotes = Objects.requireNonNullElse(minTotalVotes, 10);
            if (minTotalVotes < 0) {
                throw new IllegalArgumentException("property 'minTotalVotes' must not be a negative number");
            }
        }
    }
}
