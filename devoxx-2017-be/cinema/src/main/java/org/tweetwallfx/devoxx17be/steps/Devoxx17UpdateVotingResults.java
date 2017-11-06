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
package org.tweetwallfx.devoxx17be.steps;

import java.time.Duration;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.devoxx2017be.dataprovider.TopTalksTodayDataProvider;

/**
 * Step to trigger the updating of the voting results
 *
 * @author Sven Reimers
 */
public class Devoxx17UpdateVotingResults extends AbstractStep {

    @Override
    public void doStep(StepEngine.MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final TopTalksTodayDataProvider topTalksToday = wordleSkin.getSkinnable().getDataProvider(TopTalksTodayDataProvider.class);
        topTalksToday.updateVotigResults();
        final TopTalksTodayDataProvider topTalksWeek = wordleSkin.getSkinnable().getDataProvider(TopTalksTodayDataProvider.class);
        topTalksWeek.updateVotigResults();
        context.proceed();
    }

    @Override
    public Duration preferredStepDuration(StepEngine.MachineContext context) {
        return Duration.ZERO;
    }

//    @Override
//    public boolean requiresPlatformThread() {
//        return false;
//    }
}
