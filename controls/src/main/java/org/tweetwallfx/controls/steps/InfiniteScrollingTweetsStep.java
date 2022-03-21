/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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

import org.tweetwallfx.stepengine.api.Controllable;
import humanize.Humanize;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.stepengine.dataproviders.TweetStreamDataProvider;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.stepengine.dataproviders.PhotoImageMediaEntryDataProvider;
import org.tweetwallfx.stepengine.dataproviders.TweetUserProfileImageDataProvider;
import org.tweetwallfx.transitions.LocationTransition;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;

/**
 * Infinite TweetStream Animation Step
 *
 * @author Sven Reimers
 */
public class InfiniteScrollingTweetsStep implements Step, Controllable {

    private static final Logger LOG = LogManager.getLogger(InfiniteScrollingTweetsStep.class);

    private final Config config;

    private final AtomicReference<List<Tweet>> tweetsRef = new AtomicReference<>(Collections.emptyList());

    private TweetUserProfileImageDataProvider tweetUserProfileImageDataProvider;
    private PhotoImageMediaEntryDataProvider photoImageMediaEntryDataProvider;
    private TweetStreamDataProvider tweetStreamDataProvider;
    private WordleSkin wordleSkin;
    private CountDownLatch shutdownCountdown;
    private final AtomicInteger next = new AtomicInteger(0);

