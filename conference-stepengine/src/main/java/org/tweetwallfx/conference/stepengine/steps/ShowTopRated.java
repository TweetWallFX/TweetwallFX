/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.steps;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javafx.animation.ParallelTransition;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.conference.stepengine.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TopTalksTodayDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TopTalksWeekDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TrackImageDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.VotedTalk;
import org.tweetwallfx.emoji.control.EmojiFlow;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.AbstractConfig;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.transitions.FlipInXTransition;

/**
 * Show Top Rated Talks (Flip In) Animation Step
 */
public class ShowTopRated implements Step {

    private final Function<MachineContext, Collection<VotedTalk>> votedTalksConverter;
    private final Config config;
    private final String lookupId;

    private ShowTopRated(
            final Function<MachineContext, Collection<VotedTalk>> votedTalksConverter,
            final Config config,
            final String lookupId) {
        this.votedTalksConverter = Objects.requireNonNull(
                votedTalksConverter,
                "votedTalksConverter must not be null");
        this.config = Objects.requireNonNull(
                config,
                "config must not be null");
        this.lookupId = Objects.requireNonNull(
                lookupId,
                "lookupId must not be null");
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");

        List<FlipInXTransition> transitions = new ArrayList<>();
        if (null == wordleSkin.getNode().lookup("#topRatedeNode")) {
            Pane topRatedNode = new Pane();
            topRatedNode.getStyleClass().add("votedTalk");
            topRatedNode.setId("topRatedeNode");
            topRatedNode.setOpacity(0);

            var title = new Label(config.getTopVotedType().getTitle());

            title.setPrefWidth(config.width);
            title.getStyleClass().add("title");
            title.setPrefHeight(config.titleHeight);
            title.setAlignment(Pos.CENTER);

            topRatedNode.getChildren().add(title);

            transitions.add(new FlipInXTransition(topRatedNode));
            topRatedNode.setLayoutX(config.layoutX);
            topRatedNode.setLayoutY(config.layoutY);
            topRatedNode.setMinWidth(config.width);
            topRatedNode.setMaxWidth(config.width);
            topRatedNode.setPrefWidth(config.width);
            topRatedNode.setCacheHint(CacheHint.SPEED);
            topRatedNode.setCache(true);

            wordleSkin.getPane().getChildren().add(topRatedNode);

            Iterator<VotedTalk> iterator = votedTalksConverter.apply(context).iterator();
            int row = 0;
            while (iterator.hasNext()) {
                var talkPane = createTalkNode(context, iterator.next());
                double talkWidth = config.width;
                talkPane.setMinWidth(talkWidth);
                talkPane.setMaxWidth(talkWidth);
                talkPane.setPrefWidth(talkWidth);
                talkPane.setMinHeight(config.talkHeight);
                talkPane.setMaxHeight(config.talkHeight);
                talkPane.setPrefHeight(config.talkHeight);
                talkPane.setLayoutY(config.titleHeight + config.talkVGap + (config.talkHeight + config.talkVGap) * row);
                topRatedNode.getChildren().add(talkPane);
                row++;
            }
        }
        ParallelTransition flipIns = new ParallelTransition();
        flipIns.getChildren().addAll(transitions);
        flipIns.setOnFinished(e -> context.proceed());

        flipIns.play();
    }

