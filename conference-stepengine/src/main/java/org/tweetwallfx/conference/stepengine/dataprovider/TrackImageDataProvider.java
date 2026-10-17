/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 TweetWallFX
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

import java.util.Objects;
import javafx.scene.image.Image;

import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * DataProvider providing the (cached) avatar images of conference tracks.
 */
public class TrackImageDataProvider implements DataProvider {

    private final Config config;

    private TrackImageDataProvider(final Config config) {
        this.config = config;
    }

    /**
     * Returns the (cached) avatar image located at the given URL.
     *
     * @param url the URL of the track avatar image
     * @return the loaded image
     */
    public Image getImage(final String url) {
        return new Image(
                TrackImageCache.INSTANCE.getCachedOrLoad(url).getInputStream(),
                config.profileWidth(),
                config.profileHeight(),
                config.preserveRation(),
                config.smooth());
    }

    /**
     * Implementation of {@link DataProvider.Factory} as Service implementation
     * creating {@link TrackImageDataProvider}.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() { }

        @Override
        public TrackImageDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new TrackImageDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<TrackImageDataProvider> getDataProviderClass() {
            return TrackImageDataProvider.class;
        }
    }

    /**
     * Configuration POJO for the {@link TrackImageDataProvider}.
     *
     * @param profileWidth   The width the loaded profile image shall be resized
     *                       to. Defaults to {@code 32}.
     * @param profileHeight  The height the loaded profile image shall be
     *                       resized to. Defaults to {@code 32}.
     * @param preserveRation Whether the aspect ratio of the loaded image is
     *                       preserved. Defaults to {@code true}.
     * @param smooth         Whether the loaded image is smoothed. Defaults to
     *                       {@code true}.
     */
    public static record Config(
            Integer profileWidth,
            Integer profileHeight,
            Boolean preserveRation,
            Boolean smooth) {

        /**
         * Creates a new instance applying the documented default values to
         * {@code null} arguments.
         */
        public Config {
            profileWidth = Objects.requireNonNullElse(profileWidth, 32);
            profileHeight = Objects.requireNonNullElse(profileHeight, 32);
            preserveRation = Objects.requireNonNullElse(preserveRation, true);
            smooth = Objects.requireNonNullElse(smooth, true);
        }
    }
}
