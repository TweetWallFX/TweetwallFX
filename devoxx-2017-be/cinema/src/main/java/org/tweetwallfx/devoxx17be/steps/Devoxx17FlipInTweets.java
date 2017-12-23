/*
 * The MIT License
 *
 * Copyright 2017 TweetWallFX
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
package org.tweetwallfx.devoxx17be.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.devoxx17be.animations.FlipInXTransition;
import org.tweetwallfx.devoxx2017be.dataprovider.TweetStreamDataProvider;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * Devox 2017 TweetStream Flip In Animation Step
 *
 * @author Sven Reimers
 */
public class Devoxx17FlipInTweets implements Step {

    protected Devoxx17FlipInTweets() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        double[] spacing = new double[]{150, 20, 20, 20, 20, 10};
        double[] maxWidth = new double[]{400, 400, 380, 320, 290, 270};

        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final TweetStreamDataProvider dataProvider = context.getDataProvider(TweetStreamDataProvider.class);

        VBox tweetList = getOrCreateTweetList(wordleSkin);

        List<Transition> transitions = new ArrayList<>();

        dataProvider.getLatestImage().ifPresent(image -> {
            ImageView view = new ImageView(image);
            view.setPreserveRatio(true);
            view.setFitHeight(140);
            view.setFitWidth(259);
            view.layoutXProperty().bind(Bindings.add(Bindings.multiply(1490 / 1920.0, wordleSkin.getSkinnable().widthProperty()),
                    Bindings.multiply(Math.sin(Math.toRadians(tweetList.getRotate())) * 0.5, tweetList.widthProperty())));
            view.layoutYProperty().bind(Bindings.add(Bindings.multiply(405 / 1280.0, wordleSkin.getSkinnable().heightProperty()),
                    Bindings.multiply(Math.sin(Math.toRadians(tweetList.getRotate())) * 0.5, tweetList.heightProperty())));
            view.setRotate(-18);
            view.setId("tweetImage");
            view.setOpacity(0);
            wordleSkin.getPane().getChildren().add(view);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), view);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setDelay(Duration.seconds(0.2));
            transitions.add(fadeTransition);
        });
        tweetList.layoutXProperty().bind(Bindings.add(Bindings.multiply(1330.0 / 1920.0, wordleSkin.getSkinnable().widthProperty()),
                Bindings.multiply(Math.sin(Math.toRadians(tweetList.getRotate())) * 0.5, tweetList.widthProperty())));
        tweetList.layoutYProperty().bind(Bindings.add(Bindings.multiply(330.0 / 1280.0, wordleSkin.getSkinnable().heightProperty()),
                Bindings.multiply(Math.sin(Math.toRadians(tweetList.getRotate())) * 0.5, tweetList.heightProperty())));
        tweetList.setRotate(-18);

        List<Tweet> tweets = dataProvider.getTweets();
        for (int i = 0; i < Math.min(tweets.size(), 4); i++) {
            HBox tweet = createSingleTweetDisplay(tweets.get(i), wordleSkin, maxWidth[i]);
            tweet.setMaxWidth(maxWidth[i] + 64 + 10);
            tweet.getStyleClass().add("tweetDisplay");
            transitions.add(new FlipInXTransition(tweet));
            tweetList.getChildren().add(tweet);
            VBox.setMargin(tweet, new Insets(0, 0, spacing[i], 0));
//            if (i < 5 && i != 1) {
//                Pane pane = new Pane();
//                pane.getChildren().add(new Line(0, 0, maxWidth[i], 0));
//                pane.setPadding(new Insets(spacing[i] / 2., 0, spacing[i] / 2., 0));
//                tweetList.getChildren().add(pane);
//            }
        }
        ParallelTransition flipIns = new ParallelTransition();
        flipIns.getChildren().addAll(transitions);
        flipIns.setOnFinished(e -> context.proceed());

        flipIns.play();
    }

    private VBox getOrCreateTweetList(final WordleSkin wordleSkin) {
        VBox vbox = (VBox) wordleSkin.getNode().lookup("#tweetList");
        if (null == vbox) {
            vbox = new VBox();
            vbox.setId("tweetList");
            wordleSkin.getPane().getChildren().add(vbox);
        }
        return vbox;
    }

    private HBox createSingleTweetDisplay(final Tweet displayTweet, final WordleSkin wordleSkin, final double maxWidth) {
        String textWithoutMediaUrls = displayTweet.getDisplayEnhancedText();
        Text text = new Text(textWithoutMediaUrls.replaceAll("[\n\r]", "|"));
        text.setCache(true);
        text.setCacheHint(CacheHint.SPEED);
        text.getStyleClass().add("tweetText");
        Image profileImage = wordleSkin.getProfileImageCache().get(displayTweet.getUser().getBiggerProfileImageUrl());
        ImageView profileImageView = new ImageView(profileImage);
        profileImageView.setSmooth(true);
        profileImageView.setCacheHint(CacheHint.QUALITY);
        TextFlow flow = new TextFlow(text);
        flow.getStyleClass().add("tweetFlow");
        flow.maxWidthProperty().set(maxWidth);
        flow.maxHeightProperty().set(70);
        flow.minHeightProperty().set(70);
        flow.setCache(true);
        flow.setCacheHint(CacheHint.SPEED);
        Text name = new Text(displayTweet.getUser().getName());
        name.getStyleClass().add("tweetUsername");
        name.setCache(true);
        name.setCacheHint(CacheHint.SPEED);
        HBox tweet = new HBox(profileImageView, new VBox(name, flow));
        tweet.setCacheHint(CacheHint.QUALITY);
        tweet.setSpacing(10);
        return tweet;
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return java.time.Duration.ofSeconds(15);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link Devoxx17FlipInTweets}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public Devoxx17FlipInTweets create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new Devoxx17FlipInTweets();
        }

        @Override
        public Class<Devoxx17FlipInTweets> getStepClass() {
            return Devoxx17FlipInTweets.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(TweetStreamDataProvider.class);
        }
    }
}
