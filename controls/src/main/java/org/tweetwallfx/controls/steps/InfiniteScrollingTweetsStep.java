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
package org.tweetwallfx.controls.steps;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.util.TimeFormatter;
import org.tweetwallfx.emoji.control.EmojiFlow;
import org.tweetwallfx.stepengine.api.Controllable;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.stepengine.dataproviders.PhotoImageMediaEntryDataProvider;
import org.tweetwallfx.stepengine.dataproviders.TweetStreamDataProvider;
import org.tweetwallfx.stepengine.dataproviders.TweetUserProfileImageDataProvider;
import org.tweetwallfx.transitions.LocationTransition;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

/**
 * Infinite TweetStream Animation Step
 */
public class InfiniteScrollingTweetsStep implements Step, Controllable {

    private static final Logger LOG = LoggerFactory.getLogger(InfiniteScrollingTweetsStep.class);

    private final Config config;

    private final AtomicReference<List<Tweet>> tweetsRef = new AtomicReference<>(List.of());

    private TweetUserProfileImageDataProvider tweetUserProfileImageDataProvider;
    private PhotoImageMediaEntryDataProvider photoImageMediaEntryDataProvider;
    private TweetStreamDataProvider tweetStreamDataProvider;
    private WordleSkin wordleSkin;
    private CountDownLatch shutdownCountdown;
    private final AtomicInteger next = new AtomicInteger(0);

    private volatile boolean isTerminated = false;

    /**
     * Creates a new instance.
     *
     * @param config the configuration of this step
     */
    protected InfiniteScrollingTweetsStep(Config config) {
        this.config = config;
    }

    @Override
    public void doStep(final MachineContext context) {
        isTerminated = false;
        wordleSkin = (WordleSkin) context.get("WordleSkin");
        context.put(config.stepIdentifier, this);
        tweetUserProfileImageDataProvider = context.getDataProvider(TweetUserProfileImageDataProvider.class);
        photoImageMediaEntryDataProvider = context.getDataProvider(PhotoImageMediaEntryDataProvider.class);

        tweetStreamDataProvider = context.getDataProvider(TweetStreamDataProvider.class);

        updateTweetList();
        for (int i = 0; i < config.columns; i++) {
            var pane = createInfinitePane(wordleSkin, "infiniteStream." + i);

            pane.setLayoutX(config.layoutX + i * (config.tweetWidth + config.profileImageSize + 10 + 5 + config.columnGap));
            pane.setLayoutY(config.layoutY);
            pane.setMinWidth(config.tweetWidth + config.profileImageSize + 10 + 5);
            pane.setMinHeight(config.height);
            pane.setMaxWidth(config.tweetWidth + config.profileImageSize + 10 + 5);
            pane.setMaxHeight(config.height);
            pane.setPrefWidth(config.tweetWidth + config.profileImageSize + 10 + 5);
            pane.setPrefHeight(config.height);
            pane.setClip(new Rectangle(config.tweetWidth + config.profileImageSize + 10 + 10, config.height));

            initializePane(pane);
        }
        context.proceed();
    }

    private void updateTweetList() {
        tweetsRef.set(new CopyOnWriteArrayList<>(tweetStreamDataProvider.getTweets()));
        LOG.info("Updated tweet list to be streamed. Now contains {}", tweetsRef.get().size());
        next.set(0);
    }

    /**
     * Initializes the given pane by adding the first tweet node.
     *
     * @param pane the pane to initialize
     */
    void initializePane(Pane pane) {
        addNode(createNode(), pane, config.vOffset);
    }

