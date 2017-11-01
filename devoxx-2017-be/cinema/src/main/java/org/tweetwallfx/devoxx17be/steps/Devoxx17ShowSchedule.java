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
import java.util.stream.Collectors;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.devoxx17be.animations.FlipInXTransition;
import org.tweetwallfx.devoxx2017be.dataprovider.ScheduleDataProvider;
import org.tweetwallfx.devoxx2017be.dataprovider.SessionData;

/**
 *
 * @author sven
 */
public class Devoxx17ShowSchedule extends Devoxx17FlipInTweets {

    @Override
    public void doStep(StepEngine.MachineContext context) {
        
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final ScheduleDataProvider dataProvider = wordleSkin.getSkinnable().getDataProvider(ScheduleDataProvider.class);
        
        List<FlipInXTransition> transitions = new ArrayList<>();
        if (null == wordleSkin.getNode().lookup("#scheduleNode")) {
            try {
                Node scheduleNode = FXMLLoader.<Node>load(this.getClass().getResource("/schedule.fxml"));
                transitions.add(new FlipInXTransition(scheduleNode));
                scheduleNode.setLayoutX(150);
                scheduleNode.setLayoutY(200);
                wordleSkin.getPane().getChildren().add(scheduleNode);
                
                GridPane grid = (GridPane) scheduleNode.lookup("#sessionGrid");
                int col=0;
                int row=0;
                
                Iterator<SessionData> iterator = dataProvider.getFilteredSessionData().iterator();
                while (iterator.hasNext()) {
                    Node node = createSessionNode(iterator.next());
                    grid.getChildren().add(node);
                    GridPane.setColumnIndex(node, col);
                    GridPane.setRowIndex(node, row);
                    col = (col == 0)? 1 : 0;
                    if (col == 0 ) row++;
                }
            } catch (IOException ex) {
                LogManager.getLogger(Devoxx17ShowSchedule.class.getName()).error(ex);
            }
        }
        ParallelTransition flipIns = new ParallelTransition();
        flipIns.getChildren().addAll(transitions);
        flipIns.setOnFinished(e -> context.proceed());

        flipIns.play();                
        
    }
    
    private Node createSessionNode(SessionData sessionData) {
        try {
            Node session = FXMLLoader.<Node>load(this.getClass().getResource("/session.fxml"));
            Text title = (Text)session.lookup("#title");
            title.setText(sessionData.title);
            Text speakers = (Text)session.lookup("#speakers");
            speakers.setText(sessionData.speakers.stream().collect(Collectors.joining(", ")));
            Label room = (Label)session.lookup("#room");
            room.setText(sessionData.room);
            Label startTime = (Label)session.lookup("#startTime");
            startTime.setText(sessionData.beginTime);
            return session;
        } catch (IOException ex) {
            LogManager.getLogger(Devoxx17ShowSchedule.class).error(ex);
            throw new RuntimeException(ex);
        }
    }
        
}
