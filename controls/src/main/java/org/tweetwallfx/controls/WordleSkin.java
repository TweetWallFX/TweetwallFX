/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
package org.tweetwallfx.controls;

import de.jensd.fx.glyphs.GlyphsStack;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * @author sven
 */
public class WordleSkin extends SkinBase<Wordle> {

    private final Map<Word, Text> word2TextMap = new HashMap<>();
    // used for Tweet Display
    private final List<TweetLayout.TweetWordNode> tweetWordList = new ArrayList<>();
    private final Pane pane;
    private final Pane stackPane;
    private List<Word> limitedWords;
    private HBox hbox;
    private Point2D lowerLeft;
    private HBox mediaBox;

    private int displayCloudTags = 25;

    private ImageView logo;
    private ImageView backgroundImage;
    private Font font;
    private final Boolean favIconsVisible;
    private final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    private final ImageCache mediaImageCache = new ImageCache(new ImageCache.DefaultImageCreator());
    private final ImageCache profileImageCache = new ImageCache(new ImageCache.ProfileImageCreator());
    
    public WordleSkin(Wordle wordle) {
        super(wordle);
        //create panes
        stackPane = new StackPane();
        pane = new Pane();
        pane.prefWidthProperty().bind(stackPane.widthProperty());
        pane.prefHeightProperty().bind(stackPane.heightProperty());
        //assemble panes
        stackPane.getChildren().addAll(pane);
        this.getChildren().add(stackPane);
        //assign style
        stackPane.getStylesheets().add(this.getClass().getResource("wordle.css").toExternalForm());
        
        getSkinnable().logoProperty().addListener((obs, oldValue, newValue) -> {
            updateLogo(newValue);
        });

        getSkinnable().backgroundGraphicProperty().addListener((obs, oldValue, newValue) -> {
            updateBackgroundGraphic(newValue);
        });

        updateBackgroundGraphic(getSkinnable().backgroundGraphicProperty().getValue());
        updateLogo(getSkinnable().logoProperty().getValue());

//        pane.setStyle("-fx-border-width: 1px; -fx-border-color: black;");
//        this.getChildren().add(pane);
        updateCloud();

        wordle.wordsProperty.addListener((obs, oldValue, newValue) -> {
            switch (wordle.layoutModeProperty.get()) {
                case TWEET:
                    break;
                case WORDLE:
                    updateCloud();
                    break;
            }
        });

        pane.widthProperty().addListener(bounds -> {
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
            switch (wordle.layoutModeProperty.get()) {
                case TWEET:
                    break;
                case WORDLE:
                    updateCloud();
                    break;
            }
        });

        pane.heightProperty().addListener(bounds -> {
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
            switch (wordle.layoutModeProperty.get()) {
                case TWEET:
                    break;
                case WORDLE:
                    updateCloud();
                    break;
            }
        });

        wordle.layoutModeProperty.addListener((obs, oldVModee, newMode) -> {
            switch (newMode) {
                case TWEET:
//                    addTweetToCloud();

                    cloudToTweet();
                    break;
                case WORDLE:
                    tweetToCloud();
//                    removeTweetFromCloud();
                    break;
            }
        });
        favIconsVisible = wordle.favIconsVisibleProperty().get();
        displayCloudTags = wordle.displayedNumberOfTagsProperty().get();
        font = wordle.fontProperty().get();
    }

    private void updateLogo(final String newLogo) {
        if (null != logo) {
            pane.getChildren().remove(logo);
            logo = null;
        }   
        System.out.println("Logo: " + newLogo);
        if (null != newLogo && !newLogo.isEmpty()) {
            logo = new ImageView(newLogo);
            logo.getStyleClass().add("logo");            
            pane.getChildren().add(logo);
            logo.setLayoutX(0);
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
        }
    }

