/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

public class ImageMosaicDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LogManager.getLogger(ImageMosaicDataProvider.class);
    private final CopyOnWriteArrayList<ImageStore> images = new CopyOnWriteArrayList<>();
    private final Config config;

    private ImageMosaicDataProvider(final Config config) {
        this.config = config;
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        processHistoryTweet(tweet);
    }

    @Override
    public void processHistoryTweet(final Tweet tweet) {
        LOG.info("new Tweet received: {}", tweet.getId());
        if (null == tweet.getMediaEntries()
                || (tweet.isRetweet() && !config.includeRetweeets())) {
            return;
        }
        LOG.debug("processing new Tweet: {}", tweet.getId());
        Arrays.stream(tweet.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .forEach(mte -> addImage(mte, tweet.getCreatedAt()));
    }

    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(final MediaTweetEntry mte, final Date date) {
        PhotoImageCache.INSTANCE.getCachedOrLoad(mte, urlc -> {
            if (images.addIfAbsent(new ImageStore(urlc, date.toInstant()))) {
                LOG.info("Added ImageStore for mediaID: {}", mte.getId());
            }
            if (config.maxCacheSize() < images.size()) {
                images.sort(Comparator.comparing(ImageStore::instant));
                images.remove(0);
            }
        });
    }

    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public ImageMosaicDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new ImageMosaicDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<ImageMosaicDataProvider> getDataProviderClass() {
            return ImageMosaicDataProvider.class;
        }
    }

    public static record Config(
            boolean includeRetweeets,
            int maxCacheSize) {
    }

    public static record ImageStore(
            Image image,
            String digest,
            Instant instant) {

        public ImageStore(final URLContent urlc, final Instant instant) {
            this(new Image(urlc.getInputStream()), urlc.getDigest(), instant);
        }
    }
}
