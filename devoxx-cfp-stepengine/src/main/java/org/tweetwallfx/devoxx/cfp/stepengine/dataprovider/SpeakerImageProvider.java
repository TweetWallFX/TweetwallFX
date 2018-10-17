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
import java.util.Objects;
import java.util.function.Supplier;
import javafx.scene.image.Image;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Speaker;
import org.tweetwall.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.stepengine.dataproviders.ProfileImageCache;

/**
 * Utility to provide a simple api to get the cached speaker image.
 */
public final class SpeakerImageProvider implements DataProvider {

    private SpeakerImageProvider() {
        // prevent instantiation
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
            // use stand-in for non-loadable
            return null; // TODO: fix with https://github.com/TweetWallFX/TweetwallFX/issues/310
        } else {
            return new Image(supplier.get());
        }
    }

    public void updateSpeakerImages() {
        CFPClient.getClient()
                .getSpeakers()
                .stream()
                .map(Speaker::getAvatarURL)
                .filter(Objects::nonNull)
                .forEach(urlString -> ProfileImageCache.INSTANCE.getCachedOrLoad(urlString, this::handleURLContent));
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
            return new SpeakerImageProvider();
        }

        @Override
        public Class<SpeakerImageProvider> getDataProviderClass() {
            return SpeakerImageProvider.class;
        }
    }
}
