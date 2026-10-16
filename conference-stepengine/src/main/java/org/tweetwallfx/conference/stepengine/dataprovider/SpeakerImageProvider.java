/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

import static org.tweetwallfx.util.Nullable.nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.scene.image.Image;

import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.conference.api.ConferenceClient;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.stepengine.dataproviders.ProfileImageCache;

/**
 * Utility to provide a simple api to get the cached speaker image.
 */
public final class SpeakerImageProvider implements DataProvider, DataProvider.Scheduled {

    private final Config config;

    private SpeakerImageProvider(final Config config) {
        this.config = config;
    }

    /**
     * Returns the (cached) image of the given speaker.
     *
     * @param speaker the speaker to get the image for, may be {@code null}
     * @return the speaker image or the configured default image when no
     *         speaker image is available
     */
    public Image getSpeakerImage(final Speaker speaker) {
        return null == speaker
                ? getDefaultClasspathImage()
                : Optional.ofNullable(speaker.getAvatarURL())
                        .map(this::getSpeakerImage)
                        .orElseGet(this::getDefaultClasspathImage);
    }

    private Image getSpeakerImage(final String avatarURL) {
        final URLContent urlc = ProfileImageCache.INSTANCE.getCachedOrLoad(avatarURL);

        if (null == urlc) {
            // avatar url is not in cache (url content was not loadable)
            final String urlReplacement = config.urlReplacements().get(avatarURL);

            return null == urlReplacement
                    // use stand-in for non-loadable
                    ? getDefaultClasspathImage()
                    // look for configured replacement
                    : getSpeakerImage(urlReplacement);
        } else {
            return new Image(urlc.getInputStream());
        }
    }

    private Image getDefaultClasspathImage() {
        return new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream(config.noImageResource()));
    }

    @Override
    public ScheduledConfig getScheduleConfig() {
        return config;
    }

    /**
     * {@return a stream of the images of all conference speakers}
     */
    public Stream<Image> getImages() {
        return ConferenceClient.getClient()
                .getSpeakers()
                .stream().map(this::getSpeakerImage);
    }

    /**
     * Preloads the speaker images into the profile image cache.
     */
    @Override
    public void run() {
        ConferenceClient.getClient()
                .getSpeakers()
                .stream()
                .map(Speaker::getAvatarURL)
                .filter(Objects::nonNull)
                .forEach(urlString -> ProfileImageCache.INSTANCE.getCachedOrLoad(urlString, this::handleURLContent));

        config.urlReplacements()
                .forEach((k, v) -> ProfileImageCache.INSTANCE.putCachedContent(v, this::handleURLContent));
    }

    private void handleURLContent(final URLContent urlc) {
        // do nothing
    }

    /**
     * Implementation of {@link DataProvider.Factory} as Service implementation
     * creating {@link SpeakerImageProvider}.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() { }

        @Override
        public SpeakerImageProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new SpeakerImageProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<SpeakerImageProvider> getDataProviderClass() {
            return SpeakerImageProvider.class;
        }
    }

    /**
     * POJO used to configure {@link SpeakerImageProvider}.
     *
     * @param noImageResource Classpath entry for the image used when no speaker
     *                        image is available. Defaults to
     *                        {@code "icons/anonymous.jpg"}.
     * @param urlReplacements Mapping of unloadable profile image URLs to
     *                        loadable ones (required due to e.g. changed
     *                        profile pics).
     * @param scheduleType    The type of scheduling to perform. Defaults to
     *                        {@link ScheduleType#FIXED_RATE}.
     * @param initialDelay    Delay until the first execution in seconds.
     *                        Defaults to {@code 0L}.
     * @param scheduleDuration Fixed rate of / delay between consecutive
     *                        executions in seconds. Defaults to
     *                        {@code 1800L}.
     */
    public static record Config(
            String noImageResource,
            Map<String, String> urlReplacements,
            ScheduleType scheduleType,
            Long initialDelay,
            Long scheduleDuration) implements ScheduledConfig {

        /**
         * Creates a new instance applying the documented default values to
         * {@code null} arguments.
         */
        public Config {
            noImageResource = Objects.requireNonNullElse(noImageResource, "icons/anonymous.jpg");
            urlReplacements = nullable(urlReplacements);
            scheduleType = Objects.requireNonNullElse(scheduleType, ScheduleType.FIXED_RATE);
            initialDelay = Objects.requireNonNullElse(initialDelay, 0L);
            scheduleDuration = Objects.requireNonNullElse(scheduleDuration, 30 * 60L);
        }

        /**
         * {@return the mapping of unloadable profile image URLs to loadable
         * ones, never {@code null}}
         */
        @Override
        public Map<String, String> urlReplacements() {
            return nullable(urlReplacements);
        }
    }
}