    private Pane createTalkNode(final MachineContext context, final VotedTalk votedTalk) {
        var ratingAverageScore = new Label(String.format("%.2f",votedTalk.ratingAverageScore));
        ratingAverageScore.getStyleClass().add("ratingAverageScore");

        var ratingTotalVotes = new Label("" + votedTalk.ratingTotalVotes);
        ratingTotalVotes.getStyleClass().add("ratingTotalVotes");

        var topLeftVBox = new VBox(4, ratingAverageScore, ratingTotalVotes);
        Pane topLeft = topLeftVBox;

        var speakerNames = new VBox();
        speakerNames.getStyleClass().add("speakerNames");

        votedTalk.speakers.stream().forEach(speaker -> {
            var speakerName = new Label(speaker.getFullName());
            speakerName.setTextAlignment(TextAlignment.RIGHT);
            speakerName.getStyleClass().add("speakerName");
            speakerNames.getChildren().add(speakerName);
            if (config.showCompanyName) {
                speaker.getCompany().ifPresent(company -> {
                    var companyName = new Label("(" + company + ")");
                    companyName.setTextAlignment(TextAlignment.RIGHT);
                    companyName.getStyleClass().add("companyName");
                    speakerNames.getChildren().add(companyName);
                });
            }
        });

        if (config.showAvatar) {
            var speakerImageProvider = context.getDataProvider(SpeakerImageProvider.class);
            if (config.compressedAvatars && votedTalk.speakers.size() >= config.compressedAvatarsLimit) {
                var speakerImages = new Pane();
                var images = votedTalk.speakers.stream()
                        .map(speaker -> createSpeakerImage(speakerImageProvider, speaker))
                        .toList();
                for (int i = 0; i < images.size(); i++) {
                    var image = images.get(i);
                    image.setLayoutX(i * (config.avatarSize * 3 / 4d + 2));
                    image.setLayoutY(i % 2 * config.avatarSize / 2d + 2);
                    speakerImages.getChildren().add(image);
                }
                topLeft = new HBox(4, topLeftVBox, speakerImages);
            } else {
                var speakerImages = new HBox(config.avatarSpacing, votedTalk.speakers.stream()
                        .map(speaker -> createSpeakerImage(speakerImageProvider, speaker))
                        .toArray(Node[]::new)
                );
                topLeft = new HBox(4, topLeftVBox, speakerImages);
            }
        }

        var title = new EmojiFlow();
        title.setText(votedTalk.talk.getName());
        title.setEmojiFitWidth(15);
        title.setEmojiFitHeight(15);
        title.getStyleClass().add("title");
        title.setMaxHeight(Double.MAX_VALUE);

        Node trackImageView;
        if (config.showTrackAvatar && null != votedTalk.trackImageUrl) {
            var trackImage = context.getDataProvider(TrackImageDataProvider.class).getImage(votedTalk.trackImageUrl);
            trackImageView = new ImageView(trackImage);
        } else {
            trackImageView = null;
        }

        var bpSessionTopPane = new BorderPane();
        bpSessionTopPane.getStyleClass().add("sessionTopPane");
        bpSessionTopPane.setCenter(speakerNames);
        bpSessionTopPane.setLeft(topLeft);
        BorderPane.setAlignment(speakerNames, Pos.TOP_RIGHT);

        var bpTitle = new BorderPane();
        bpTitle.getStyleClass().add("titlePane");
        bpTitle.setBottom(title);

        var bpSessionBottomPane = new BorderPane();
        bpSessionBottomPane.getStyleClass().add("sessionBottomPane");
        bpSessionBottomPane.setRight(trackImageView);
        if (config.showTags) {
            var tags = new FlowPane();
            tags.getStyleClass().add("tags");
            votedTalk.tags.stream().forEach(tag -> {
                var tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("tagLabel");
                tags.getChildren().add(tagLabel);
            });

            var bpTags = new BorderPane();
            bpTags.getStyleClass().add("tagPane");
            BorderPane.setAlignment(tags, Pos.BOTTOM_LEFT);
            bpTags.setLeft(tags);

            VBox bpCenter = new VBox(bpTitle, bpTags);
            bpCenter.getStyleClass().add("centerFlow");

            bpSessionBottomPane.setCenter(bpCenter);
        } else {
            bpSessionBottomPane.setRight(trackImageView);
            bpSessionBottomPane.setCenter(bpTitle);
        }

        var bpSessionPane = new BorderPane();
        bpSessionPane.getStyleClass().add("scheduleSession");
        bpSessionPane.setTop(bpSessionTopPane);
        bpSessionPane.setBottom(bpSessionBottomPane);

        if (null != trackImageView) {
            BorderPane.setAlignment(trackImageView, Pos.BOTTOM_RIGHT);
        }

        return bpSessionPane;
    }

