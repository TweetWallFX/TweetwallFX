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
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.devoxx17be.animations.FlipInXTransition;
import org.tweetwallfx.devoxx2017be.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.devoxx2017be.dataprovider.TopTalksTodayDataProvider;
import org.tweetwallfx.devoxx2017be.dataprovider.VotedTalk;

/**
 * Devox 2017 Show Top Rated Talks Today (Flip In) Animation Step
 * @author Sven Reimers
 */
public class Devoxx17ShowTopRatedToday extends AbstractStep {

    @Override
    public void doStep(StepEngine.MachineContext context) {

        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final TopTalksTodayDataProvider dataProvider = wordleSkin.getSkinnable().getDataProvider(TopTalksTodayDataProvider.class);

        List<FlipInXTransition> transitions = new ArrayList<>();
        if (null == wordleSkin.getNode().lookup("#topRatedToday")) {
            try {
                Node scheduleNode = FXMLLoader.<Node>load(this.getClass().getResource("/topratedtalktoday.fxml"));
                transitions.add(new FlipInXTransition(scheduleNode));
                scheduleNode.layoutXProperty().bind(Bindings.multiply(150.0/1920.0, wordleSkin.getSkinnable().widthProperty()));
                scheduleNode.layoutYProperty().bind(Bindings.multiply(200.0/1280.0, wordleSkin.getSkinnable().heightProperty()));
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
                dataProvider.getFilteredSessionData();
            } catch (IOException ex) {
                LogManager.getLogger(Devoxx17ShowTopRatedToday.class.getName()).error(ex);
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
            Rectangle clip = new Rectangle(
                    speakerImage.getFitWidth(), speakerImage.getFitHeight()
            );
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            speakerImage.setClip(clip);
            speakerImage.setImage(SpeakerImageProvider.getSpeakerImage(votingResultTalk.speakerAvatar));
            return session;
        } catch (IOException ex) {
            LogManager.getLogger(Devoxx17ShowSchedule.class).error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public java.time.Duration preferredStepDuration(StepEngine.MachineContext context) {
        return java.time.Duration.ZERO; // do not stop at this step;
    }

    @Override
    public boolean shouldSkip(StepEngine.MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        // if scheduleNode available do not execute Step.
        return null != wordleSkin.getNode().lookup("#topRatedToday");
    }

}
