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
package org.tweetwallfx.controls;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.geometry.BoundingBox;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * @author sven
 */
public class WordleSkin extends SkinBase<Wordle> {

    private final Random rand = new Random();
    private static final int dDeg = 10;
    private static final double dRadius = 5.0;

    private final Map<Word, Text> word2TextMap = new HashMap<>();
    // used for Tweet Display
    private final List<TweetWordNode> tweetWordList = new ArrayList<>();
    private double max;
    private double min;
    private final Pane pane;
    private List<Word> limitedWords;
    private HBox hbox;
    private Point2D lowerLeft;
    private HBox mediaBox;

    private ImageView logo;
    private final Boolean favIconsVisible;

    public WordleSkin(Wordle wordle) {
        super(wordle);
        pane = new Pane();
        pane.getStylesheets().add(this.getClass().getResource("wordle.css").toExternalForm());

        getSkinnable().logoProperty().addListener((obs, oldValue, newValue) -> {
            if (null != logo) {
                pane.getChildren().remove(logo);
            }
            System.out.println("Logo: " + newValue);
            logo = new ImageView(newValue);

            pane.getChildren().add(logo);
            logo.setLayoutX(0);
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
        });

        System.out.println("LOGO: " + getSkinnable().logoProperty().getValue());

        logo = new ImageView(getSkinnable().logoProperty().getValue());
        logo.getStyleClass().setAll("logo");

        pane.getChildren().add(logo);
        logo.setLayoutX(0);
        logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());

//        pane.setStyle("-fx-border-width: 1px; -fx-border-color: black;");
        this.getChildren().add(pane);
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

    }

    private Point2D tweetWordLineOffset(Bounds targetBounds, Point2D upperLeft, double maxWidth, Point2D lineOffset) {
        double x = upperLeft.getX() + targetBounds.getMinX() - lineOffset.getX();
        double rightMargin = upperLeft.getX() + maxWidth;
        if (x + targetBounds.getWidth() > rightMargin) {
            return new Point2D(lineOffset.getX() + (x - upperLeft.getX()), lineOffset.getY() + targetBounds.getHeight());
        }
        return lineOffset;
    }

    private Point2D layoutTweetWord(Bounds targetBounds, Point2D upperLeft, Point2D lineOffset) {
        double y = upperLeft.getY() + targetBounds.getMinY() + lineOffset.getY();
        double x = upperLeft.getX() + targetBounds.getMinX() - lineOffset.getX();
        return new Point2D(x, y);
    }

    Point2D tweetLineOffset = new Point2D(0, 0);

    private void cloudToTweet() {

        Bounds layoutBounds = pane.getLayoutBounds();
        Tweet tweetInfo = getSkinnable().tweetInfoProperty.get();

        Point2D minPosTweetText = new Point2D(layoutBounds.getWidth() / 6d, (layoutBounds.getHeight() - logo.getImage().getHeight()) / 4d);

        double width = layoutBounds.getWidth() * (2 / 3d);

        List<TweetWord> tweetLayout = recalcTweetLayout(tweetInfo);

        ParallelTransition fadeOuts = new ParallelTransition();
        ParallelTransition moves = new ParallelTransition();
        ParallelTransition fadeIns = new ParallelTransition();
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        lowerLeft = new Point2D(minPosTweetText.getX(), minPosTweetText.getY());
        tweetLineOffset = new Point2D(0, 0);

        tweetLayout.stream().forEach(tweetWord -> {
            Word word = new Word(tweetWord.text.trim(), -2);
            if (word2TextMap.containsKey(word)) {
                Text textNode = word2TextMap.remove(word);
                tweetWordList.add(new TweetWordNode(tweetWord, textNode));

                FontSizeTransition ft = new FontSizeTransition(Duration.seconds(1.5), textNode);
                ft.setFromSize(textNode.getFont().getSize());
                ft.setToSize(getFontSize(-1));
                moves.getChildren().add(ft);

                Bounds bounds = tweetLayout.stream().filter(tw -> tw.text.trim().equals(word.getText())).findFirst().get().bounds;

                LocationTransition lt = new LocationTransition(Duration.seconds(1.5), textNode);
                lt.setFromX(textNode.getLayoutX());
                lt.setFromY(textNode.getLayoutY());
                tweetLineOffset = tweetWordLineOffset(bounds, lowerLeft, width, tweetLineOffset);
                Point2D twPoint = layoutTweetWord(bounds, minPosTweetText, tweetLineOffset);
                lt.setToX(twPoint.getX());
                lt.setToY(twPoint.getY());
                if (twPoint.getY() > lowerLeft.getY()) {
                    lowerLeft = lowerLeft.add(0, twPoint.getY() - lowerLeft.getY());
                }
                moves.getChildren().add(lt);
            } else {
                Text textNode = createTextNode(word);

                fontSizeAdaption(textNode, -1);
                tweetWordList.add(new TweetWordNode(tweetWord, textNode));

                Bounds bounds = tweetWord.bounds;

                tweetLineOffset = tweetWordLineOffset(bounds, lowerLeft, width, tweetLineOffset);
                Point2D twPoint = layoutTweetWord(bounds, minPosTweetText, tweetLineOffset);

                textNode.setLayoutX(twPoint.getX());
                textNode.setLayoutY(twPoint.getY());
                if (twPoint.getY() > lowerLeft.getY()) {
                    lowerLeft = lowerLeft.add(0, twPoint.getY() - lowerLeft.getY());
                }
                textNode.setOpacity(0);
                pane.getChildren().add(textNode);
                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), textNode);
                ft.setToValue(1);
                fadeIns.getChildren().add(ft);
            }
        });

        // kill the remaining words from the cloud
        word2TextMap.entrySet().forEach(entry -> {
            Text textNode = entry.getValue();
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), textNode);
            ft.setToValue(0);
            fadeOuts.getChildren().add(ft);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(textNode);
            });
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
        Image image = new Image(tweetInfo.getUser().getProfileImageUrl(), 64, 64, true, false);
        ImageView imageView = new ImageView(image);
        Rectangle clip = new Rectangle(64, 64);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);
        hImage.getChildren().add(imageView);

