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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javafx.animation.ParallelTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.devoxx17be.animations.FlipInXTransition;
import org.tweetwallfx.devoxx2017be.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.devoxx2017be.dataprovider.TopTalksWeekDataProvider;
import org.tweetwallfx.devoxx2017be.dataprovider.VotedTalk;

/**
 * Devox 2017 Show Top Rated Talks Week (Flip In) Animation Step
 *
 * @author Sven Reimers
 */
public class Devoxx17ShowTopRatedWeek implements Step {

    private static final Logger LOGGER = LogManager.getLogger(Devoxx17ShowTopRatedWeek.class);

    private Devoxx17ShowTopRatedWeek() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final TopTalksWeekDataProvider dataProvider = context.getDataProvider(TopTalksWeekDataProvider.class);

        List<FlipInXTransition> transitions = new ArrayList<>();
        if (null == wordleSkin.getNode().lookup("#topRatedWeek")) {
            try {
                Node scheduleNode = FXMLLoader.<Node>load(this.getClass().getResource("/topratedtalkweek.fxml"));
                transitions.add(new FlipInXTransition(scheduleNode));
                scheduleNode.layoutXProperty().bind(Bindings.multiply(150.0 / 1920.0, wordleSkin.getSkinnable().widthProperty()));
                scheduleNode.layoutYProperty().bind(Bindings.multiply(200.0 / 1280.0, wordleSkin.getSkinnable().heightProperty()));
                wordleSkin.getPane().getChildren().add(scheduleNode);

                GridPane grid = (GridPane) scheduleNode.lookup("#sessionGrid");
                int col = 0;
                int row = 0;

                Iterator<VotedTalk> iterator = dataProvider.getFilteredSessionData().iterator();
                while (iterator.hasNext()) {
                    Node node = createTalkNode(iterator.next());
                    grid.getChildren().add(node);
                    GridPane.setColumnIndex(node, col);
                    GridPane.setRowIndex(node, row);
                    row += 1;
                }
            } catch (IOException ex) {
                LOGGER.error(ex);
            }
        }
        ParallelTransition flipIns = new ParallelTransition();
        flipIns.getChildren().addAll(transitions);
        flipIns.setOnFinished(e -> context.proceed());

        flipIns.play();
    }

    private Node createTalkNode(VotedTalk votingResultTalk) {
        try {
            Node session = FXMLLoader.<Node>load(this.getClass().getResource("/ratedTalk.fxml"));
            Text title = (Text) session.lookup("#title");
            title.setText(votingResultTalk.proposalTitle);
            Text speakers = (Text) session.lookup("#speakers");
            speakers.setText(votingResultTalk.speakers);
            Label averageVoting = (Label) session.lookup("#averageVote");
            averageVoting.setText(String.format("%.1f", votingResultTalk.ratingAverageScore));
            Label voteCount = (Label) session.lookup("#voteCount");
            voteCount.setText(votingResultTalk.ratingTotalVotes + " Votes");
            ImageView speakerImage = (ImageView) session.lookup("#speakerImage");
            speakerImage.setImage(SpeakerImageProvider.getSpeakerImage(votingResultTalk.speakerAvatar));
            speakerImage.setFitHeight(64);
            speakerImage.setFitWidth(64);
            Rectangle clip = new Rectangle(speakerImage.getFitWidth(), speakerImage.getFitHeight());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            speakerImage.setClip(clip);
            return session;
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        return context.getDataProvider(TopTalksWeekDataProvider.class).getFilteredSessionData().isEmpty();
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link Devoxx17ShowTopRatedWeek}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public Devoxx17ShowTopRatedWeek create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new Devoxx17ShowTopRatedWeek();
        }

        @Override
        public Class<Devoxx17ShowTopRatedWeek> getStepClass() {
            return Devoxx17ShowTopRatedWeek.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(TopTalksWeekDataProvider.class);
        }
    }
}
