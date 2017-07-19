/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.steps;

import java.util.ArrayList;
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
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.transition.LocationTransition;

/**
 *
 * @author sven
 */
public class CloudToCloudStep extends AbstractStep { 

    @Override
    public long preferredStepDuration(MachineContext context) {
        return 5000;
    }

    @Override
    public void doStep(MachineContext context) {
//        pane.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
        WordleSkin wordleSkin = (WordleSkin)context.get("WordleSkin");
//        Wordle wordle = (Wordle)context.get("Wordle");

        List<Word> sortedWords = wordleSkin.getSkinnable().getDataProvider(TagCloudDataProvider.class).getWords();
        if (sortedWords.isEmpty()) {
            return;
        }

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
}
