/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2026 TweetWallFX
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.image.Image;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

/**
 * Data provider collecting the photo images of tweets in order to present them
 * as an image mosaic.
 */
public class ImageMosaicDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMosaicDataProvider.class);
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
                || (tweet.isRetweet() && !config.includeRetweets())) {
            return;
        }
        LOG.debug("processing new Tweet: {}", tweet.getId());
        tweet.getMediaEntries().stream()
                .filter(MediaTweetEntryType.photo::isType)
                .forEach(mte -> addImage(mte, tweet.getCreatedAt()));
    }

    /**
     * Returns an unmodifiable view on the images collected so far.
     *
     * @return an unmodifiable {@link List} of the collected {@link ImageStore}
     * instances
     */
    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(final MediaTweetEntry mte, final LocalDateTime date) {
        PhotoImageCache.INSTANCE.getCachedOrLoad(mte, urlc -> {
            if (images.addIfAbsent(new ImageStore(urlc, date.toInstant(ZoneOffset.UTC)))) {
                LOG.info("Added ImageStore for mediaID: {}", mte.getId());
            }
            if (config.maxCacheSize() < images.size()) {
                images.sort(Comparator.comparing(ImageStore::getInstant));
                images.remove(0);
            }
        });
    }

    /**
     * Factory implementation creating {@link ImageMosaicDataProvider} instances.
     */
    public static class FactoryImpl implements DataProvider.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() {
        }

        @Override
        public ImageMosaicDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new ImageMosaicDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<ImageMosaicDataProvider> getDataProviderClass() {
            return ImageMosaicDataProvider.class;
        }
    }

    /**
     * POJO used to configure {@link ImageMosaicDataProvider}.
     *
     * @param includeRetweets whether retweets are to be included, defaults to
     * {@code false}
     * @param maxCacheSize the maximum number of images to keep, defaults to
     * {@code 40}
     */
    public static record Config(
            Boolean includeRetweets,
            Integer maxCacheSize) {

        /**
         * Creates a new instance applying the documented default values.
         */
        public Config {
            includeRetweets = Objects.requireNonNullElse(includeRetweets, false);
            maxCacheSize = Objects.requireNonNullElse(maxCacheSize, 40);
        }
    }

    /**
     * Immutable holder of an image together with its digest and the timestamp
     * of the tweet it originated from.
     */
    public static final class ImageStore {

        private final Image image;
        private final String digest;
        private final Instant instant;

        /**
         * Creates a new instance reading the image from the given content.
         *
         * @param urlc the URL content providing the image data
         * @param instant the instant the image shall be associated with
         */
        public ImageStore(final URLContent urlc, final Instant instant) {
            this.digest = urlc.digest();
            this.image = new Image(urlc.getInputStream());
            this.instant = instant;
        }

        /**
         * Returns the digest identifying the image data source.
         *
         * @return the digest
         */
        public String getDigest() {
            return digest;
        }

        /**
         * Returns the instant this image is associated with.
         *
         * @return the instant
         */
        public Instant getInstant() {
            return instant;
        }

        /**
         * Returns the image.
         *
         * @return the image
         */
        @SuppressFBWarnings
        public Image getImage() {
            return image;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.digest);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof ImageStore other
                    && Objects.nonNull(this.digest)
                    && Objects.nonNull(other.digest)
                    && Objects.equals(this.digest, other.digest);
        }
    }
}
