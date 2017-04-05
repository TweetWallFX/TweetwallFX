/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
package org.tweetwallfx.controls.dataprovider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;

/**
 *
 * @author sven
 */
public class ImageMosaicDataProvider implements DataProvider {

    private static final Logger log = LogManager.getLogger(ImageMosaicDataProvider.class);

    private Executor imageLoader = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("Image-Downloader");
            t.setDaemon(true);
            return t;
        });

    private List<ImageStore> images = new CopyOnWriteArrayList<>();

    public ImageMosaicDataProvider(TweetStream tweetStream) {
        tweetStream.onTweet(tweet -> processTweet(tweet));
    }

    public void processTweet(Tweet tweet) {
        log.info("new Tweet received");
        if (null == tweet.getMediaEntries() || tweet.isRetweet()) {
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
                    addImage(tweet, me.getId(), me.getId() + me.getMediaUrl().substring(me.getMediaUrl().lastIndexOf(".")), url, tweet.getCreatedAt());
                });
    }

    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(Tweet tweet, long mediaId, String fileName, String url, Date date) {

        Task<Optional<ImageStore>> task = new Task<Optional<ImageStore>>() {
            @Override
            protected Optional<ImageStore> call() throws Exception {
                File file = new File("dump/" + fileName);
                if (file.exists()) {
                    return Optional.empty();
                }
                boolean mkdirs = file.getParentFile().mkdirs();
                if (mkdirs) {
                    log.info("directory for dumping files created!");
                }
                downloadContent(new URL(url), file);
                return Optional.of(new ImageStore(tweet, new Image(file.toPath().toUri().toURL().toExternalForm()), file, date.toInstant(), mediaId));
            }
        };

        task.setOnSucceeded((event) -> {
            task.getValue().ifPresent(images::add);
            if (40 < images.size()) {
                images.sort(Comparator.comparing(ImageStore::getInstant));
                ImageStore removeLast = images.remove(images.size()-1);
                removeLast.file.delete();
            }
        });

        imageLoader.execute(task);
    }

    private static void downloadContent(URL url, File file) {
        file.deleteOnExit();
        if (file.getParentFile().mkdirs()) {
            log.info("directory created " + file.getPath());
        }

        try (InputStream inputStream = url.openStream();
                FileOutputStream outputStream = new FileOutputStream(file)) {
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException exception) {
        }
    }

    @Override
    public String getName() {
        return "MosaicDataProvider";
    }
    
    public static class ImageStore {
        private final Tweet tweet;
        private final Image image;
        private final Instant instant;
        private final File file;
        private final long mediaId;

        public ImageStore(Tweet tweet, Image image, File file, Instant instant, long mediaId) {
            this.tweet = tweet;
            this.image = image;
            this.file = file;
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
            hash = 97 * hash + Objects.hashCode(this.file);
            hash = 97 * hash + (int) (this.mediaId ^ (this.mediaId >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
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
            if (!Objects.equals(this.file, other.file)) {
                return false;
            }
            return true;
        }

    }

}
