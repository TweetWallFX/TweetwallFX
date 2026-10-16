/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2026 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.dataprovider;

import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.ScheduleSlot;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * DataProvider Implementation for Schedule Data
 */
public class ScheduleDataProvider implements DataProvider, DataProvider.Scheduled {

    private volatile List<ScheduleSlot> scheduleSlots = Collections.emptyList();
    private final Config config;
    private volatile boolean initialized = false;

    private ScheduleDataProvider(final Config config) {
        this.config = config;
    }

    /**
     * {@return the scheduling configuration of this data provider}
     */
    @Override
    public ScheduledConfig getScheduleConfig() {
        return config;
    }

    /**
     * {@return {@code true} as this data provider has to be initialized before
     * its first use}
     */
    @Override
    public boolean requiresInitialization() {
        return true;
    }

    /**
     * {@return {@code true} when the data has been loaded, {@code false}
     * otherwise}
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Loads the schedule of the current day from the conference client.
     */
    @Override
    public void run() {
        scheduleSlots = ConferenceClient.getClient()
                .getSchedule(Optional
                        .ofNullable(System.getProperty("org.tweetwallfx.scheduledata.day"))
                        .orElseGet(()
                                -> LocalDateTime.now(ZoneId.systemDefault())
                                .getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                .toLowerCase(Locale.ENGLISH)));
        initialized = true;
    }

    /**
     * {@return an immutable list of the sessions starting within the
     * configured time frame}
     */
    public List<SessionData> getFilteredSessionData() {
        ZoneId zoneId = Optional.ofNullable(System.getProperty("org.tweetwallfx.scheduledata.zone"))
                .map(ZoneId::of)
                .orElseGet(ZoneId::systemDefault);
        OffsetTime liveOffset = Optional.ofNullable(System.getProperty("org.tweetwallfx.scheduledata.time"))
                .map(OffsetTime::parse)
                .orElseGet(() -> OffsetTime.now(zoneId));
        return SessionData.from(scheduleSlots, liveOffset, zoneId, config.sessionsShallStartWithinMinutes);
    }

    /**
     * Implementation of {@link DataProvider.Factory} as Service implementation
     * creating {@link ScheduleDataProvider}.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() { }

        /**
         * {@return a new {@link ScheduleDataProvider} instance as configured by
         * the given data provider setting}
         *
         * @param dataProviderSetting the data provider setting providing the
         *                            configuration for the created provider
         */
        @Override
        public DataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new ScheduleDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        /**
         * {@return the class of the data provider created by this factory}
         */
        @Override
        public Class<ScheduleDataProvider> getDataProviderClass() {
            return ScheduleDataProvider.class;
        }
    }

    /**
     * POJO used to configure {@link ScheduleDataProvider}.
     *
     * @param scheduleType                  The type of scheduling to perform.
     *                                      Defaults to
     *                                      {@link ScheduleType#FIXED_RATE}.
     * @param initialDelay                  Delay until the first execution in
     *                                      seconds. Defaults to {@code 0L}.
     * @param scheduleDuration              Fixed rate of / delay between
     *                                      consecutive executions in seconds.
     *                                      Defaults to {@code 300L}.
     * @param sessionsShallStartWithinMinutes Number of minutes within which the
     *                                      displayable sessions shall start.
     *                                      Defaults to {@code 240L}.
     */
    public record Config(
            ScheduleType scheduleType,
            Long initialDelay,
            Long scheduleDuration,
            Long sessionsShallStartWithinMinutes) implements ScheduledConfig {

        /**
         * Creates a new instance applying the documented default values to
         * {@code null} arguments.
         */
        public Config {
            scheduleType = Objects.requireNonNullElse(scheduleType, ScheduleType.FIXED_RATE);
            initialDelay = Objects.requireNonNullElse(initialDelay, 0L);
            scheduleDuration = Objects.requireNonNullElse(scheduleDuration, 5 * 60L);
            sessionsShallStartWithinMinutes = Objects.requireNonNullElse(sessionsShallStartWithinMinutes, 4 * 60L);
        }
    }
}
