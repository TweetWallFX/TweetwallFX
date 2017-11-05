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
import java.time.OffsetTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.tweet.api.TweetStream;

/**
 * DataProvider Implementation for Schedule Data
 * @author Sven Reimers
 */
public class ScheduleDataProvider implements DataProvider {

    private Schedule schedule;

    private ScheduleDataProvider() {
        updateSchedule();
    }

    private void updateSchedule() {        
        String actualDayName = LocalDateTime.now().getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        schedule = CFPClient.getClient().getSchedule(System.getProperty("org.tweetwallfx.devoxxbe17.day", actualDayName));
    }

    public List<SessionData> getFilteredSessionData() {        
        String time = System.getProperty("org.tweetwallfx.devoxxbe17.time");
        OffsetTime liveOffset = null != time ? OffsetTime.parse(time) : 
                OffsetTime.now().minus(TimeZone.getDefault().getRawOffset(), ChronoUnit.MILLIS);
        return  SessionData.from(schedule, liveOffset);
    }

    @Override
    public String getName() {
        return "Schedule-Devoxx2017BE";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public DataProvider create(TweetStream tweetStream) {
            return new ScheduleDataProvider();
        }

    }

}
