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
package org.tweetwallfx.conference.stepengine.steps;

import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.conference.api.Speaker;
import org.tweetwallfx.conference.stepengine.dataprovider.ScheduleDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.SessionData;
import org.tweetwallfx.conference.stepengine.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TrackImageDataProvider;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.emoji.control.EmojiFlow;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.AbstractConfig;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
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
import javafx.util.Duration;

/**
 * Show Schedule (Flip In) Animation Step
 */
public class ShowSchedule implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowSchedule.class);
    private static final ZoneId ZONE_ID = Optional.ofNullable(System.getProperty("org.tweetwallfx.scheduledata.zone"))
            .map(ZoneId::of)
            .orElseGet(ZoneId::systemDefault);
    private static final DateTimeFormatter HOUR_MINUTES = DateTimeFormatter.ofPattern("HH:mm");
    private final Config config;

    private ShowSchedule(Config config) {
        this.config = config;
    }

    /**
     * Creates the upcoming talks schedule node (if not yet present) and fades
     * it into view.
     *
     * @param context the step engine machine context providing access to the
     *                skin and the data providers
     */
    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final ScheduleDataProvider dataProvider = context.getDataProvider(ScheduleDataProvider.class);

        if (null == wordleSkin.getNode().lookup("#scheduleNode")) {
            Pane scheduleNode = new Pane();
            scheduleNode.getStyleClass().add("schedule");
            scheduleNode.setId("scheduleNode");
            scheduleNode.setOpacity(0);

            var title = new Label("Upcoming Talks");

            title.setPrefWidth(config.titleWidth);
            title.setLayoutX((config.width-config.titleWidth) / 2.0);
            title.getStyleClass().add("title");
            title.setPrefHeight(config.titleHeight);
            title.setAlignment(Pos.CENTER);

            scheduleNode.getChildren().add(title);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scheduleNode);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(e -> {
                LOGGER.info("Calling proceed from ShowSchedule");
                context.proceed();
            });
            scheduleNode.setLayoutX(config.layoutX);
            scheduleNode.setLayoutY(config.layoutY);
            scheduleNode.setMinWidth(config.width);
            scheduleNode.setMaxWidth(config.width);
            scheduleNode.setPrefWidth(config.width);
            scheduleNode.setCacheHint(CacheHint.SPEED);
            scheduleNode.setCache(true);
            int col = 0;
            int row = 0;

            Iterator<SessionData> iterator = dataProvider.getFilteredSessionData().iterator();
            String oldRoom = null;
            while (iterator.hasNext()) {
                var sessionData = iterator.next();
                if (null == oldRoom) {
                    oldRoom = sessionData.room.getName();
                }
                if (config.autoSeparateRoomTypes && col != 0 && !oldRoom.startsWith(sessionData.room.getName().substring(0, 2))) {
                    row++;
                    col = 0;
                }
                Pane sessionPane = createSessionNode(context, sessionData);
                double sessionWidth = (config.width - (config.columns - 1) * config.sessionHGap) / config.columns;
                sessionPane.setMinWidth(sessionWidth);
                sessionPane.setMaxWidth(sessionWidth);
                sessionPane.setPrefWidth(sessionWidth);
                sessionPane.setMinHeight(config.sessionHeight);
                sessionPane.setMaxHeight(config.sessionHeight);
                sessionPane.setPrefHeight(config.sessionHeight);
                sessionPane.setLayoutX(col * (sessionWidth + config.sessionHGap));
                sessionPane.setLayoutY(config.titleHeight + config.sessionVGap + (config.sessionHeight + config.sessionVGap) * row);
                scheduleNode.getChildren().add(sessionPane);
                col++;
                if (col == config.columns) {
                    row++;
                    col = 0;
                }
                oldRoom = sessionData.room.getName();
            }

            Platform.runLater(() -> {
                wordleSkin.getPane().getChildren().add(scheduleNode);
                fadeIn.play();
            });
        }
    }

    @Override
    public boolean requiresPlatformThread() {
        return false;
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return config.stepDuration();
    }

    private Pane createSessionNode(final MachineContext context, final SessionData sessionData) {
        var speakerNames = new VBox();
        speakerNames.getStyleClass().add("speakerNames");

        sessionData.speakerObjects.stream().forEach(speaker -> {
            var speakerName = new Label(speaker.getFullName());
            speakerName.setTextAlignment(TextAlignment.RIGHT);
            speakerName.getStyleClass().add("speakerName");
            speakerNames.getChildren().add(speakerName);
            if (config.showCompanyName && sessionData.speakerObjects.size() < config.automaticCompanyNameHide) {
                speaker.getCompany().ifPresent(company -> {
                    var companyName = new Label("(" + company + ")");
                    companyName.setTextAlignment(TextAlignment.RIGHT);
                    companyName.getStyleClass().add("companyName");
                    speakerNames.getChildren().add(companyName);
                });
            }
        });

        var room = new Label(sessionData.room.getName());
        room.getStyleClass().add("room");

        var times = new Label(String.format("%s - %s",
                HOUR_MINUTES.format(OffsetTime.ofInstant(sessionData.beginTime, ZONE_ID)),
                HOUR_MINUTES.format(OffsetTime.ofInstant(sessionData.endTime, ZONE_ID))));
        times.getStyleClass().add("times");

        var topLeftVBox = new VBox(4, room, times);
        Pane topLeft = topLeftVBox;

        if (config.showAvatar) {
            var speakerImageProvider = context.getDataProvider(SpeakerImageProvider.class);
            if (config.compressedAvatars && sessionData.speakerObjects.size() >= config.compressedAvatarsLimit) {
                var speakerImages = new Pane();
                var images = sessionData.speakerObjects.stream()
                        .map(speaker -> createSpeakerImage(speakerImageProvider, speaker))
                        .toList();
                // layout 3 in line 1 , 2 in line 2, 3 in line 3 ... so 3 in even lines (starting with 0)
                // and 2 in odd lines..
                // offset only depend on odd / even
                int lineNum = 0;
                int imageInLine = 0;
                int evenAvatars = 3;
                int oddAvatars = 2;
                double configAvatarOffset = (config.avatarSize * 3 / 4d + 2);
                for (int i = 0; i < images.size(); i++) {
                    var image = images.get(i);
                    image.setLayoutX((imageInLine * 2 + lineNum % 2) * configAvatarOffset);
                    image.setLayoutY(lineNum * config.avatarSize / 2d + 2);
                    speakerImages.getChildren().add(image);
                    imageInLine++;
                    if ((imageInLine == 2 && 4 == images.size()) // special handling for 4 speakers - we want 2 lines of 2 avatars
                            || imageInLine > (0 == lineNum % 2 ? evenAvatars : oddAvatars) - 1) {
                        lineNum++;
                        imageInLine = 0;
                    }
                }
                topLeft = new HBox(4, topLeftVBox, speakerImages);
            } else {
                var speakerImages = new Pane();
                var images = sessionData.speakerObjects.stream()
                        .map(speaker -> createSpeakerImage(speakerImageProvider, speaker))
                        .toList();
                for (int i = 0; i < images.size(); i++) {
                    var image = images.get(i);
                    image.setLayoutX(i * (config.avatarSize + config.avatarSpacing));
                    image.setLayoutY(2);
                    speakerImages.getChildren().add(image);
                }
                topLeft = new HBox(4, topLeftVBox, speakerImages);
            }
        }

        if (config.showFavourite && sessionData.favouritesCount >= 0) {
            final EmojiFlow faiFavCount = new EmojiFlow("👍", 24D, 24D);
            faiFavCount.getStyleClass().setAll("favoriteGlyph");

            var favLabel = new Label(Integer.toString(sessionData.favouritesCount));
            favLabel.getStyleClass().setAll("favoriteCount");

            final HBox favourites = new HBox(5, faiFavCount, favLabel);
            favourites.setAlignment(Pos.CENTER_LEFT);

            topLeftVBox.getChildren().add(favourites);
        }

        var title = new EmojiFlow();
        title.setText(sessionData.title);
        title.setEmojiFitWidth(config.titleFontSize);
        title.setEmojiFitHeight(config.titleFontSize);
        title.getStyleClass().add("title");
        title.setMaxHeight(Double.MAX_VALUE);

        Node trackImageView;
        if (config.showTrackAvatar && null != sessionData.trackImageUrl) {
            var trackImage = context.getDataProvider(TrackImageDataProvider.class).getImage(sessionData.trackImageUrl);
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
            sessionData.tags.stream().forEach(tag -> {
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

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link ShowSchedule}.
     */
    public static final class FactoryImpl implements Step.Factory {

        /**
         * Creates a new instance.
         */
        public FactoryImpl() { }

        @Override
        public ShowSchedule create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new ShowSchedule(stepDefinition.getConfig(Config.class));
        }

        @Override
        public Class<ShowSchedule> getStepClass() {
            return ShowSchedule.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(ScheduleDataProvider.class, SpeakerImageProvider.class, TrackImageDataProvider.class);
        }
    }

    /**
     * Configuration POJO for the {@link ShowSchedule} step.
     */
    public static class Config extends AbstractConfig {

        /**
         * Creates a new instance.
         */
        public Config() { }

        /** The x coordinate of the schedule node layout. */
        public double layoutX = 0;
        /** The y coordinate of the schedule node layout. */
        public double layoutY = 0;
        /** Whether speaker avatars are shown at all. */
        public boolean showAvatar = false;
        /** The size (width and height) of a speaker avatar in pixels. */
        public int avatarSize = 64;
        /** The corner arc size used when clipping rectangular avatars. */
        public int avatarArcSize = 20;
        /** The horizontal spacing between adjacent speaker avatars. */
        public int avatarSpacing = 4;
        /** Whether the favourite count of a session is shown. */
        public boolean showFavourite = false;
        /** The width of the title node in pixels. */
        public double titleWidth = 800;
        /** The total width of the schedule node in pixels. */
        public double width = 800;
        /** The height of the title node in pixels. */
        public double titleHeight = 60;
        /** The vertical gap between two session rows in pixels. */
        public double sessionVGap = 10;
        /** The horizontal gap between two session columns in pixels. */
        public double sessionHGap = 10;
        /** The height of a single session node in pixels. */
        public double sessionHeight = 200;
        /** Whether the track avatar of a session is shown. */
        public boolean showTrackAvatar = true;
        /** Whether speaker avatars are clipped to a circle. */
        public boolean circularAvatar = true;
        /** Whether the tags of a session are shown. */
        public boolean showTags = false;
        /** Whether avatars are arranged in multiple compressed lines. */
        public boolean compressedAvatars = true;
        /** The number of speakers from which on compressed avatars are used. */
        public int compressedAvatarsLimit = 4;
        /** The number of speakers below which the company name is hidden. */
        public int automaticCompanyNameHide = 6;
        /** The number of session columns of the schedule grid. */
        public int columns = 2;
        /** Whether a new row is started when the room type changes. */
        public boolean autoSeparateRoomTypes = false;
        /** Whether the company name of a speaker is shown. */
        public boolean showCompanyName = false;
        /** The font size of the session title in points. */
        public int titleFontSize = 13;
    }
}
