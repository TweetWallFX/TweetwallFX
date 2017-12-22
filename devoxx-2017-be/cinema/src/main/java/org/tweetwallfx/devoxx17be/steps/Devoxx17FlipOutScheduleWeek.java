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

import javafx.scene.Node;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.devoxx17be.animations.FlipOutXTransition;
import org.tweetwallfx.devoxx2017be.dataprovider.TopTalksWeekDataProvider;

/**
 * Devox 2017 Schedule Flip Out Animation Step
 *
 * @author Sven Reimers
 */
public class Devoxx17FlipOutScheduleWeek implements Step {

    private Devoxx17FlipOutScheduleWeek() {
        // prevent external instantiation
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        Node node = wordleSkin.getNode().lookup("#scheduleNode");
        FlipOutXTransition flipOutXTransition = new FlipOutXTransition(node);
        flipOutXTransition.setOnFinished(e -> {
            wordleSkin.getPane().getChildren().remove(node);
            context.proceed();
        });
        flipOutXTransition.play();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        return null == wordleSkin.getNode().lookup("#scheduleNode")
                || context.getDataProvider(TopTalksWeekDataProvider.class).getFilteredSessionData().isEmpty();
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link Devoxx17FlipOutScheduleWeek}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public Devoxx17FlipOutScheduleWeek create(final StepEngineSettings.Step stepSettings) {
            return new Devoxx17FlipOutScheduleWeek();
        }

        @Override
        public Class<Devoxx17FlipOutScheduleWeek> getStepClass() {
            return Devoxx17FlipOutScheduleWeek.class;
        }
    }
}
