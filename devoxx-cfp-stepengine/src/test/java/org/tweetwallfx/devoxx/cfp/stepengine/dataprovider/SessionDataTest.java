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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.tweetwallfx.devoxx.api.cfp.client.Schedule;
import org.tweetwallfx.util.JsonDataConverter;

/**
 * Tests for Session Data Filtering
 */
public class SessionDataTest {

    @Test
    public void testFrom1035OnMonday() throws IOException {
        System.out.println("from");
        URL jsonFile = this.getClass().getResource("/Devoxx2017BeMonday.json");
        try (InputStream inputStream = jsonFile.openStream()) {
            Schedule schedule = JsonDataConverter.convertFromInputStream(inputStream, Schedule.class);
            List<SessionData> result = SessionData.from(schedule, OffsetTime.parse("10:35Z"), ZoneId.systemDefault());
            assertEquals(6, result.size());
        }
    }

    @Test
    public void testFrom1300OnMonday() throws IOException {
        System.out.println("from");
        URL jsonFile = this.getClass().getResource("/Devoxx2017BeMonday.json");
        try (InputStream inputStream = jsonFile.openStream()) {
            Schedule schedule = JsonDataConverter.convertFromInputStream(inputStream, Schedule.class);
            List<SessionData> result = SessionData.from(schedule, OffsetTime.parse("13:00Z"), ZoneId.systemDefault());
            assertEquals(7, result.size());
        }
    }
}
