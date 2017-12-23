/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.dataprovider.TagCloudDataProvider;
import org.tweetwallfx.controls.stepengine.Step;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;
import org.tweetwallfx.controls.transition.LocationTransition;

/**
 * @author Sven Reimers
 */
public class CloudToCloudStep implements Step {

    private CloudToCloudStep() {
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
        Bounds layoutBounds = wordleSkin.getPane().getLayoutBounds();
        List<Word> limitedWords = sortedWords.stream().limit(wordleSkin.getDisplayCloudTags()).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        WordleLayout.Configuration configuration = new WordleLayout.Configuration(limitedWords, wordleSkin.getFont(), wordleSkin.getFontSizeMin(), wordleSkin.getFontSizeMax(), layoutBounds);
        if (null != wordleSkin.getLogo()) {
            configuration.setBlockedAreaBounds(wordleSkin.getLogo().getBoundsInParent());
        }
        if (null != wordleSkin.getSecondLogo()) {
            configuration.setBlockedAreaBounds(wordleSkin.getSecondLogo().getBoundsInParent());
        }

        WordleLayout cloudWordleLayout = WordleLayout.createWordleLayout(configuration);
        List<Word> unusedWords = wordleSkin.word2TextMap.keySet().stream().filter(word -> !cloudWordleLayout.getWordLayoutInfo().containsKey(word)).collect(Collectors.toList());

        Duration defaultDuration = Duration.seconds(1.5);

        SequentialTransition morph = new SequentialTransition();

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();

        unusedWords.forEach(word -> {
            Text textNode = wordleSkin.word2TextMap.remove(word);

            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(0);
            ft.setOnFinished((event) -> {
                wordleSkin.getPane().getChildren().remove(textNode);
            });
            fadeOutTransitions.add(ft);
        });

        ParallelTransition fadeOuts = new ParallelTransition();
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        morph.getChildren().add(fadeOuts);

        List<Word> existingWords = cloudWordleLayout.getWordLayoutInfo().keySet().stream().filter(word -> wordleSkin.word2TextMap.containsKey(word)).collect(Collectors.toList());

        existingWords.forEach(word -> {

            Text textNode = wordleSkin.word2TextMap.get(word);
            cloudWordleLayout.fontSizeAdaption(textNode, word.getWeight());
            Bounds bounds = cloudWordleLayout.getWordLayoutInfo().get(word);

            LocationTransition lt = new LocationTransition(defaultDuration, textNode);
            lt.setFromX(textNode.getLayoutX());
            lt.setFromY(textNode.getLayoutY());
            lt.setToX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            lt.setToY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            moveTransitions.add(lt);
        });

        ParallelTransition moves = new ParallelTransition();
        moves.getChildren().addAll(moveTransitions);
        morph.getChildren().add(moves);

        List<Word> newWords = cloudWordleLayout.getWordLayoutInfo().keySet().stream().filter(word -> !wordleSkin.word2TextMap.containsKey(word)).collect(Collectors.toList());

        List<Text> newTextNodes = new ArrayList<>();
        newWords.forEach(word -> {
            Text textNode = cloudWordleLayout.createTextNode(word);
            wordleSkin.word2TextMap.put(word, textNode);

            Bounds bounds = cloudWordleLayout.getWordLayoutInfo().get(word);
            textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            textNode.setOpacity(0);
            newTextNodes.add(textNode);
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(1);
            fadeInTransitions.add(ft);
        });
        wordleSkin.getPane().getChildren().addAll(newTextNodes);

        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        morph.getChildren().add(fadeIns);

        morph.setOnFinished(e -> context.proceed());
        morph.play();
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link CloudToCloudStep}.
     */
    public static final class Factory implements Step.Factory {

        @Override
        public CloudToCloudStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new CloudToCloudStep();
        }

        @Override
        public Class<CloudToCloudStep> getStepClass() {
            return CloudToCloudStep.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(TagCloudDataProvider.class);
        }
    }
}
