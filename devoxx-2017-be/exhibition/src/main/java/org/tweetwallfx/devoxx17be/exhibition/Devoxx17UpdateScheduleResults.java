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
package org.tweetwallfx.devoxx17be.exhibition;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;

/**
 * Step to trigger the updating of the schedule
 *
 * @author Sven Reimers
 */
public class Devoxx17UpdateScheduleResults implements Step {

    private LocalDateTime nextUpDateTime = LocalDateTime.now().minusMinutes(5);

    private Devoxx17UpdateScheduleResults() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        final ScheduleDataProvider scheduleProvider = context.getDataProvider(ScheduleDataProvider.class);
        scheduleProvider.updateSchedule();
        nextUpDateTime = LocalDateTime.now().plusMinutes(15);
        context.proceed();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return LocalDateTime.now().isBefore(nextUpDateTime);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link Devoxx17UpdateScheduleResults}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public Devoxx17UpdateScheduleResults create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new Devoxx17UpdateScheduleResults();
        }

        @Override
        public Class<Devoxx17UpdateScheduleResults> getStepClass() {
            return Devoxx17UpdateScheduleResults.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(ScheduleDataProvider.class);
        }
    }
}
