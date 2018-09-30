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
package org.tweetwallfx.devoxx.cfp.stepengine.steps;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.tweetwallfx.devoxx.cfp.stepengine.dataprovider.ScheduleDataProvider;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * Step to trigger the updating of the schedule
 */
public class UpdateScheduleResults implements Step {

    private final Config config;
    private LocalDateTime nextUpDateTime;

    private UpdateScheduleResults(final Config config) {
        this.config = config;
        nextUpDateTime = LocalDateTime.now().minusMinutes(config.getInitialOffset());
    }

    @Override
    public void doStep(final MachineContext context) {
        final ScheduleDataProvider scheduleProvider = context.getDataProvider(ScheduleDataProvider.class);
        scheduleProvider.updateSchedule();
        nextUpDateTime = LocalDateTime.now().plusMinutes(config.getUpdateInteval());
        context.proceed();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return LocalDateTime.now().isBefore(nextUpDateTime);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link UpdateScheduleResults}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public UpdateScheduleResults create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new UpdateScheduleResults(stepDefinition.getConfig(Config.class));
        }

        @Override
        public Class<UpdateScheduleResults> getStepClass() {
            return UpdateScheduleResults.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(ScheduleDataProvider.class);
        }
    }

    /**
     * POJO used to configure {@link UpdateScheduleResults}.
     */
    public static class Config {

        /**
         * Initial offset of minutes.
         */
        private int initialOffset = 5;

        /**
         * Interval of minutes between actual calls to update the schedule data.
         */
        private int updateInteval = 5;

        public int getInitialOffset() {
            return initialOffset;
        }

        public void setInitialOffset(int initialOffset) {
            this.initialOffset = initialOffset;
        }

        public int getUpdateInteval() {
            return updateInteval;
        }

        public void setUpdateInteval(final int updateInteval) {
            this.updateInteval = Math.abs(updateInteval);
        }
    }
}
