/*
 * The MIT License
 *
 * Copyright 2017 TweetWallFX
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
package org.tweetwallfx.devoxx2017be.dataprovider;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.VotingResultTalk;
import org.tweetwallfx.controls.dataprovider.DataProvider;

/**
 * DataProvider Implementation for Top Talks Week
 *
 * @author Sven Reimers
 */
public final class TopTalksWeekDataProvider implements DataProvider {

    List<VotedTalk> votedTalks = Collections.emptyList();

    private TopTalksWeekDataProvider() {
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
                .limit(5)
                .map(VotedTalk::new)
                .collect(Collectors.toList());
    }

    private static String averageFormattedVote(final VotingResultTalk ratedTalk) {
        return String.format("%.1f", ratedTalk.getRatingAverageScore());
    }

    public List<VotedTalk> getFilteredSessionData() {
        return votedTalks;
    }

    @Override
    public String getName() {
        return "TRTW-Devoxx2017BE";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public TopTalksWeekDataProvider create() {
            return new TopTalksWeekDataProvider();
        }

        @Override
        public Class<TopTalksWeekDataProvider> getDataProviderClass() {
            return TopTalksWeekDataProvider.class;
        }
    }
}