    /**
     * Adds the given node to the pane and recursively fills the pane up to its
     * height.
     *
     * @param node the node to add
     * @param pane the pane to add the node to
     * @param lastLayoutY the layout y position below which the node is added
     */
    void addNode(Node node, Pane pane, double lastLayoutY) {
        if (lastLayoutY < config.height) {
            node.setOpacity(0);
            pane.getChildren().add(node);
            node.setLayoutX(0);
            pane.applyCss();
            pane.layout();
            node.setLayoutY(lastLayoutY + config.tweetGap);
            addNode(createNode(), pane, node.getLayoutY() + node.getLayoutBounds().getHeight());
        } else {
            for (Node nodeToScroll : pane.getChildren().subList(0, pane.getChildren().size() - 1)) {
                double pixelToTravel = nodeToScroll.getLayoutBounds().getHeight() + nodeToScroll.getLayoutY();
                double duration = pixelToTravel / config.speed;

                var locationTransition = new LocationTransition(Duration.seconds(duration), nodeToScroll,
                        nodeToScroll.getLayoutX(), nodeToScroll.getLayoutY(),
                        nodeToScroll.getLayoutX(), -nodeToScroll.getLayoutBounds().getHeight());
                locationTransition.setInterpolator(Interpolator.LINEAR);
                locationTransition.setOnFinished(evt -> {
                    pane.getChildren().remove(node);
                });
                var fadeIn = new FadeTransition(Duration.millis(1500), nodeToScroll);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                locationTransition.play();
            }

            Node lastNode = pane.getChildren().get(pane.getChildren().size() - 1);
            double pixelToTravel = lastNode.getLayoutY() - (config.height - lastNode.getLayoutBounds().getHeight());
            double duration = pixelToTravel / config.speed;
            var locationTransition = new LocationTransition(Duration.seconds(duration), lastNode,
                    lastNode.getLayoutX(), lastNode.getLayoutY(),
                    lastNode.getLayoutX(), config.height - lastNode.getLayoutBounds().getHeight());
            locationTransition.setInterpolator(Interpolator.LINEAR);
            locationTransition.setOnFinished(evt -> {
                scrollOut(lastNode, pane).play();
                scrollIn(createNode(), pane).play();
            });
            var fadeIn = new FadeTransition(Duration.millis(1500), lastNode);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            locationTransition.play();
        }
    }

    /**
     * Scrolls a new node into the given pane.
     *
     * @param pane the pane to scroll into
     */
    void scrollInfinite(Pane pane) {
        scrollIn(createNode(), pane).play();
    }

    private Tweet getNextTweet() {
        var tweets = tweetsRef.get();
        var listPointer = next.get();
        if (listPointer >= config.numberOfTweets - 1 || listPointer >= tweets.size()) {
            next.set(0);
            updateTweetList();
        }
        var tweet = tweets.get(next.getAndAdd(1));
        return tweet;
    }

    private Node createNode() {
        return createSingleTweetDisplay(getNextTweet());
    }

    /**
     * {@return the transition scrolling the given node into the pane}
     *
     * @param node the node to scroll in
     * @param pane the pane to scroll into
     */
    Transition scrollIn(final Node node, Pane pane) {
        node.setLayoutY(config.height + config.tweetGap);
        node.setLayoutX(0);

        pane.getChildren().add(node);

        // required to know the correct height of node in next step
        pane.applyCss();
        pane.layout();

        double pixelToTravel = node.getLayoutBounds().getHeight() + config.tweetGap;
        double duration = pixelToTravel / config.speed;

        var locationTransition = new LocationTransition(Duration.seconds(duration), node,
                node.getLayoutX(), pane.getHeight() + config.tweetGap,
                node.getLayoutX(), pane.getHeight() - node.getLayoutBounds().getHeight());
        locationTransition.setInterpolator(Interpolator.LINEAR);
        locationTransition.setOnFinished(evt -> {
            scrollOut(node, pane).play();
            if (!isTerminated) {
                scrollIn(createNode(), pane).play();
            }
        });
        return locationTransition;
    }

    /**
     * {@return the transition scrolling the given node out of the pane}
     *
     * @param node the node to scroll out
     * @param pane the pane to scroll out of
     */
    Transition scrollOut(Node node, Pane pane) {
        double pixelToTravel = pane.getHeight();
        double duration = pixelToTravel / config.speed;

        var locationTransition = new LocationTransition(Duration.seconds(duration), node,
                node.getLayoutX(), node.getLayoutY(),
                node.getLayoutX(), -node.getLayoutBounds().getHeight());
        locationTransition.setInterpolator(Interpolator.LINEAR);
        locationTransition.setOnFinished(evt -> {
            pane.getChildren().remove(node);
        });
        return locationTransition;
    }

    private Pane createInfinitePane(final WordleSkin wordleSkin, String paneId) {
        var pane = new Pane();
        pane.setId(paneId);
        wordleSkin.getPane().getChildren().add(pane);
        wordleSkin.getPane().applyCss();
        wordleSkin.getPane().layout();
        return pane;
    }

