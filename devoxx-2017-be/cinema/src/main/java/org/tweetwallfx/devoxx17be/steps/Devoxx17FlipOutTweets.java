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
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.devoxx17be.animations.FlipOutXTransition;

/**
 * Devox 2017 TweetStream Flip Out Animation Step
 *
 * @author Sven Reimers
 */
public class Devoxx17FlipOutTweets implements Step {

    private Devoxx17FlipOutTweets() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        VBox vbox = (VBox) wordleSkin.getNode().lookup("#tweetList");

        List<Transition> transitions = new ArrayList<>();
        vbox.getChildren().forEach(node -> transitions.add(new FlipOutXTransition(node)));
        Node imageNode = wordleSkin.getNode().lookup("#tweetImage");
        ParallelTransition flipOuts = new ParallelTransition();
        if (null != imageNode) {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), imageNode);
            fadeTransition.setDelay(Duration.seconds(0.2));
            fadeTransition.fromValueProperty().setValue(1);
            fadeTransition.toValueProperty().setValue(0);
            flipOuts.getChildren().add(fadeTransition);
        }
        flipOuts.getChildren().addAll(transitions);
        flipOuts.setOnFinished(e -> {
            vbox.getChildren().removeAll();
            wordleSkin.getPane().getChildren().remove(vbox);
            if (null != imageNode) {
                wordleSkin.getPane().getChildren().remove(imageNode);
            }
            context.proceed();
        });
        flipOuts.play();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        return null == wordleSkin.getNode().lookup("#tweetList");
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link Devoxx17FlipOutTweets}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public Devoxx17FlipOutTweets create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new Devoxx17FlipOutTweets();
        }

        @Override
        public Class<Devoxx17FlipOutTweets> getStepClass() {
            return Devoxx17FlipOutTweets.class;
        }
    }
}
