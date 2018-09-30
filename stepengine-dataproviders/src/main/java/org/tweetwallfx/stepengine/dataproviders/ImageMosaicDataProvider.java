/*
 * The MIT License
 *
 * Copyright 2014-2018 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.tweetwall.util.ToString.createToString;
import static org.tweetwall.util.ToString.map;
import org.tweetwallfx.stepengine.dataproviders.MediaCache.CachedMedia;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

public class ImageMosaicDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LogManager.getLogger(ImageMosaicDataProvider.class);
    private final Executor imageLoader = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("Image-Downloader");
        t.setDaemon(true);
        return t;
    });
    private final List<ImageStore> images = new CopyOnWriteArrayList<>();
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
        LOG.info("new Tweet received");
        if (null == tweet.getMediaEntries()
                || (tweet.isRetweet() && !config.isIncludeRetweets())
                || tweet.getUser().getFollowersCount() < 25) {
            return;
        }
        Arrays.stream(tweet.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .forEach(me -> {
                    String url;
                    switch (me.getSizes().keySet().stream().max(Comparator.naturalOrder()).get()) {
                        case 0:
                            url = me.getMediaUrl() + ":thumb";
                            break;
                        case 1:
                            url = me.getMediaUrl() + ":small";
                            break;
                        case 2:
                            url = me.getMediaUrl() + ":medium";
                            break;
                        case 3:
                            url = me.getMediaUrl() + ":large";
                            break;
                        default:
                            throw new RuntimeException("Illegal value");
                    }
                    addImage(me.getId(), url, tweet.getCreatedAt());
                });
    }

    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(final long mediaId, final String url, final Date date) {
        Task<Optional<ImageStore>> task = new Task<Optional<ImageStore>>() {
            @Override
            protected Optional<ImageStore> call() throws Exception {
                if (MediaCache.hasCachedMedia(mediaId)) {
                    return MediaCache.getCachedMedia(mediaId).map(cm -> new ImageStore(
                            new Image(cm.getInputStream()),
                            date.toInstant(),
                            mediaId));
                }
                try (InputStream in = new URL(url).openStream()) {
                    CachedMedia image = new CachedMedia(in);
                    MediaCache.putCachedMedia(mediaId, image);
                    return Optional.of(new ImageStore(
                            new Image(image.getInputStream()),
                            date.toInstant(),
                            mediaId));
                }
            }
        };

        task.setOnSucceeded((event) -> {
            task.getValue().ifPresent(images::add);
            if (config.getMaxCacheSize() < images.size()) {
                images.sort(Comparator.comparing(ImageStore::getInstant));
                ImageStore removeLast = images.remove(images.size() - 1);
            }
        });

        imageLoader.execute(task);
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

    public static class Config {

        private boolean includeRetweets = false;
        private int maxCacheSize = 40;

        public boolean isIncludeRetweets() {
            return includeRetweets;
        }

        public void setIncludeRetweets(final boolean includeRetweets) {
            this.includeRetweets = includeRetweets;
        }

        public int getMaxCacheSize() {
            return maxCacheSize;
        }

        public void setMaxCacheSize(final int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "includeRetweets", isIncludeRetweets(),
                    "maxCacheSize", getMaxCacheSize()
            )) + " extends " + super.toString();
        }
    }

    public static class ImageStore {

        private final Image image;
        private final Instant instant;
        private final long mediaId;

        public ImageStore(final Image image, final Instant instant, final long mediaId) {
            this.image = image;
            this.instant = instant;
            this.mediaId = mediaId;
        }

        public Instant getInstant() {
            return instant;
        }

        public Image getImage() {
            return image;
        }

        public long getMediaId() {
            return mediaId;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.image);
            hash = 97 * hash + Objects.hashCode(this.instant);
            hash = 97 * hash + (int) (this.mediaId ^ (this.mediaId >>> 32));
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (null == obj || getClass() != obj.getClass()) {
                return false;
            }

            final ImageStore other = (ImageStore) obj;
            boolean result = true;

            result &= this.mediaId == other.mediaId;
            result &= Objects.equals(this.image, other.image);
            result &= Objects.equals(this.instant, other.instant);

            return result;
        }
    }
}
