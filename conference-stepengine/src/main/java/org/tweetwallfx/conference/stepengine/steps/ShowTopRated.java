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
package org.tweetwallfx.conference.stepengine.steps;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.conference.stepengine.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TopTalksTodayDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.TopTalksWeekDataProvider;
import org.tweetwallfx.conference.stepengine.dataprovider.VotedTalk;
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

    private static final Logger LOGGER = LogManager.getLogger(ShowTopRated.class);
    private final Function<MachineContext, Collection<VotedTalk>> votedTalksConverter;
    private final Config config;
    private final String lookupId;
    private final String fxmlResourcePath;

    private ShowTopRated(
            final Function<MachineContext, Collection<VotedTalk>> votedTalksConverter,
            final Config config,
            final String lookupId,
            final String fxmlResourcePath) {
        this.votedTalksConverter = Objects.requireNonNull(
                votedTalksConverter,
                "votedTalksConverter must not be null");
        this.config = Objects.requireNonNull(
                config,
                "config must not be null");
        this.lookupId = Objects.requireNonNull(
                lookupId,
                "lookupId must not be null");
        this.fxmlResourcePath = Objects.requireNonNull(
                fxmlResourcePath,
                "fxmlResourcePath must not be null");
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");

        List<FlipInXTransition> transitions = new ArrayList<>();
        try {
            Node scheduleNode = FXMLLoader.<Node>load(this.getClass().getResource(fxmlResourcePath));
            transitions.add(new FlipInXTransition(scheduleNode));
            scheduleNode.setLayoutX(config.layoutX);
            scheduleNode.setLayoutY(config.layoutY);
//            scheduleNode.layoutXProperty().bind(Bindings.multiply(150.0 / 1920.0, wordleSkin.getSkinnable().widthProperty()));
//            scheduleNode.layoutYProperty().bind(Bindings.multiply(200.0 / 1280.0, wordleSkin.getSkinnable().heightProperty()));
            wordleSkin.getPane().getChildren().add(scheduleNode);

            GridPane grid = (GridPane) scheduleNode.lookup("#sessionGrid");
            int col = 0;
            int row = 0;

            Iterator<VotedTalk> iterator = votedTalksConverter.apply(context).iterator();
            final SpeakerImageProvider speakerImageProvider = context.getDataProvider(SpeakerImageProvider.class);
            while (iterator.hasNext()) {
                Node node = createTalkNode(iterator.next(), speakerImageProvider);
                grid.getChildren().add(node);
                GridPane.setColumnIndex(node, col);
                GridPane.setRowIndex(node, row);
                row += 1;
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
        ParallelTransition flipIns = new ParallelTransition();
        flipIns.getChildren().addAll(transitions);
        flipIns.setOnFinished(e -> context.proceed());

        flipIns.play();
    }

    private Node createTalkNode(
            final VotedTalk votingResultTalk,
            final SpeakerImageProvider speakerImageProvider) {
        try {
            Node session = FXMLLoader.<Node>load(this.getClass().getResource("/ratedTalk.fxml"));
            Text title = (Text) session.lookup("#title");
            title.setText(votingResultTalk.talk.getName());
            Text speakers = (Text) session.lookup("#speakers");
            speakers.setText(votingResultTalk.speakers);
            Label averageVoting = (Label) session.lookup("#averageVote");
            averageVoting.setText(String.format("%.1f", votingResultTalk.ratingAverageScore));
            Label voteCount = (Label) session.lookup("#voteCount");
            voteCount.setText(votingResultTalk.ratingTotalVotes + " Votes");
            ImageView speakerImage = (ImageView) session.lookup("#speakerImage");
            speakerImage.setImage(speakerImageProvider.getSpeakerImage(votingResultTalk.speaker));
            speakerImage.setFitHeight(64);
            speakerImage.setFitWidth(64);
            if (config.circularAvatar) {
                Circle clip = new Circle(speakerImage.getFitWidth() /2f, speakerImage.getFitHeight()/2f, speakerImage.getFitWidth() /2f);
                speakerImage.setClip(clip);
            } else {
                Rectangle clip = new Rectangle(speakerImage.getFitWidth(), speakerImage.getFitHeight());
                clip.setArcWidth(20);
                clip.setArcHeight(20);
            }
            return session;
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new IllegalStateException(ex);
        }
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
                            "#topRatedToday",
                            "/topratedtalktoday.fxml");

                case WEEK:
                    return new ShowTopRated(
                            mc -> mc.getDataProvider(TopTalksWeekDataProvider.class).getFilteredSessionData(),
                            stepDefinition.getConfig(Config.class),
                            "#topRatedWeek",
                            "/topratedtalkweek.fxml");

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
                            SpeakerImageProvider.class);

                case WEEK:
                    return Arrays.asList(
                            TopTalksWeekDataProvider.class,
                            SpeakerImageProvider.class);

                default:
                    throw new IllegalArgumentException("TopVotedType " + topVotedType + " is not supported");
            }
        }
    }

    public static class Config extends AbstractConfig {

        private TopVotedType topVotedType = null;
        public double layoutX = 0;
        public double layoutY = 0;
        public boolean circularAvatar = true;

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
        TODAY,
        /**
         * The Top Voted Session of the Week.
         */
        WEEK;
    }
}
