/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
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
package org.tweetwallfx.threed;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * Tasks to perform a search on Twitter for some hashtag, create an HBox with
 * each tweets, crate a snapshot and then load the image as diffuseMap of a
 * segmented torus Tasks and blockingQueues take care of this complex process
 *
 * @author José Pereda
 */
public class TweetsToTori {

    private static final String TWEET_FILE_PREFIX = "tweet_";
    private static final String WORKING_DIR = System.getProperty("user.home") + File.separatorChar + ".devoxx";

    private final ExecutorService saveTweetsExecutor = createExecutor("SaveTweets");
    private final SaveTweetsTask saveTweetsTask;

    private final AtomicInteger contTweets = new AtomicInteger();

    private final Group tori;

    public TweetsToTori(final Tweeter tweeter, String searchText, Group tori) {
        this.tori = tori;
        this.saveTweetsTask = new SaveTweetsTask(tweeter, searchText);
    }

    public void start() {
        File dir = new File(WORKING_DIR);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.out.println("Failed creating dir: " + dir);
            }
        } else {
            try {
                Files.list(dir.toPath()).forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException ex) {
                        System.out.println("Exception during temp file clean up" + ex);
                    }
                });
            } catch (IOException ioex) {
                System.out.println("Exception during temp file clean up" + ioex);
            }
        }

        contTweets.set(0);
        saveTweetsExecutor.execute(saveTweetsTask);
    }

    public void stop() {
        saveTweetsExecutor.shutdown();
        try {
            saveTweetsExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        }
    }

    private static class TweetsCreationTask extends Task<Void> {

        private final String searchText;
        private TweetStream stream;
        private final Tweeter tweeter;
        private final BlockingQueue<Parent> tweets;

        public TweetsCreationTask(Tweeter tweeter, String searchText, BlockingQueue<Parent> tweets) {
            this.tweeter = tweeter;
            this.searchText = searchText;
            this.tweets = tweets;
        }

        @Override
        protected Void call() throws Exception {
            if (tweeter != null) {
                stream = tweeter.createTweetStream(new TweetFilterQuery().track(new String[]{searchText}));
                stream.onTweet(tweet -> {
                    try {
                        System.out.println("Tw: " + tweet.getText());
                        tweets.put(createTweetInfoBox(tweet));
                    } catch (InterruptedException ex) {
                        System.out.println("Error: " + ex);
                    }
                });
            }

            return null;
        }

        private static Parent createTweetInfoBox(Tweet info) {
            HBox hbox = new HBox(20);
            hbox.setStyle("-fx-padding: 20px;");

            HBox hImage = new HBox();
            hImage.setPadding(new Insets(10));
            Image image = new Image(info.getUser().getProfileImageUrl(), 48, 48, true, false);
            ImageView imageView = new ImageView(image);
            Rectangle clip = new Rectangle(48, 48);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            imageView.setClip(clip);
            hImage.getChildren().add(imageView);

            HBox hName = new HBox(20);
            Label name = new Label(info.getUser().getName());
            name.setStyle("-fx-font: 32px \"Andalus\"; -fx-text-fill: #292F33; -fx-font-weight: bold;");
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            Label handle = new Label("@" + info.getUser().getScreenName() + " · " + df.format(info.getCreatedAt()));
            handle.setStyle("-fx-font: 28px \"Andalus\"; -fx-text-fill: #8899A6;");
            hName.getChildren().addAll(name, handle);

            Text text = new Text(info.getText());
            text.setWrappingWidth(550);
            text.setStyle("-fx-font: 24px \"Andalus\"; -fx-fill: #292F33;");
            VBox vbox = new VBox(20);
            vbox.getChildren().addAll(hName, text);
            hbox.getChildren().addAll(hImage, vbox);

            return hbox;
        }
    }

    private static class TweetsSnapshotTask extends Task<Void> {

        private final BlockingQueue<Parent> tweets;
        private final BlockingQueue<BufferedImage> images;

        TweetsSnapshotTask(BlockingQueue<Parent> tweets, BlockingQueue<BufferedImage> images) {
            this.tweets = tweets;
            this.images = images;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                images.put(snapshotTweet(tweets.take()));
            }
            return null;
        }

        private BufferedImage snapshotTweet(final Parent tweetContainer) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            // render the chart in an offscreen scene (scene is used to allow css processing) and snapshot it to an image.
            // the snapshot is done in runlater as it must occur on the javafx application thread.
            final SimpleObjectProperty<BufferedImage> imageProperty = new SimpleObjectProperty<>();
            Platform.runLater(() -> {
                Scene snapshotScene = new Scene(tweetContainer);
                final SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.WHITESMOKE);
                tweetContainer.snapshot(result -> {
                    imageProperty.set(SwingFXUtils.fromFXImage(result.getImage(), null));
                    latch.countDown();
                    return null;
                }, params, null);
            });

            latch.await();

            return imageProperty.get();
        }
    }

    private class PngsExportTask extends Task<Void> {

        private final BlockingQueue<BufferedImage> images;
        private final BlockingQueue<Image> diffuseMaps;

        PngsExportTask(BlockingQueue<BufferedImage> images, BlockingQueue<Image> diffuseMaps) {
            this.images = images;
            this.diffuseMaps = diffuseMaps;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                exportPng(images.take(), getTweetFilePath(contTweets.get()));
            }
            return null;
        }

        private void exportPng(BufferedImage image, String filename) {
            try {
                File img = new File(filename);
                if (img.exists()) {
                    if (!img.delete()) {
                        System.out.println("Failed deleting file: " + img);
                    }
                }
                ImageIO.write(image, "png", new File(filename));
                diffuseMaps.put(new Image(new File(filename).toURI().toString()));
            } catch (IOException | InterruptedException ex) {
                System.out.println("Error: " + ex);
            }
        }
    }

    private class ToriImageTask extends Task<Void> {

        private final BlockingQueue<Image> diffuseMaps;

        ToriImageTask(BlockingQueue<Image> diffuseMaps) {
            this.diffuseMaps = diffuseMaps;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                setDiffuseMap(diffuseMaps.take());
            }
            return null;
        }

        private void setDiffuseMap(Image diffuseMap) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                int i = contTweets.get() % 5;
                SegmentedTorus torus = (SegmentedTorus) tori.getChildren().get(i);
                torus.setDiffuseMap(diffuseMap);
                latch.countDown();
            });
            latch.await();
            contTweets.getAndIncrement();
        }
    }

    private class SaveTweetsTask extends Task<Void> {

        private final BlockingQueue<Parent> tweets = new ArrayBlockingQueue<>(5);
        private final BlockingQueue<BufferedImage> bufferedImages = new ArrayBlockingQueue<>(5);
        private final BlockingQueue<Image> diffuseMaps = new ArrayBlockingQueue<>(5);
        private final ExecutorService tweetsCreationExecutor = createExecutor("CreateTweets");
        private final ExecutorService tweetsSnapshotExecutor = createExecutor("TakeSnapshots");
        private final ExecutorService imagesExportExecutor = createExecutor("ExportImages");
        private final ExecutorService toriImagesExecutor = createExecutor("ToriImages");
        private final TweetsCreationTask tweetsCreationTask;
        private final TweetsSnapshotTask tweetsSnapshotTask;
        private final PngsExportTask imagesExportTask;
        private final ToriImageTask toriImagesTask;

        SaveTweetsTask(final Tweeter tweeter, final String textSearch) {
            tweetsCreationTask = new TweetsCreationTask(tweeter, textSearch, tweets);
            tweetsSnapshotTask = new TweetsSnapshotTask(tweets, bufferedImages);
            imagesExportTask = new PngsExportTask(bufferedImages, diffuseMaps);
            toriImagesTask = new ToriImageTask(diffuseMaps);

            setOnCancelled(e -> {
                tweetsCreationTask.cancel();
                tweetsSnapshotTask.cancel();
                imagesExportTask.cancel();
                toriImagesTask.cancel();
            });
        }

        @Override
        protected Void call() throws Exception {
            tweetsCreationExecutor.execute(tweetsCreationTask);
            tweetsSnapshotExecutor.execute(tweetsSnapshotTask);
            imagesExportExecutor.execute(imagesExportTask);
            toriImagesExecutor.execute(toriImagesTask);

            tweetsCreationExecutor.shutdown();
            tweetsSnapshotExecutor.shutdown();
            imagesExportExecutor.shutdown();
            toriImagesExecutor.shutdown();

            try {
                toriImagesExecutor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
            }

            return null;
        }
    }

    private String getTweetFilePath(int tweetNumber) {
        return new File(WORKING_DIR, TWEET_FILE_PREFIX + tweetNumber + ".png").getPath();
    }

    private ExecutorService createExecutor(final String name) {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setName(name);
            t.setDaemon(true);
            return t;
        };
        return Executors.newSingleThreadExecutor(factory);
    }
}
