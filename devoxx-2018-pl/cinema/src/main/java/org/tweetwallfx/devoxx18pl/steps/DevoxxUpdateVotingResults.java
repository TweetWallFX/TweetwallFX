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
package org.tweetwallfx.devoxx18pl.steps;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.devoxx18pl.dataprovider.TopTalksTodayDataProvider;
import org.tweetwallfx.devoxx18pl.dataprovider.TopTalksWeekDataProvider;

/**
 * Step to trigger the updating of the voting results
 *
 * @author Sven Reimers
 */
public class DevoxxUpdateVotingResults implements Step {

    private LocalDateTime nextUpDateTime = LocalDateTime.now().minusMinutes(5);

    private DevoxxUpdateVotingResults() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        final TopTalksTodayDataProvider topTalksToday = context.getDataProvider(TopTalksTodayDataProvider.class);
        topTalksToday.updateVotigResults();
        final TopTalksWeekDataProvider topTalksWeek = context.getDataProvider(TopTalksWeekDataProvider.class);
        topTalksWeek.updateVotingResults();
        nextUpDateTime = LocalDateTime.now().plusMinutes(5);
        context.proceed();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return LocalDateTime.now().isBefore(nextUpDateTime);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link DevoxxUpdateVotingResults}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public DevoxxUpdateVotingResults create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new DevoxxUpdateVotingResults();
        }

        @Override
        public Class<DevoxxUpdateVotingResults> getStepClass() {
            return DevoxxUpdateVotingResults.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(TopTalksTodayDataProvider.class, TopTalksWeekDataProvider.class);
        }
    }
}
