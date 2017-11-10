/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwallfx.controls.dataprovider;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 * @author Sven Reimers
 */
public class ImageMosaicDataProvider implements DataProvider, DataProvider.HistoryAware {

    private static final Logger LOG = LogManager.getLogger(ImageMosaicDataProvider.class);
    private final MediaCache cache;
    private final Executor imageLoader = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("Image-Downloader");
        t.setDaemon(true);
        return t;
    });
    private final List<ImageStore> images = new CopyOnWriteArrayList<>();

    private ImageMosaicDataProvider(final TweetStream tweetStream) {
        cache = MediaCache.INSTANCE;
        tweetStream.onTweet(tweet -> processTweet(tweet));
    }

    @Override
    public void processTweet(final Tweet tweet) {
        LOG.info("new Tweet received");
        if (null == tweet.getMediaEntries() || tweet.isRetweet() || tweet.getUser().getFollowersCount() < 25) {
            return;
        }
        Arrays.stream(tweet.getMediaEntries())
                .filter(me -> me.getType().equals("photo"))
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
                    addImage(tweet, me.getId(), url, tweet.getCreatedAt());
                });
    }

    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(final Tweet tweet, final long mediaId, final String url, final Date date) {
        Task<Optional<ImageStore>> task = new Task<Optional<ImageStore>>() {
            @Override
            protected Optional<ImageStore> call() throws Exception {
                if (cache.hasCachedMedia(mediaId)) {
                    return Optional.empty();
                }
                try (InputStream in = new URL(url).openStream()) {
                    CachedMedia image = new CachedMedia(in);
                    cache.putCachedMedia(mediaId, image);
                    return Optional.of(new ImageStore(tweet, new Image(image.getInputStream()),
                            date.toInstant(), mediaId));
                }
            }
        };

        task.setOnSucceeded((event) -> {
            task.getValue().ifPresent(images::add);
            if (40 < images.size()) {
                images.sort(Comparator.comparing(ImageStore::getInstant));
                ImageStore removeLast = images.remove(images.size() - 1);
            }
        });

        imageLoader.execute(task);
    }

    @Override
    public String getName() {
        return "MosaicDataProvider";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public ImageMosaicDataProvider create(final TweetStream tweetStream) {
            return new ImageMosaicDataProvider(tweetStream);
        }

        @Override
        public Class<ImageMosaicDataProvider> getDataProviderClass() {
            return ImageMosaicDataProvider.class;
        }
    }

    public static class ImageStore {

        private final Tweet tweet;
        private final Image image;
        private final Instant instant;
        private final long mediaId;

        public ImageStore(final Tweet tweet, final Image image, final Instant instant, final long mediaId) {
            this.tweet = tweet;
            this.image = image;
            this.instant = instant;
            this.mediaId = mediaId;
        }

        public Instant getInstant() {
            return instant;
        }

        public Tweet getTweet() {
            return tweet;
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
            hash = 97 * hash + Objects.hashCode(this.tweet);
            hash = 97 * hash + Objects.hashCode(this.image);
            hash = 97 * hash + Objects.hashCode(this.instant);
            hash = 97 * hash + (int) (this.mediaId ^ (this.mediaId >>> 32));
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImageStore other = (ImageStore) obj;
            if (this.mediaId != other.mediaId) {
                return false;
            }
            if (!Objects.equals(this.tweet, other.tweet)) {
                return false;
            }
            if (!Objects.equals(this.image, other.image)) {
                return false;
            }
            if (!Objects.equals(this.instant, other.instant)) {
                return false;
            }
            return true;
        }
    }
}