    private void updateBackgroundGraphic(final String newBackgroundGraphic) {
        if (null != backgroundImage) {
            stackPane.getChildren().remove(backgroundImage);
            backgroundImage = null;
        }
        if (null != newBackgroundGraphic && !newBackgroundGraphic.isEmpty()) {
            backgroundImage = new ImageView(newBackgroundGraphic) {
                @Override
                public double minHeight(double width) {
                    return 10; 
                }

                @Override
                public double minWidth(double height) {
                    return 10;
                }
                
            };

            backgroundImage.getStyleClass().add("bg-image");
            
            backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());

            backgroundImage.setPreserveRatio(true);
            backgroundImage.setCache(true);
            backgroundImage.setSmooth(true);
            
            stackPane.getChildren().add(0, backgroundImage);
        }
    }


    Point2D tweetLineOffset = new Point2D(0, 0);

    private void cloudToTweet() {
        Logger startupLogger = Logger.getLogger("org.tweetwallfx.startup");
        startupLogger.trace("cloudToTweet()");
        Bounds layoutBounds = pane.getLayoutBounds();
        Tweet tweetInfo = getSkinnable().tweetInfoProperty.get();

        Point2D minPosTweetText = new Point2D(layoutBounds.getWidth() / 6d, (layoutBounds.getHeight() - logo.getImage().getHeight()) / 4d);

        double width = layoutBounds.getWidth() * (2 / 3d);

        TweetLayout tweetLayout = TweetLayout.createTweetLayout(new TweetLayout.Configuration(tweetInfo, font, TWEET_FONT_SIZE));       

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();
        
        lowerLeft = new Point2D(minPosTweetText.getX(), minPosTweetText.getY());
        tweetLineOffset = new Point2D(0, 0);

        Duration defaultDuration = Duration.seconds(1.5);

        TweetWordNodeFactory wordNodeFactory = TweetWordNodeFactory.createFactory(new TweetWordNodeFactory.Configuration(font, TWEET_FONT_SIZE));
        
        tweetLayout.getWordLayoutInfo().stream().forEach(tweetWord -> {
            Word word = new Word(tweetWord.text.trim(), -2);
            if (word2TextMap.containsKey(word)) {
                Text textNode = word2TextMap.remove(word);
                tweetWordList.add(new TweetLayout.TweetWordNode(tweetWord, textNode));

                FontSizeTransition ft = new FontSizeTransition(defaultDuration, textNode);
                ft.setFromSize(textNode.getFont().getSize());
                ft.setToSize(TWEET_FONT_SIZE);
                moveTransitions.add(ft);

                Bounds bounds = tweetLayout.getWordLayoutInfo().stream().filter(tw -> tw.text.trim().equals(word.getText())).findFirst().get().bounds;

                LocationTransition lt = new LocationTransition(defaultDuration, textNode);
                lt.setFromX(textNode.getLayoutX());
                lt.setFromY(textNode.getLayoutY());
                tweetLineOffset = tweetLayout.tweetWordLineOffset(bounds, lowerLeft, width, tweetLineOffset);
                Point2D twPoint = tweetLayout.layoutTweetWord(bounds, minPosTweetText, tweetLineOffset);
                lt.setToX(twPoint.getX());
                lt.setToY(twPoint.getY());
                if (twPoint.getY() > lowerLeft.getY()) {
                    lowerLeft = lowerLeft.add(0, twPoint.getY() - lowerLeft.getY());
                }
                moveTransitions.add(lt);                
            } else {
                Text textNode = wordNodeFactory.createTextNode(word.getText());

                wordNodeFactory.fontSizeAdaption(textNode, TWEET_FONT_SIZE);
                tweetWordList.add(new TweetLayout.TweetWordNode(tweetWord, textNode));

                Bounds bounds = tweetWord.bounds;

                tweetLineOffset = tweetLayout.tweetWordLineOffset(bounds, lowerLeft, width, tweetLineOffset);
                Point2D twPoint = tweetLayout.layoutTweetWord(bounds, minPosTweetText, tweetLineOffset);

                textNode.setLayoutX(twPoint.getX());
                textNode.setLayoutY(twPoint.getY());
                if (twPoint.getY() > lowerLeft.getY()) {
                    lowerLeft = lowerLeft.add(0, twPoint.getY() - lowerLeft.getY());
                }
                textNode.setOpacity(0);
                pane.getChildren().add(textNode);
                FadeTransition ft = new FadeTransition(defaultDuration, textNode);
                ft.setToValue(1);
                fadeInTransitions.add(ft);
            }
        });

        // kill the remaining words from the cloud
        word2TextMap.entrySet().forEach(entry -> {
            Text textNode = entry.getValue();
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(0);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(textNode);
            });
            fadeOutTransitions.add(ft);
        });
        word2TextMap.clear();

        // layout image and meta data first
        hbox = new HBox(20);
        hbox.setStyle("-fx-padding: 20px;");
        hbox.setPrefHeight(80);
        hbox.setMaxHeight(80);
        hbox.setLayoutX(lowerLeft.getX());
        hbox.setLayoutY(lowerLeft.getY());

        HBox hImage = new HBox();
        hImage.setPadding(new Insets(10));

        Image profileImage = profileImageCache.get(tweetInfo.getUser().getProfileImageUrl());