    private Node createSpeakerImage(SpeakerImageProvider speakerImageProvider, Speaker speaker) {
        var image = speakerImageProvider.getSpeakerImage(speaker);
        var speakerImage = new ImageView(image);
        speakerImage.getStyleClass().add("speakerImage");
        speakerImage.setPreserveRatio(true);
        if (image.getWidth() > image.getHeight()) {
            speakerImage.setFitHeight(config.avatarSize);
        } else {
            speakerImage.setFitWidth(config.avatarSize);
        }

        // avatar image clipping
        if (config.circularAvatar) {
            Circle circle = new Circle(config.avatarSize / 2f, config.avatarSize / 2f, config.avatarSize / 2f);
            speakerImage.setClip(circle);
        } else {
            Rectangle clip = new Rectangle(config.avatarSize, config.avatarSize);
            clip.setArcWidth(config.avatarArcSize);
            clip.setArcHeight(config.avatarArcSize);
            speakerImage.setClip(clip);
        }
        return speakerImage;
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return votedTalksConverter.apply(context).isEmpty()
                || null != ((WordleSkin) context.get("WordleSkin")).getNode().lookup(lookupId);
    }

    @Override
    public Duration preferredStepDuration(MachineContext context) {
        return java.time.Duration.ofMillis(config.getStepDuration());
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link ShowTopRated}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public ShowTopRated create(final StepEngineSettings.StepDefinition stepDefinition) {
            final TopVotedType topVotedType = stepDefinition.getConfig(Config.class).getTopVotedType();

            switch (topVotedType) {
                case TODAY:
                    return new ShowTopRated(
                            mc -> mc.getDataProvider(TopTalksTodayDataProvider.class).getFilteredSessionData(),
                            stepDefinition.getConfig(Config.class),
                            "#topRatedToday");

                case WEEK:
                    return new ShowTopRated(
                            mc -> mc.getDataProvider(TopTalksWeekDataProvider.class).getFilteredSessionData(),
                            stepDefinition.getConfig(Config.class),
                            "#topRatedWeek");

                default:
                    throw new IllegalArgumentException("TopVotedType " + topVotedType + " is not supported");
            }
        }

        @Override
        public Class<ShowTopRated> getStepClass() {
            return ShowTopRated.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepDefinition) {
            final ShowTopRated.TopVotedType topVotedType = stepDefinition.getConfig(ShowTopRated.Config.class).getTopVotedType();

            switch (topVotedType) {
                case TODAY:
                    return Arrays.asList(
                            TopTalksTodayDataProvider.class,
                            SpeakerImageProvider.class,
                            TrackImageDataProvider.class);

                case WEEK:
                    return Arrays.asList(
                            TopTalksWeekDataProvider.class,
                            SpeakerImageProvider.class,
                            TrackImageDataProvider.class);

                default:
                    throw new IllegalArgumentException("TopVotedType " + topVotedType + " is not supported");
            }
        }
    }

    public static class Config extends AbstractConfig {

        private TopVotedType topVotedType = null;
        public int avatarSize = 64;
        public int avatarArcSize = 20;
        public int avatarSpacing = 4;
        public double layoutX = 0;
        public double layoutY = 0;
        public double width = 800;
        public double titleHeight = 60;
        public double talkVGap = 10;
        public double talkHeight = 200;
        public boolean circularAvatar = true;
        public boolean showCompanyName = false;
        public boolean showAvatar = true;
        public boolean showTrackAvatar = true;
        public boolean compressedAvatars = true;
        public int compressedAvatarsLimit = 4;
        public boolean showTags = false;

        /**
         * Provides the type of the Top Voted display to flip out.
         *
         * @return the type to flip out
         */
        public TopVotedType getTopVotedType() {
            return Objects.requireNonNull(topVotedType, "topVotedType must not be null");
        }

        /**
         * Sets the type of the Top Voted display to flip out.
         *
         * @param topVotedType the type to flip out
         */
        public void setTopVotedType(final TopVotedType topVotedType) {
            this.topVotedType = Objects.requireNonNull(topVotedType, "topVotedType must not be null");
        }
    }

    /**
     * The type of top voted display to show.
     */
    public static enum TopVotedType {

        /**
         * The Top Voted Session of Today.
         */
        TODAY("Top Rated Talks of the Day"),
        /**
         * The Top Voted Session of the Week.
         */
        WEEK("Top Rated Talks of the Week");

        private final String title;

        TopVotedType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}
