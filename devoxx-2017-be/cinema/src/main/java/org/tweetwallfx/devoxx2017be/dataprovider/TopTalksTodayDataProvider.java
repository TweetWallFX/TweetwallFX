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

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.VotingResultTalk;
import org.tweetwallfx.controls.dataprovider.DataProvider;

/**
 * DataProvider Implementation for Top Talks Today
 *
 * @author Sven Reimers
 */
public final class TopTalksTodayDataProvider implements DataProvider {

    private List<VotedTalk> votedTalks = Collections.emptyList();

    private TopTalksTodayDataProvider() {
        updateVotigResults();
    }

    public void updateVotigResults() {
        String actualDayName = LocalDateTime.now().getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        List<VotingResultTalk> votingResults = CFPClient.getClient()
                .getVotingResultsDaily(System.getProperty("org.tweetwalfx.devoxxbe17.day", actualDayName))
                .map(org.tweetwall.devoxx.api.cfp.client.VotingResults::getResult)
                .map(org.tweetwall.devoxx.api.cfp.client.VotingResult::getTalks)
                .orElse(Collections.emptyList());
        votedTalks = votingResults.stream()
                .sorted(Comparator
                        .comparing(TopTalksTodayDataProvider::averageFormattedVote)
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
        return "TRTT-Devoxx2017BE";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public TopTalksTodayDataProvider create() {
            return new TopTalksTodayDataProvider();
        }

        @Override
        public Class<TopTalksTodayDataProvider> getDataProviderClass() {
            return TopTalksTodayDataProvider.class;
        }
    }
}
