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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.WordleLayout;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.dataprovider.TagCloudDataProvider;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

public class FadeInCloudStep implements Step {

    private FadeInCloudStep() {
        // prevent external instantiation
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return java.time.Duration.ofSeconds(5);
    }

    @Override
    public void doStep(final MachineContext context) {
        List<Word> sortedWords = context.getDataProvider(TagCloudDataProvider.class).getWords();

        if (sortedWords.isEmpty()) {
            return;
        }

        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        List<Word> limitedWords = sortedWords.stream().limit(wordleSkin.getDisplayCloudTags()).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        Bounds layoutBounds = wordleSkin.getPane().getLayoutBounds();

        WordleLayout.Configuration configuration = new WordleLayout.Configuration(limitedWords, wordleSkin.getFont(), wordleSkin.getFontSizeMax(), layoutBounds);
        if (null != wordleSkin.getLogo()) {
            configuration.setBlockedAreaBounds(wordleSkin.getLogo().getBoundsInParent());
        }
        if (null != wordleSkin.getSecondLogo()) {
            configuration.setBlockedAreaBounds(wordleSkin.getSecondLogo().getBoundsInParent());
        }
        WordleLayout cloudWordleLayout = WordleLayout.createWordleLayout(configuration);
        Duration defaultDuration = Duration.seconds(1.5);

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();

        cloudWordleLayout.getWordLayoutInfo().entrySet().stream().forEach(entry -> {
            Word word = entry.getKey();
            Bounds bounds = entry.getValue();
            Text textNode = cloudWordleLayout.createTextNode(word);
            wordleSkin.word2TextMap.put(word, textNode);
            textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            textNode.setOpacity(0);
            wordleSkin.getPane().getChildren().add(textNode);
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(1);
            fadeInTransitions.add(ft);
        });

        ParallelTransition fadeOuts = new ParallelTransition();
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        ParallelTransition moves = new ParallelTransition();
        moves.getChildren().addAll(moveTransitions);
        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        morph.setOnFinished(e -> context.proceed());
        morph.play();
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link FadeInCloudStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public FadeInCloudStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new FadeInCloudStep();
        }

        @Override
        public Class<FadeInCloudStep> getStepClass() {
            return FadeInCloudStep.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(TagCloudDataProvider.class);
        }
    }
}
