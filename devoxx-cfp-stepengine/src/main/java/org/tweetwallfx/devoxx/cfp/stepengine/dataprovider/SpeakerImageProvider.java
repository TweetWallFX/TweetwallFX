/*
 * The MIT License
 *
 * Copyright 2015-2018 TweetWallFX
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

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javafx.scene.image.Image;
import org.tweetwallfx.devoxx.api.cfp.client.CFPClient;
import org.tweetwallfx.devoxx.api.cfp.client.Speaker;
import org.tweetwallfx.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.stepengine.dataproviders.ProfileImageCache;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * Utility to provide a simple api to get the cached speaker image.
 */
public final class SpeakerImageProvider implements DataProvider, DataProvider.Scheduled {

    private final Config config;

    private SpeakerImageProvider(final Config config) {
        this.config = config;
    }

    public Image getSpeakerImage(final Speaker speaker) {
        return getSpeakerImage(speaker.getAvatarURL());
    }

    public Image getSpeakerImage(final SpeakerReference speakerReference) {
        return getSpeakerImage(speakerReference.getSpeaker().map(Speaker::getAvatarURL).orElse(null));
    }

    private Image getSpeakerImage(final String avatarURL) {
        final Supplier<InputStream> supplier = ProfileImageCache.INSTANCE.getCachedOrLoad(avatarURL);

        if (null == supplier) {
            // avatar url is not in cache (url content was not loadable)
            // look for configured replacement
            final String urlReplacement = config.getUrlReplacements().get(avatarURL);
            Image image = null == urlReplacement
                    ? null
                    : getSpeakerImage(urlReplacement);

            if (null == image) {
                // use stand-in for non-loadable
                image = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream(config.getNoImageResource()));
            }

            return image;
        } else {
            return new Image(supplier.get());
        }
    }

    @Override
    public ScheduledConfig getScheduleConfig() {
        return config;
    }

    @Override
    public void run() {
        CFPClient.getClient()
                .getSpeakers()
                .stream()
                .map(Speaker::getAvatarURL)
                .filter(Objects::nonNull)
                .forEach(urlString -> ProfileImageCache.INSTANCE.getCachedOrLoad(urlString, this::handleURLContent));

        config.getUrlReplacements()
                .forEach((k, v) -> ProfileImageCache.INSTANCE.putCachedContent(v, this::handleURLContent));
    }

    private void handleURLContent(final Supplier<InputStream> urlc) {
        // do nothing
    }

    /**
     * Implementation of {@link DataProvider.Factory} as Service implementation
     * creating {@link SpeakerImageProvider}.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public SpeakerImageProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new SpeakerImageProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<SpeakerImageProvider> getDataProviderClass() {
            return SpeakerImageProvider.class;
        }
    }

    public static class Config implements ScheduledConfig {

        private String noImageResource = "icons/user1-256x256.png";
        private Map<String, String> urlReplacements = Collections.emptyMap();
        /**
         * The type of scheduling to perform. Defaults to
         * {@link ScheduleType#FIXED_RATE}.
         */
        private ScheduleType scheduleType = ScheduleType.FIXED_RATE;
        /**
         * Delay until the first execution in seconds. Defaults to {@code 0L}.
         */
        private long initialDelay = 0L;
        /**
         * Fixed rate of / delay between consecutive executions in seconds.
         * Defaults to {@code 1800L}.
         */
        private long scheduleDuration = 30 * 60L;

        public String getNoImageResource() {
            return noImageResource;
        }

        public void setNoImageResource(final String noImageResource) {
            this.noImageResource = noImageResource;
        }

        public Map<String, String> getUrlReplacements() {
            return urlReplacements;
        }

        public void setUrlReplacements(final Map<String, String> urlReplacements) {
            this.urlReplacements = null == urlReplacements || urlReplacements.isEmpty()
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(urlReplacements);
        }

        @Override
        public ScheduleType getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(final ScheduleType scheduleType) {
            this.scheduleType = scheduleType;
        }

        @Override
        public long getInitialDelay() {
            return initialDelay;
        }

        public void setInitialDelay(final long initialDelay) {
            this.initialDelay = initialDelay;
        }

        @Override
        public long getScheduleDuration() {
            return scheduleDuration;
        }

        public void setScheduleDuration(final long scheduleDuration) {
            this.scheduleDuration = scheduleDuration;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "scheduleType", getScheduleType(),
                    "initialDelay", getInitialDelay(),
                    "scheduleDuration", getScheduleDuration(),
                    "noImageResource", getNoImageResource(),
                    "urlReplacements", getUrlReplacements()
            )) + " extends " + super.toString();
        }
    }
}
