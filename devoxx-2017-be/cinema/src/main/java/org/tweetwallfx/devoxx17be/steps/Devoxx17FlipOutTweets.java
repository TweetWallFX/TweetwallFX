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
import java.util.List;
import javafx.animation.ParallelTransition;
import javafx.scene.layout.VBox;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.devoxx17be.animations.FlipOutXTransition;

/**
 *
 * @author sven
 */
public class Devoxx17FlipOutTweets extends AbstractStep {

    @Override
    public void doStep(StepEngine.MachineContext context) {

        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");

        VBox vbox = (VBox) wordleSkin.getNode().lookup("#tweetList");

        List<FlipOutXTransition> transitions = new ArrayList<>();
        vbox.getChildren().forEach(node -> transitions.add(new FlipOutXTransition(node)));
        ParallelTransition flipOuts = new ParallelTransition();
        flipOuts.getChildren().addAll(transitions);
        flipOuts.setOnFinished(e -> {
            vbox.getChildren().removeAll();
            wordleSkin.getPane().getChildren().remove(vbox);
            context.proceed();
        });
        flipOuts.play();
    }

    @Override
    public long preferredStepDuration(StepEngine.MachineContext context) {
        return 1;
    }

    @Override
    public boolean shouldSkip(StepEngine.MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        return null == wordleSkin.getNode().lookup("#tweetList");
    }

}
