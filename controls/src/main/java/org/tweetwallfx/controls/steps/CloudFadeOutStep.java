/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 TweetWallFX
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
package org.tweetwallfx.controls.steps;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * @author Jörg Michelberger
 */
public class CloudFadeOutStep implements Step {

    private CloudFadeOutStep() {
        // prevent external instantiation
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return java.time.Duration.ofSeconds(2);
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");

        List<Transition> fadeOutTransitions = new ArrayList<>();

        Duration defaultDuration = Duration.seconds(1.5);

        // kill the remaining words from the cloud
        wordleSkin.word2TextMap.entrySet().forEach(entry -> {
            Text textNode = entry.getValue();
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(0);
            ft.setOnFinished(event
                    -> wordleSkin.getPane().getChildren().remove(textNode));
            fadeOutTransitions.add(ft);
        });
        wordleSkin.word2TextMap.clear();

        ParallelTransition fadeLOuts = new ParallelTransition();

        fadeLOuts.getChildren().addAll(fadeOutTransitions);
        fadeLOuts.setOnFinished(e -> context.proceed());
        fadeLOuts.play();
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link CloudFadeOutStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public CloudFadeOutStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new CloudFadeOutStep();
        }

        @Override
        public Class<CloudFadeOutStep> getStepClass() {
            return CloudFadeOutStep.class;
        }
    }
}