//        HBox hName = new HBox(20);
        Label name = new Label(tweetInfo.getUser().getName());
        name.getStyleClass().setAll("name");

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Label handle = new Label("@" + tweetInfo.getUser().getScreenName() + " · " + df.format(tweetInfo.getCreatedAt()));
        handle.getStyleClass().setAll("handle");
        hbox.getChildren().addAll(hImage, name, handle);
        if (favIconsVisible) {
            FontAwesomeIcon faiFavCount = new FontAwesomeIcon();
            faiFavCount.getStyleClass().setAll("favoriteCount");
            FontAwesomeIcon faiReTwCount = new FontAwesomeIcon();
            faiReTwCount.getStyleClass().setAll("retweetCount");

            Label favCount = new Label(String.valueOf(tweetInfo.getFavoriteCount()));
            favCount.getStyleClass().setAll("handle");

            Label reTwCount = new Label(String.valueOf(tweetInfo.getRetweetCount()));
            reTwCount.getStyleClass().setAll("handle");

            hbox.getChildren().addAll(faiReTwCount, reTwCount, faiFavCount, favCount);
        }

        hbox.setOpacity(0);
        hbox.setAlignment(Pos.CENTER);
        pane.getChildren().add(hbox);

        if (tweetInfo.getMediaEntries().length > 0) {
//            System.out.println("Media detected: " + tweetInfo.getText() + " " + Arrays.toString(tweetInfo.getMediaEntities()));
            mediaBox = new HBox();
            hImage.setPadding(new Insets(10));
            Image mediaImage = new Image(tweetInfo.getMediaEntries()[0].getMediaUrl());
            ImageView mediaView = new ImageView(mediaImage);
            mediaView.setPreserveRatio(true);
            mediaView.setCache(true);
            mediaView.setSmooth(true);
//            Rectangle clip = new Rectangle(64, 64);
//            clip.setArcWidth(10);
//            clip.setArcHeight(10);
//            imageView.setClip(clip);
            mediaBox.getChildren().add(mediaView);
            mediaBox.setOpacity(0);
            mediaBox.setLayoutX(layoutBounds.getWidth() / 2d);
            mediaBox.setLayoutY(lowerLeft.getY() + 100);
            mediaBox.setMaxSize(layoutBounds.getWidth() / 2d, layoutBounds.getHeight() - 50);
            mediaView.setFitWidth(mediaBox.getWidth() - 10);
            mediaView.setFitHeight(mediaBox.getHeight() - 10);
            // add fade in for image and meta data
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), mediaBox);
            ft.setToValue(1);
            fadeIns.getChildren().add(ft);
            pane.getChildren().add(mediaBox);
        }

        // add fade in for image and meta data
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), hbox);
        ft.setToValue(1);
        fadeIns.getChildren().add(ft);

        morph.play();
    }

    private void tweetToCloud() {
        List<Word> sortedWords = new ArrayList<>(getSkinnable().wordsProperty().getValue());

        if (sortedWords.isEmpty()) {
            return;
        }

        limitedWords = sortedWords.stream().limit(MAX_CLOUD_TAGS).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        max = limitedWords.get(0).getWeight();
        min = limitedWords.stream().filter(w -> w.getWeight() > 0).min(Comparator.naturalOrder()).get().getWeight();

        Map<Word, Bounds> boundsMap = recalcTagLayout(limitedWords);

        ParallelTransition fadeOuts = new ParallelTransition();
        ParallelTransition moves = new ParallelTransition();
        ParallelTransition fadeIns = new ParallelTransition();
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        Bounds layoutBounds = pane.getLayoutBounds();

        boundsMap.entrySet().stream().forEach(entry -> {
            Word word = entry.getKey();
            Bounds bounds = entry.getValue();
            Optional<TweetWordNode> optionalTweetWord = tweetWordList.stream().filter(tweetWord -> tweetWord.tweetWord.text.trim().equals(word.getText())).findFirst();
            if (optionalTweetWord.isPresent()) {
                boolean removed = tweetWordList.remove(optionalTweetWord.get());
                Text textNode = optionalTweetWord.get().textNode;

                word2TextMap.put(word, textNode);
                LocationTransition lt = new LocationTransition(Duration.seconds(1.5), textNode);

                lt.setFromX(textNode.getLayoutX());
                lt.setFromY(textNode.getLayoutY());
                lt.setToX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
                lt.setToY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
                moves.getChildren().add(lt);

                FontSizeTransition ft = new FontSizeTransition(Duration.seconds(1.5), textNode);
                ft.setFromSize(textNode.getFont().getSize());
                ft.setToSize(getFontSize(word.getWeight()));
                moves.getChildren().add(ft);

            } else {
                Text textNode = createTextNode(word);

                word2TextMap.put(word, textNode);
                textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
                textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
                textNode.setOpacity(0);
                pane.getChildren().add(textNode);
                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), textNode);
                ft.setToValue(1);
                fadeIns.getChildren().add(ft);
            }
        });

        tweetWordList.forEach(tweetWord -> {
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), tweetWord.textNode);
            ft.setToValue(0);
            fadeOuts.getChildren().add(ft);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(tweetWord.textNode);
            });
        });

        tweetWordList.clear();

        if (null != hbox) {
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), hbox);
            ft.setToValue(0);
            fadeOuts.getChildren().add(ft);
            ft.setOnFinished(event -> {
                pane.getChildren().remove(hbox);
            });
        }
        if (null != mediaBox) {
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), mediaBox);
            ft.setToValue(0);
            fadeOuts.getChildren().add(ft);
            ft.setOnFinished(event -> {
                pane.getChildren().remove(mediaBox);
            });
        }

        morph.play();
    }

    private void updateCloud() {
//        pane.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
        List<Word> sortedWords = new ArrayList<>(getSkinnable().wordsProperty().getValue());
        if (sortedWords.isEmpty()) {
            return;
        }

        limitedWords = sortedWords.stream().limit(MAX_CLOUD_TAGS).collect(Collectors.toList());
        limitedWords.sort(Comparator.reverseOrder());

        max = limitedWords.get(0).getWeight();
        min = limitedWords.stream().filter(w -> w.getWeight() > 0).min(Comparator.naturalOrder()).get().getWeight();

        Map<Word, Bounds> boundsMap = recalcTagLayout(limitedWords);
        Bounds layoutBounds = pane.getLayoutBounds();

        List<Word> unusedWords = word2TextMap.keySet().stream().filter(word -> !boundsMap.containsKey(word)).collect(Collectors.toList());
        SequentialTransition morph = new SequentialTransition();

        ParallelTransition fadeOuts = new ParallelTransition();
        unusedWords.forEach(word -> {
            Text textNode = word2TextMap.remove(word);

            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), textNode);
            ft.setToValue(0);
            fadeOuts.getChildren().add(ft);
            ft.setOnFinished((event) -> {
                pane.getChildren().remove(textNode);
            });
        });

        morph.getChildren().add(fadeOuts);

        List<Word> existingWords = boundsMap.keySet().stream().filter(word -> word2TextMap.containsKey(word)).collect(Collectors.toList());
        ParallelTransition moves = new ParallelTransition();

        existingWords.forEach(word -> {

            Text textNode = word2TextMap.get(word);
            fontSizeAdaption(textNode, word.getWeight());
            Bounds bounds = boundsMap.get(word);

            LocationTransition lt = new LocationTransition(Duration.seconds(1.5), textNode);
            lt.setFromX(textNode.getLayoutX());
            lt.setFromY(textNode.getLayoutY());
            lt.setToX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            lt.setToY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            moves.getChildren().add(lt);
        });

        morph.getChildren().add(moves);

        List<Word> newWords = boundsMap.keySet().stream().filter(word -> !word2TextMap.containsKey(word)).collect(Collectors.toList());
        ParallelTransition fadeIns = new ParallelTransition();

        newWords.forEach(word -> {
            Text textNode = createTextNode(word);
            word2TextMap.put(word, textNode);

            Bounds bounds = boundsMap.get(word);
            textNode.setLayoutX(bounds.getMinX() + layoutBounds.getWidth() / 2d);
            textNode.setLayoutY(bounds.getMinY() + layoutBounds.getHeight() / 2d + bounds.getHeight() / 2d);
            textNode.setOpacity(0);
            pane.getChildren().add(textNode);
            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), textNode);
            ft.setToValue(1);
            fadeIns.getChildren().add(ft);
        });
        morph.getChildren().add(fadeIns);
        morph.play();
    }

    private static final int MAX_CLOUD_TAGS = 50;

    private final Font defaultFont = Font.font("Calibri", FontWeight.BOLD, MINIMUM_FONT_SIZE);

    private double getFontSize(double weight) {
        // maxFont = 48
        // minFont = 18

        double size;
        if (weight == -1) {
            size = TWEET_FONT_SIZE;
        } else if (weight == -2) {
            size = MINIMUM_FONT_SIZE - 10;
        } else {
            // linear
            //y = a+bx
//        double size = defaultFont.getSize() + ((48-defaultFont.getSize())/(max-min)) * word.weight;
            // logarithmic
            // y = a * Math.ln(x) + b
            double a = (defaultFont.getSize() - MAX_FONT_SIZE) / (Math.log(min / max));
            double b = defaultFont.getSize() - a * Math.log(min);
            size = a * Math.log(weight) + b;
        }
        return size;
    }

    private void fontSizeAdaption(Text text, double weight) {
        text.setFont(Font.font(defaultFont.getFamily(), getFontSize(weight)));
    }

    private Text createTextNode(Word word) {
        Text textNode = new Text(word.getText());
        textNode.getStyleClass().setAll("tag");
        textNode.setStyle("-fx-padding: 5px");
        fontSizeAdaption(textNode, word.getWeight());
        return textNode;
    }

    private final Pattern pattern = Pattern.compile("\\s+");

    private List<TweetWord> recalcTweetLayout(Tweet info) {
        TextFlow flow = new TextFlow();
        flow.setMaxWidth(300);
        pattern.splitAsStream(info.getText())
                .forEach(w -> {
                    Text textWord = new Text(w.concat(" "));
                    textWord.getStyleClass().setAll("tag");
                    String color = "#292F33";
//                    textWord.setStyle("-fx-fill: " + color + ";");
                    textWord.setFont(Font.font(defaultFont.getFamily(), TWEET_FONT_SIZE));
                    flow.getChildren().add(textWord);
                });
        flow.requestLayout();
        return flow.getChildren().stream().map(node -> new TweetWord(node.getBoundsInParent(), ((Text) node).getText())).collect(Collectors.toList());
    }
    private static final int TWEET_FONT_SIZE = 72;
    private static final int MINIMUM_FONT_SIZE = 36;
    private static final int MAX_FONT_SIZE = 72;

    private Map<Word, Bounds> recalcTagLayout(List<Word> words) {
        boolean doFinish = false;
        Bounds layoutBounds = pane.getLayoutBounds();
        Bounds logoLayout = logo.getBoundsInParent();
        Bounds logoBounds = new BoundingBox(logoLayout.getMinX() - layoutBounds.getWidth() / 2d,
                logoLayout.getMinY() - layoutBounds.getHeight() / 2d,
                logoLayout.getWidth(),
                logoLayout.getHeight());

        List<Bounds> boundsList = new ArrayList<>();
        Text firstNode = createTextNode(words.get(0));
        double firstWidth = firstNode.getLayoutBounds().getWidth();
        double firstHeight = firstNode.getLayoutBounds().getHeight();

        boundsList.add(new BoundingBox(-firstWidth / 2d,
                -firstHeight / 2d, firstWidth, firstHeight));

        for (int i = 1; i < words.size(); ++i) {
            Word word = words.get(i);
            Text textNode = createTextNode(word);
            double width = textNode.getLayoutBounds().getWidth();
            double height = textNode.getLayoutBounds().getHeight();

            Point2D center = new Point2D(0, 0);
            double totalWeight = 0.0;
            for (int prev = 0; prev < i; ++prev) {
                Bounds prevBounds = boundsList.get(prev);
                double weight = words.get(prev).getWeight();
                center = center.add((prevBounds.getWidth() / 2d) * weight, (prevBounds.getHeight() / 2d) * weight);
                totalWeight += weight;
            }
            center = center.multiply(1d / totalWeight);
            boolean done = false;
            double radius = 0.5 * Math.min(boundsList.get(0).getWidth(), boundsList.get(0).getHeight());
            while (!done) {
                if (radius > Math.max(layoutBounds.getHeight(), layoutBounds.getWidth())) {
                    doFinish = true;
                }
                int startDeg = rand.nextInt(360);
                double prev_x = -1;
                double prev_y = -1;
                for (int deg = startDeg; deg < startDeg + 360; deg += dDeg) {
                    double rad = ((double) deg / Math.PI) * 180.0;
                    center = center.add(radius * Math.cos(rad), radius * Math.sin(rad));
                    if (prev_x == center.getX() && prev_y == center.getY()) {
                        continue;
                    }
                    prev_x = center.getX();
                    prev_y = center.getY();
                    Bounds mayBe = new BoundingBox(center.getX() - width / 2d,
                            center.getY() - height / 2d, width, height);
                    boolean useable = true;
                    //check if bounds are full on screen:
                    if (layoutBounds.getWidth() > 0 && layoutBounds.getHeight() > 0 && (mayBe.getMinX() + layoutBounds.getWidth() / 2d < 0
                            || mayBe.getMinY() + layoutBounds.getHeight() / 2d < 0
                            || mayBe.getMaxX() + layoutBounds.getWidth() / 2d > layoutBounds.getMaxX()
                            || mayBe.getMaxY() + layoutBounds.getHeight() / 2d > layoutBounds.getMaxY())) {
                        useable = false;
                    }
                    for (int prev = 0; prev < i; ++prev) {
                        if (mayBe.intersects(boundsList.get(prev)) || (null != logo && mayBe.intersects(logoBounds))) {
                            useable = false;
                            break;
                        }
                    }
                    if (useable || doFinish) {
                        done = true;
                        boundsList.add(new BoundingBox(center.getX() - width / 2d,
                                center.getY() - height / 2d, width, height));
                        break;
                    }
                }
                radius += WordleSkin.dRadius;
            }
        }

        Map<Word, Bounds> boundsMap = new HashMap<>();

        for (int k = 0; k < words.size(); k++) {
            boundsMap.put(words.get(k), boundsList.get(k));
        }
        return boundsMap;
    }

    private static class LocationTransition extends Transition {

        private final Node node;
        private double startX;
        private double startY;
        private double targetY;
        private double targetX;

        public LocationTransition(Duration duration, Node node) {
            setCycleDuration(duration);
            this.node = node;
        }

        public void setFromX(double startX) {
            this.startX = startX;
        }

        public void setFromY(double startY) {
            this.startY = startY;
        }

        public void setToX(double targetX) {
            this.targetX = targetX;
        }

        public void setToY(double targetY) {
            this.targetY = targetY;
        }

        @Override
        protected void interpolate(double frac) {
            if (!Double.isNaN(startX)) {
                node.setLayoutX(startX + frac * (targetX - startX));
            }
            if (!Double.isNaN(startY)) {
                node.setLayoutY(startY + frac * (targetY - startY));
            }
        }

    }

    private static class FontSizeTransition extends Transition {

        private final Text node;
        private double startSize;
        private double toSize;

        public FontSizeTransition(Duration duration, Text node) {
            setCycleDuration(duration);
            this.node = node;
        }

        public void setFromSize(double startSize) {
            this.startSize = startSize;
        }

        public void setToSize(double toSize) {
            this.toSize = toSize;
        }

        @Override
        protected void interpolate(double frac) {
            if (!Double.isNaN(startSize)) {
                node.setFont(Font.font(node.getFont().getFamily(), startSize + frac * (toSize - startSize)));
            }
        }

    }

    private static class TweetWord {

        Bounds bounds;
        String text;

        public TweetWord(Bounds bounds, String text) {
            this.bounds = bounds;
            this.text = text;
        }

        @Override
        public String toString() {
            return "TweetWord{" + "text=" + text + ", bounds=" + bounds + '}';
        }

    }

    private static class TweetWordNode {

        private TweetWord tweetWord;
        private Text textNode;

        public TweetWordNode(TweetWord tweetWord, Text textNode) {
            this.tweetWord = tweetWord;
            this.textNode = textNode;
        }

    }

}