//        Image profileImage = new Image(tweetInfo.getUser().getProfileImageUrl(), 64, 64, true, false);
        ImageView imageView = new ImageView(profileImage);
        Rectangle clip = new Rectangle(64, 64);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);
        hImage.getChildren().add(imageView);

        if (tweetInfo.isRetweet()) {
            FontAwesomeIcon retweetIconBack = new FontAwesomeIcon();
            retweetIconBack.getStyleClass().addAll("retweetBack");
            FontAwesomeIcon retweetIconFront = new FontAwesomeIcon();
            retweetIconFront.getStyleClass().addAll("retweetFront");
            
            GlyphsStack stackedIcon = GlyphsStack.create()
                    .add(retweetIconBack)
                    .add(retweetIconFront);
            hbox.getChildren().add(stackedIcon);
        }
//        HBox hName = new HBox(20);
        Label name = new Label(tweetInfo.getUser().getName());
        name.getStyleClass().setAll("name");

        Label handle = new Label("@" + tweetInfo.getUser().getScreenName() + " - " + df.format(tweetInfo.getCreatedAt()));
        handle.getStyleClass().setAll("handle");
        hbox.getChildren().addAll(hImage, name, handle);
        if (favIconsVisible) {
            if (0 < tweetInfo.getRetweetCount()) {
                FontAwesomeIcon faiReTwCount = new FontAwesomeIcon();
                faiReTwCount.getStyleClass().setAll("retweetCount");

                Label reTwCount = new Label(String.valueOf(tweetInfo.getRetweetCount()));
                reTwCount.getStyleClass().setAll("handle");
                hbox.getChildren().addAll(faiReTwCount, reTwCount);
            }
            if (0 < tweetInfo.getFavoriteCount()) {
                FontAwesomeIcon faiFavCount = new FontAwesomeIcon();
                faiFavCount.getStyleClass().setAll("favoriteCount");
                Label favCount = new Label(String.valueOf(tweetInfo.getFavoriteCount()));
                favCount.getStyleClass().setAll("handle");
                hbox.getChildren().addAll(faiFavCount, favCount);
            }
        }

        hbox.setOpacity(0);
        hbox.setAlignment(Pos.CENTER);
        pane.getChildren().add(hbox);

        if (tweetInfo.getMediaEntries().length > 0) {
//            System.out.println("Media detected: " + tweetInfo.getText() + " " + Arrays.toString(tweetInfo.getMediaEntities()));
            mediaBox = new HBox(10);
            mediaBox.setOpacity(0);
            mediaBox.setPadding(new Insets(10));
            mediaBox.setAlignment(Pos.CENTER_RIGHT);
            mediaBox.setLayoutX(logo.getImage().getWidth() + 10);
            mediaBox.setLayoutY(lowerLeft.getY() + 100);
            // ensure media box fills the complete area, so that layouting from right to left works
            mediaBox.setMinSize(layoutBounds.getWidth() - (logo.getImage().getWidth() + 80), Math.max(50, layoutBounds.getHeight() - (lowerLeft.getY() + 100)));
            mediaBox.setMaxSize(layoutBounds.getWidth() - (logo.getImage().getWidth() + 80), Math.max(50, layoutBounds.getHeight() - (lowerLeft.getY() + 100)));
            FadeTransition ft = new FadeTransition(defaultDuration, mediaBox);
            ft.setToValue(1);
            fadeInTransitions.add(ft);
            hImage.setPadding(new Insets(10));
            int imageCount = Math.min(3, tweetInfo.getMediaEntries().length);   //limit to maximum loading time of 3 images.
            for (int i = 0; i < imageCount; i++) {
                Image mediaImage = mediaImageCache.get(tweetInfo.getMediaEntries()[i].getMediaUrl());
    //            Image mediaImage = new Image(tweetInfo.getMediaEntries()[0].getMediaUrl());
                ImageView mediaView = new ImageView(mediaImage);
                mediaView.setPreserveRatio(true);
                mediaView.setCache(true);
                mediaView.setSmooth(true);
//            Rectangle clip = new Rectangle(64, 64);
//            clip.setArcWidth(10);
//            clip.setArcHeight(10);
//            imageView.setClip(clip);
                mediaView.setFitWidth((mediaBox.getMaxWidth() - 10) / imageCount);
                mediaView.setFitHeight(mediaBox.getMaxHeight() - 10);
                mediaBox.getChildren().add(mediaView);
            }
            // add fade in for image and meta data
            pane.getChildren().add(mediaBox);
        }

        // add fade in for image and meta data
        FadeTransition ft = new FadeTransition(defaultDuration, hbox);
        ft.setToValue(1);
        fadeInTransitions.add(ft);
        
        ParallelTransition fadeOuts = new ParallelTransition();
        ParallelTransition moves = new ParallelTransition();
        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        moves.getChildren().addAll(moveTransitions);
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        morph.play();
    }

    private void tweetToCloud() {
        Logger startupLogger = Logger.getLogger("org.tweetwallfx.startup");
        startupLogger.trace("tweetToCloud()");
        List<Word> sortedWords = new ArrayList<>(getSkinnable().wordsProperty().getValue());

        if (sortedWords.isEmpty()) {
            return;
        }

        limitedWords = sortedWords.stream().limit(displayCloudTags).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        WordleLayout.Configuration configuration = new WordleLayout.Configuration(limitedWords, font, MINIMUM_FONT_SIZE, MAX_FONT_SIZE, pane.getLayoutBounds());
        if (null != logo) {
            configuration.setBlockedAreaBounds(logo.getBoundsInParent());
        }
        
        WordleLayout cloudWordleLayout = WordleLayout.createWordleLayout(configuration);
        Duration defaultDuration = Duration.seconds(1.5);

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();
        
        Bounds layoutBounds = pane.getLayoutBounds();

        cloudWordleLayout.getWordLayoutInfo().entrySet().stream().forEach(entry -> {
            Word word = entry.getKey();
            Bounds bounds = entry.getValue();
            Optional<TweetLayout.TweetWordNode> optionalTweetWord = tweetWordList.stream().filter(tweetWord -> tweetWord.tweetWord.text.trim().equals(word.getText())).findFirst();
            if (optionalTweetWord.isPresent()) {
                boolean removed = tweetWordList.remove(optionalTweetWord.get());
                Text textNode = optionalTweetWord.get().textNode;

                word2TextMap.put(word, textNode);
                LocationTransition lt = new LocationTransition(defaultDuration, textNode);

                lt.setFromX(textNode.getLayoutX());
                lt.setFromY(textNode.getLayoutY());
                lt.setToX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
                lt.setToY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
                moveTransitions.add(lt);

                FontSizeTransition ft = new FontSizeTransition(defaultDuration, textNode);
                ft.setFromSize(textNode.getFont().getSize());
                ft.setToSize(cloudWordleLayout.getFontSizeForWeight(word.getWeight()));
                moveTransitions.add(ft);

            } else {
                Text textNode = cloudWordleLayout.createTextNode(word);

                word2TextMap.put(word, textNode);
                textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
                textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
                textNode.setOpacity(0);
                pane.getChildren().add(textNode);
                FadeTransition ft = new FadeTransition(defaultDuration, textNode);
                ft.setToValue(1);
                fadeInTransitions.add(ft);
            }
        });

        tweetWordList.forEach(tweetWord -> {
            FadeTransition ft = new FadeTransition(defaultDuration, tweetWord.textNode);
            ft.setToValue(0);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(tweetWord.textNode);
            });
            fadeOutTransitions.add(ft);
        });

        tweetWordList.clear();

        if (null != hbox) {
            Node hboxNode = hbox;
            FadeTransition ft = new FadeTransition(defaultDuration, hboxNode);
            ft.setToValue(0);
            ft.setOnFinished(event -> {
                pane.getChildren().remove(hboxNode);
            });
            fadeOutTransitions.add(ft);
        }
        if (null != mediaBox) {
            Node mediaBoxNode = mediaBox;
            FadeTransition ft = new FadeTransition(defaultDuration, mediaBoxNode);
            ft.setToValue(0);
            ft.setOnFinished(event -> {
                pane.getChildren().remove(mediaBoxNode);
            });
            fadeOutTransitions.add(ft);
        }
        
        ParallelTransition fadeOuts = new ParallelTransition();
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        ParallelTransition moves = new ParallelTransition();
        moves.getChildren().addAll(moveTransitions);
        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        morph.play();
    }

    private void updateCloud() {
//        pane.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
        List<Word> sortedWords = new ArrayList<>(getSkinnable().wordsProperty().getValue());
        if (sortedWords.isEmpty()) {
            return;
        }

        Bounds layoutBounds = pane.getLayoutBounds();
        
        limitedWords = sortedWords.stream().limit(displayCloudTags).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        WordleLayout.Configuration configuration = new WordleLayout.Configuration(limitedWords, font, MINIMUM_FONT_SIZE, MAX_FONT_SIZE, pane.getLayoutBounds());
        if (null != logo) {
            configuration.setBlockedAreaBounds(logo.getBoundsInParent());
        }
        
        WordleLayout cloudWordleLayout = WordleLayout.createWordleLayout(configuration);
        
        List<Word> unusedWords = word2TextMap.keySet().stream().filter(word -> !cloudWordleLayout.getWordLayoutInfo().containsKey(word)).collect(Collectors.toList());
        
        Duration defaultDuration = Duration.seconds(1.5);

        SequentialTransition morph = new SequentialTransition();

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();
        
        unusedWords.forEach(word -> {
            Text textNode = word2TextMap.remove(word);

            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(0);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(textNode);
            });
            fadeOutTransitions.add(ft);
        });

        ParallelTransition fadeOuts = new ParallelTransition();
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        morph.getChildren().add(fadeOuts);

        List<Word> existingWords = cloudWordleLayout.getWordLayoutInfo().keySet().stream().filter(word -> word2TextMap.containsKey(word)).collect(Collectors.toList());

        existingWords.forEach(word -> {

            Text textNode = word2TextMap.get(word);
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

        List<Word> newWords = cloudWordleLayout.getWordLayoutInfo().keySet().stream().filter(word -> !word2TextMap.containsKey(word)).collect(Collectors.toList());

        List<Text> newTextNodes = new ArrayList<>();
        newWords.forEach(word -> {
            Text textNode = cloudWordleLayout.createTextNode(word);
            word2TextMap.put(word, textNode);

            Bounds bounds = cloudWordleLayout.getWordLayoutInfo().get(word);
            textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            textNode.setOpacity(0);
            newTextNodes.add(textNode);
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(1);
            fadeInTransitions.add(ft);
        });
        pane.getChildren().addAll(newTextNodes);
        
        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        morph.getChildren().add(fadeIns);
        morph.play();
    }
    
    private static final int TWEET_FONT_SIZE = 54;
    private static final int MINIMUM_FONT_SIZE = 36;
    private static final int MAX_FONT_SIZE = 72;
}