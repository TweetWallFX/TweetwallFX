/*
 * The MIT License
 *
 * Copyright 2017-2018 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tweetwallfx.vdz.dataprovider;

import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwall.devoxx.api.cfp.client.ScheduleSlot;
import org.tweetwallfx.controls.dataprovider.DataProvider;

import javafx.application.Platform;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;

/**
 * DataProvider Implementation for Schedule Data
 *
 * @author Patrick Reinhart
 */
public class ScheduleDataProvider implements DataProvider {

    private List<ScheduleSlot> scheduleSlots = Collections.emptyList();

    private ScheduleDataProvider() {
        updateSchedule();
    }

    public void updateSchedule() {
        Platform.runLater(() -> {
            String actualDayName = LocalDateTime.now().getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
            CFPClient.getClient()
                    .getSchedule(System.getProperty("org.tweetwallfx.vdz.day", actualDayName))
                    .map(Schedule::getSlots).ifPresent(slots -> {
                        scheduleSlots = slots;
                    });
        });
    }

    public List<SessionData> getFilteredSessionData() {
        String time = System.getProperty("org.tweetwallfx.vdz.time");
        OffsetTime liveOffset =
                null == time
                        ? OffsetTime.now(ZoneOffset.UTC).plus(TimeZone.getDefault().getRawOffset(),
                                ChronoUnit.MILLIS)
                        : OffsetTime.parse(time);
        return SessionData.from(scheduleSlots, liveOffset);
    }

    @Override
    public String getName() {
        return "Schedule-VoxxedDaysZurich";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public DataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new ScheduleDataProvider();
        }

        @Override
        public Class<ScheduleDataProvider> getDataProviderClass() {
            return ScheduleDataProvider.class;
        }
    }
}