    private Pane createSingleTweetDisplay(
            final Tweet displayTweet) {

        String textWithoutMediaUrls = displayTweet.getDisplayEnhancedText();
        String text = textWithoutMediaUrls.replaceAll("[\n\r]+", config.respectLineFeeds ? "\n" : "|");
        Node profileImageView = createProfileImageView(displayTweet);

        EmojiFlow tweetFlow = new EmojiFlow();
        tweetFlow.getStyleClass().add("tweetFlow");
        tweetFlow.setText(text);
        tweetFlow.setCache(config.tweetFlowNode.isCacheEnabled);
        tweetFlow.setCacheHint(config.tweetFlowNode.cacheHint);
        tweetFlow.setMinWidth(config.tweetWidth);
        tweetFlow.setMaxWidth(config.tweetWidth);
        tweetFlow.setPrefWidth(config.tweetWidth);

        String name = displayTweet.getUser().getName();
        EmojiFlow nameFlow = new EmojiFlow();
        nameFlow.getStyleClass().add("tweetUsername");
        nameFlow.setText(name);
        nameFlow.setCache(config.tweetFlowNode.isCacheEnabled);
        nameFlow.setCacheHint(config.tweetFlowNode.cacheHint);

        Instant createdAt = displayTweet.getCreatedAt().toInstant(ZoneOffset.UTC);
        Label naturalTime = new Label(TimeFormatter.formatNatural(createdAt, Locale.ENGLISH));
        naturalTime.getStyleClass().add("tweetTime");
        naturalTime.setMinWidth(config.tweetWidth);
        naturalTime.setMaxWidth(config.tweetWidth);
        naturalTime.setPrefWidth(config.tweetWidth);
        naturalTime.applyCss();
        var vbox = new VBox(nameFlow, naturalTime, tweetFlow);
        vbox.applyCss();
        vbox.layout();
        HBox tweet = new HBox(profileImageView, vbox);
        tweet.setMaxWidth(config.tweetWidth + config.profileImageSize + 10);
        tweet.setPrefWidth(config.tweetWidth + config.profileImageSize + 10);

        VBox.setMargin(nameFlow, new Insets(5, 0, 0, 0));
        VBox.setMargin(naturalTime, new Insets(2, 5, 5, 0));
        tweet.setCache(config.tweetOverallNode.isCacheEnabled);
        tweet.setCacheHint(config.tweetOverallNode.cacheHint);

        Pane pane = tweet;

        final Node iv = createMediaNode(displayTweet);
        if (null != iv) {
            var box = config.mediaPosition.isTop() ? new VBox(iv, tweet) : new VBox(tweet, iv);
            box.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(iv, new Insets(5, 5, 5, 5));
            HBox.setMargin(profileImageView, new Insets(5, 5, 5, 5));
            box.getStyleClass().add("tweetDisplay");
            box.setCache(config.tweetOverallNode.isCacheEnabled);
            box.setCacheHint(config.tweetOverallNode.cacheHint);
            pane = box;
        } else {
            tweet.getStyleClass().add("tweetDisplay");
            HBox.setMargin(profileImageView, new Insets(5, 5, 5, 5));
            HBox.setMargin(vbox, new Insets(0, 5, 5, 0));
        }

        return pane;
    }

    private Node createProfileImageView(Tweet displayTweet) {
        Image profileImage = tweetUserProfileImageDataProvider.getImageBig(displayTweet.getUser());
        ImageView profileImageView = new ImageView(profileImage);
        profileImageView.setSmooth(true);
        profileImageView.setCache(config.speakerImageNode.isCacheEnabled);
        profileImageView.setCacheHint(config.speakerImageNode.cacheHint);
        if (config.circularProfileImage) {
            final Circle clip = new Circle(config.profileImageSize / 2f, config.profileImageSize / 2f, config.profileImageSize / 2f);
            profileImageView.setClip(clip);
        }
        return profileImageView;
    }

    private Node createMediaNode(Tweet displayTweet) {
        return displayTweet.getMediaEntries().stream()
                .filter(e -> e.getType().equals(MediaTweetEntryType.photo))
                .findFirst()
                .map(entry -> {
                    var image = photoImageMediaEntryDataProvider.getImage(entry);
                    ImageView iv = new ImageView(image);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(config.tweetWidth + config.profileImageSize + 10);
                    Rectangle rectangle = new Rectangle(0, 0, iv.getBoundsInLocal().getWidth(), iv.getBoundsInLocal().getHeight());
                    rectangle.setArcHeight(20);
                    rectangle.setArcWidth(20);
                    iv.setClip(rectangle);
                    iv.setCache(config.tweetImageNode.isCacheEnabled);
                    iv.setCacheHint(config.tweetImageNode.cacheHint);
                    return iv;
                })
                .orElse(null);
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return java.time.Duration.ofMillis(config.stepDuration);
    }

