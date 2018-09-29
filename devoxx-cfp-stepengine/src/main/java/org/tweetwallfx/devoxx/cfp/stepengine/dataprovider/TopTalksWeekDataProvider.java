/*
 * The MIT License
 *
 * Copyright 2017-2018 TweetWallFX
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.VotingResultTalk;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * DataProvider Implementation for Top Talks Week
 *
 * @author Sven Reimers
 */
public final class TopTalksWeekDataProvider implements DataProvider {

    private List<VotedTalk> votedTalks = Collections.emptyList();
    private final Config config;

    private TopTalksWeekDataProvider(final Config config) {
        this.config = config;
        updateVotingResults();
    }

    public void updateVotingResults() {
        List<VotingResultTalk> votingResults = CFPClient.getClient()
                .getVotingResultsOverall()
                .map(org.tweetwall.devoxx.api.cfp.client.VotingResults::getResult)
                .map(org.tweetwall.devoxx.api.cfp.client.VotingResult::getTalks)
                .orElse(Collections.emptyList());
        votedTalks = votingResults.stream()
                .sorted(Comparator
                        .comparing(TopTalksWeekDataProvider::averageFormattedVote)
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

    public static class Factory implements DataProvider.Factory {

        @Override
        public TopTalksWeekDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new TopTalksWeekDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<TopTalksWeekDataProvider> getDataProviderClass() {
            return TopTalksWeekDataProvider.class;
        }
    }

    /**
     * POJO used to configure {@link TopTalksWeekDataProvider}.
     */
    public static class Config {

        /**
         * The number of votes to produce at most. Defaults to {@code 5}.
         */
        private int nrVotes = 5;

        public int getNrVotes() {
            return nrVotes;
        }

        public void setNrVotes(final int nrVotes) {
            if (nrVotes < 0) {
                throw new IllegalArgumentException("property 'nrVotes' must not be a negative number");
            }

            this.nrVotes = nrVotes;
        }
    }
}
