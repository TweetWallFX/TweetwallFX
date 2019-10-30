/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 TweetWallFX
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
package org.tweetwallfx.devoxx.cfp.stepengine.dataprovider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.tweetwallfx.devoxx.api.cfp.client.CFPClient;
import org.tweetwallfx.devoxx.api.cfp.client.VotingResultTalk;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * DataProvider Implementation for Top Talks Today
 *
 * @author Sven Reimers
 */
public final class TopTalksTodayDataProvider implements DataProvider, DataProvider.Scheduled {

    private List<VotedTalk> votedTalks = Collections.emptyList();
    private final Config config;

    private TopTalksTodayDataProvider(final Config config) {
        this.config = config;
    }

    @Override
    public ScheduledConfig getScheduleConfig() {
        return config;
    }

    @Override
    public void run() {
        String actualDayName = LocalDateTime.now(ZoneId.systemDefault())
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        List<VotingResultTalk> votingResults = CFPClient.getClient()
                .getVotingResultsDaily(System.getProperty("org.tweetwallfx.scheduledata.day", actualDayName))
                .map(org.tweetwallfx.devoxx.api.cfp.client.VotingResults::getResult)
                .map(org.tweetwallfx.devoxx.api.cfp.client.VotingResult::getTalks)
                .orElse(Collections.emptyList());
        votedTalks = votingResults.stream()
                .sorted(Comparator
                        .comparing(TopTalksTodayDataProvider::averageFormattedVote)
                        .thenComparing(VotingResultTalk::getRatingTotalVotes)
                        .reversed())
                .limit(config.getNrVotes())
                .map(VotedTalk::new)
                .collect(Collectors.toList());
    }

    private static String averageFormattedVote(final VotingResultTalk ratedTalk) {
        return String.format("%.1f", ratedTalk.getRatingAverageScore());
    }

    public List<VotedTalk> getFilteredSessionData() {
        return votedTalks;
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
     */
    public static class Config implements ScheduledConfig {

        /**
         * The number of votes to produce at most. Defaults to {@code 5}.
         */
        private int nrVotes = 5;
        /**
         * The type of scheduling to perform. Defaults to
         * {@link ScheduleType#FIXED_RATE}.
         */
        private ScheduleType scheduleType = ScheduleType.FIXED_RATE;
        /**
         * Delay until the first execution in seconds. Defaults to {@code 0L}.
         */
        private long initialDelay = 0L;
        /**
         * Fixed rate of / delay between consecutive executions in seconds.
         * Defaults to {@code 300L}.
         */
        private long scheduleDuration = 300L;

        @Override
        public ScheduleType getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(final ScheduleType scheduleType) {
            this.scheduleType = scheduleType;
        }

        @Override
        public long getInitialDelay() {
            return initialDelay;
        }

        public void setInitialDelay(final long initialDelay) {
            this.initialDelay = initialDelay;
        }

        @Override
        public long getScheduleDuration() {
            return scheduleDuration;
        }

        public void setScheduleDuration(final long scheduleDuration) {
            this.scheduleDuration = scheduleDuration;
        }

        public int getNrVotes() {
            return nrVotes;
        }

        public void setNrVotes(final int nrVotes) {
            if (nrVotes < 0) {
                throw new IllegalArgumentException("property 'nrVotes' must not be a negative number");
            }

            this.nrVotes = nrVotes;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "scheduleType", getScheduleType(),
                    "initialDelay", getInitialDelay(),
                    "scheduleDuration", getScheduleDuration(),
                    "nrVotes", getNrVotes()
            )) + " extends " + super.toString();
        }
    }
}