    @Override
    public void shutdown() {
        shutdownCountdown = new CountDownLatch(2);
        this.isTerminated = true;
        Platform.runLater(() -> {
            wordleSkin.getPane().getChildren()
                    .stream()
                    .filter(p -> p instanceof Pane)
                    .map(p -> (Pane) p)
                    .filter(p -> p.getId() != null && p.getId().startsWith("infiniteStream"))
                    .forEach(pane -> {
                        LOG.info("Shutting down {}", pane.getId());
                        for (Node nodeToFadeOut : pane.getChildren()) {
                            var fadeOut = new FadeTransition(Duration.millis(1500), nodeToFadeOut);
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setOnFinished(e -> {
                                pane.getChildren().remove(nodeToFadeOut);
                                if (pane.getChildren().isEmpty()) {
                                    wordleSkin.getPane().getChildren().remove(pane);
                                    LOG.info("Shutting down - removed {} from wordle", pane.getId());
                                    shutdownCountdown.countDown();
                                }
                            });
                            fadeOut.play();
                        }
                    });
        });
        try {
            shutdownCountdown.await();
        } catch (InterruptedException ex) {
            LOG.error("Shutdown interrupted");
        }
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link InfiniteScrollingTweetsStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        /** Creates a new instance. */
        public FactoryImpl() {
        }

        @Override
        public InfiniteScrollingTweetsStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new InfiniteScrollingTweetsStep(stepDefinition.getConfig(Config.class));
        }

        @Override
        public Class<InfiniteScrollingTweetsStep> getStepClass() {
            return InfiniteScrollingTweetsStep.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(
                    TweetStreamDataProvider.class,
                    TweetUserProfileImageDataProvider.class,
                    PhotoImageMediaEntryDataProvider.class
            );
        }
    }

    /**
     * Configuration of an {@link InfiniteScrollingTweetsStep}.
     */
    public static class Config {

        /**
         * The position of the media within a displayed tweet.
         */
        public static enum MediaPosition {
            /**
             * The media is placed above the tweet text.
             */
            TOP(true),
            /**
             * The media is placed below the tweet text.
             */
            BOTTOM(false);

            private final boolean isTop;

            /**
             * Creates a new instance.
             *
             * @param isTop whether the media is placed above the tweet text
             */
            MediaPosition(boolean isTop) {
                this.isTop = isTop;
            }

            /**
             * {@return whether the media is placed above the tweet text}
             */
            final public boolean isTop() {
                return isTop;
            }
        }

        /**
         * The duration of the step in milliseconds.
         */
        public long stepDuration = 1;
        /**
         * The x offset of the pane.
         */
        public double layoutX = 0;
        /**
         * The y offset of the pane.
         */
        public double layoutY = 0;
        /**
         * The width of a tweet.
         */
        public double tweetWidth = 600;
        /**
         * The vertical gap between tweets.
         */
        public double tweetGap = 20;
        /**
         * The height of the pane.
         */
        public double height = 700;
        /**
         * The scroll speed in pixels per second.
         */
        public double speed = 50; // pixel / s
        /**
         * The number of columns of scrolling tweets.
         */
        public int columns = 1;
        /**
         * The horizontal gap between columns.
         */
        public double columnGap = tweetGap;
        /**
         * The number of tweets to stream.
         */
        public int numberOfTweets = 25;
        /**
         * The identifier under which this step is registered in the context.
         */
        public String stepIdentifier = InfiniteScrollingTweetsStep.class.getName();
        /**
         * Whether line feeds in tweet text are respected.
         */
        public boolean respectLineFeeds = true;
        /**
         * The vertical offset of the first tweet.
         */
        public double vOffset = 0;
        /**
         * The position of the media within a displayed tweet.
         */
        public MediaPosition mediaPosition = MediaPosition.BOTTOM;
        /**
         * The size of the profile image.
         */
        public int profileImageSize = 64;
        /**
         * Whether the profile image is clipped to a circle.
         */
        public boolean circularProfileImage = true;

        /**
         * The cache configuration of the speaker name node.
         */
        public NodeCacheConfig speakerNameNode = new NodeCacheConfig();
        /**
         * The cache configuration of the speaker image node.
         */
        public NodeCacheConfig speakerImageNode = new NodeCacheConfig();
        /**
         * The cache configuration of the tweet text node.
         */
        public NodeCacheConfig tweetTextNode = new NodeCacheConfig();
        /**
         * The cache configuration of the tweet flow node.
         */
        public NodeCacheConfig tweetFlowNode = new NodeCacheConfig();
        /**
         * The cache configuration of the tweet image node.
         */
        public NodeCacheConfig tweetImageNode = new NodeCacheConfig();
        /**
         * The cache configuration of the overall tweet node.
         */
        public NodeCacheConfig tweetOverallNode = new NodeCacheConfig();

        /** Creates a new instance. */
        public Config() {
        }

        /**
         * Cache configuration of a node.
         */
        public static class NodeCacheConfig {

            /**
             * Whether caching is enabled for the node.
             */
            public boolean isCacheEnabled = true;
            /**
             * The cache hint to use for the node.
             */
            public CacheHint cacheHint = CacheHint.QUALITY;

            /** Creates a new instance. */
            public NodeCacheConfig() {
            }
        }
    }
}
