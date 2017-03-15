/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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

//import org.tweetwallfx.controls.Wordle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.tweetwallfx.controls.TweetLayout;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.WordleLayout;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.transition.FontSizeTransition;
import org.tweetwallfx.controls.transition.LocationTransition;

/**
 *
 * @author Jörg Michelberger
 */
public class FadeInCloudStep extends AbstractStep {

    @Override
    public long preferredStepDuration(MachineContext context) {
        return 5000;
    }

    @Override
    public void doStep(MachineContext context) {
//        context.getWordle().setLayoutMode(Wordle.LayoutMode.TWEET);
        Logger startupLogger = Logger.getLogger("org.tweetwallfx.startup");
        startupLogger.trace("tweetToCloud()");
        
        WordleSkin wordleSkin = (WordleSkin)context.get("WordleSkin");
//        Wordle wordle = (Wordle)context.get("Wordle");
        
        List<Word> sortedWords = new ArrayList<>(wordleSkin.getSkinnable().wordsProperty().getValue());

        if (sortedWords.isEmpty()) {
            return;
        }

        List<Word> limitedWords = sortedWords.stream().limit(wordleSkin.getDisplayCloudTags()).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());
 
        Bounds layoutBounds = wordleSkin.getPane().getLayoutBounds();

        WordleLayout.Configuration configuration = new WordleLayout.Configuration(limitedWords, wordleSkin.getFont(), wordleSkin.getFontSizeMin(), wordleSkin.getFontSizeMax(),layoutBounds);
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
}