    private volatile boolean isTerminated = false;

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
        for (int i = 0; i< config.columns; i++) {
            var pane = createInfinitePane(wordleSkin, "infiniteStream." + i);

            pane.setLayoutX(config.layoutX + i * (config.tweetWidth + 64 + 10 +5 + config.columnGap));
            pane.setLayoutY(config.layoutY);
            pane.setMinWidth(config.tweetWidth + 64 + 10 +5);
            pane.setMinHeight(config.height);
            pane.setMaxWidth(config.tweetWidth + 64 + 10 + 5);
            pane.setMaxHeight(config.height);
            pane.setPrefWidth(config.tweetWidth + 64 + 10 + 5);
            pane.setPrefHeight(config.height);
            pane.setClip(new Rectangle(config.tweetWidth + 64 + 10 + 10, config.height));

            initializePane(pane);
        }
        context.proceed();
    }

    private void updateTweetList() {
        tweetsRef.set(new CopyOnWriteArrayList<>(tweetStreamDataProvider.getTweets()));
        LOG.info("Updated tweet list to be streamed. Now contains " + tweetsRef.get().size());
        next.set(0);
    }

    void initializePane(Pane pane) {
        addNode(createNode(), pane, config.vOffset);
    }

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
            for (Node nodeToScroll : pane.getChildren().subList(0, pane.getChildren().size()-1)) {
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

            Node lastNode = pane.getChildren().get(pane.getChildren().size()-1);
            double pixelToTravel = lastNode.getLayoutY() - (config.height-lastNode.getLayoutBounds().getHeight());
            double duration = pixelToTravel / config.speed;
            var locationTransition = new LocationTransition(Duration.seconds(duration), lastNode,
                        lastNode.getLayoutX(), lastNode.getLayoutY(),
                        lastNode.getLayoutX(), config.height-lastNode.getLayoutBounds().getHeight());
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
        Text text = new Text(textWithoutMediaUrls.replaceAll("[\n\r]+", config.respectLineFeeds ? "\n": "|"));
        text.setCache(config.tweetTextNode.isCacheEnabled);
        text.setCacheHint(config.tweetTextNode.cacheHint);
        text.getStyleClass().add("tweetText");
        Image profileImage = tweetUserProfileImageDataProvider.getImageBig(displayTweet.getUser());
        ImageView profileImageView = new ImageView(profileImage);
        profileImageView.setSmooth(true);
        profileImageView.setCache(config.speakerImageNode.isCacheEnabled);
        profileImageView.setCacheHint(config.speakerImageNode.cacheHint);
        TextFlow flow = new TextFlow(text);
        flow.getStyleClass().add("tweetFlow");
        flow.setCache(config.tweetFlowNode.isCacheEnabled);
        flow.setCacheHint(config.tweetFlowNode.cacheHint);
        flow.setMinWidth(config.tweetWidth);
        flow.setMaxWidth(config.tweetWidth);
        flow.setPrefWidth(config.tweetWidth);
        Text name = new Text(displayTweet.getUser().getName());
        name.getStyleClass().add("tweetUsername");
        name.setCache(config.speakerNameNode.isCacheEnabled);
        name.setCacheHint(config.speakerNameNode.cacheHint);
        TextFlow nameFlow = new TextFlow(name);
        nameFlow.setCache(config.tweetFlowNode.isCacheEnabled);
        nameFlow.setCacheHint(config.tweetFlowNode.cacheHint);
        Label naturalTime = new Label(Humanize.naturalTime(displayTweet.getCreatedAt(), Locale.ENGLISH));
        naturalTime.getStyleClass().add("tweetTime");
        var vbox = new VBox(nameFlow, naturalTime, flow);
        vbox.applyCss();
        vbox.layout();
        HBox tweet = new HBox(profileImageView, vbox);
        tweet.setMaxWidth(config.tweetWidth + 64 + 10);
        tweet.setPrefWidth(config.tweetWidth + 64 + 10);

        VBox.setMargin(nameFlow, new Insets(0,0,5,0));
        tweet.setCache(config.tweetOverallNode.isCacheEnabled);
        tweet.setCacheHint(config.tweetOverallNode.cacheHint);

        Pane pane = tweet;

        Optional<MediaTweetEntry> maybeImageEntry = Arrays.stream(displayTweet.getMediaEntries()).filter(e -> e.getType().equals(MediaTweetEntryType.photo)).findFirst();
        if(maybeImageEntry.isPresent()) {
            var image = photoImageMediaEntryDataProvider.getImage(maybeImageEntry.get());
            var iv = new ImageView(image);
            iv.setPreserveRatio(true);
            iv.setFitWidth(config.tweetWidth + 64 + 5);
            iv.setCache(config.tweetImageNode.isCacheEnabled);
            iv.setCacheHint(config.tweetImageNode.cacheHint);
            var box = new VBox(iv, tweet);
            box.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(iv, new Insets(5,5,5,5));
            HBox.setMargin(profileImageView, new Insets(0,5,5,5));
            HBox.setMargin(vbox, new Insets(0,5,5,0));
            box.getStyleClass().add("tweetDisplay");
            box.setCache(config.tweetOverallNode.isCacheEnabled);
            box.setCacheHint(config.tweetOverallNode.cacheHint);
            pane = box;
        } else {
            tweet.getStyleClass().add("tweetDisplay");
            HBox.setMargin(profileImageView, new Insets(5,5,5,5));
            HBox.setMargin(vbox, new Insets(5,5,5,0));
        }

        return pane;
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
            wordleSkin.getPane().getChildren().stream().map(p -> (Pane) p).filter(p -> p.getId() != null && p.getId().startsWith("infiniteStream")).forEach(pane -> {
                LOG.info("Shutting down " + pane.getId());
                for (Node nodeToFadeOut : pane.getChildren()) {
                    var fadeOut = new FadeTransition(Duration.millis(1500), nodeToFadeOut);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(e -> {
                        pane.getChildren().remove(nodeToFadeOut);
                        if (pane.getChildren().isEmpty()) {
                            wordleSkin.getPane().getChildren().remove(pane);
                            LOG.info("Shutting down - removed " + pane.getId() + " from wordle");
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

    public static class Config {

        public long stepDuration = 1;
        public double layoutX = 0;
        public double layoutY = 0;
        public double tweetWidth = 600;
        public double tweetGap = 20;
        public double height = 700;
        public double speed = 50; // pixel / s
        public int columns = 1;
        public double columnGap = tweetGap;
        public int numberOfTweets = 25;
        public String stepIdentifier = InfiniteScrollingTweetsStep.class.getName();
        public boolean respectLineFeeds = true;
        public double vOffset = 0;

        public NodeCacheConfig speakerNameNode = new NodeCacheConfig();
        public NodeCacheConfig speakerImageNode = new NodeCacheConfig();
        public NodeCacheConfig tweetTextNode = new NodeCacheConfig();
        public NodeCacheConfig tweetFlowNode = new NodeCacheConfig();
        public NodeCacheConfig tweetImageNode = new NodeCacheConfig();
        public NodeCacheConfig tweetOverallNode = new NodeCacheConfig();

        public static class NodeCacheConfig {
            public boolean isCacheEnabled = true;
            public CacheHint cacheHint = CacheHint.QUALITY;
        }
    }
}
