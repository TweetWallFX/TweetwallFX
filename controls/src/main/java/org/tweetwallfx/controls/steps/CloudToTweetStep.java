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
import de.jensd.fx.glyphs.GlyphsStack;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.tweetwallfx.controls.TweetLayout;
import org.tweetwallfx.controls.TweetWordNodeFactory;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.dataprovider.TweetDataProvider;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.transition.FontSizeTransition;
import org.tweetwallfx.controls.transition.LocationTransition;
import org.tweetwallfx.tweet.api.Tweet;

/**
 *
 * @author Jörg Michelberger
 */
public class CloudToTweetStep extends AbstractStep {
    //TODO: push this attributes into doStep!
    private Point2D lowerLeft;  //OMG, how can this be piped through a lambda?
    private Point2D tweetLineOffset;  //OMG, how can this be piped through a lambda?
    
    @Override
    public long preferredStepDuration(MachineContext context) {
        return 5000;
    }

    @Override
    public void doStep(MachineContext context) {
//        context.getWordle().setLayoutMode(Wordle.LayoutMode.WORDLE);
        Logger startupLogger = Logger.getLogger("org.tweetwallfx.startup");
        
        WordleSkin wordleSkin = (WordleSkin)context.get("WordleSkin");
//        Wordle wordle = (Wordle)context.get("Wordle");
        
        startupLogger.trace("cloudToTweet()");
        Bounds layoutBounds = wordleSkin.getPane().getLayoutBounds();
        Tweet displayTweet = wordleSkin.getSkinnable().getDataProvider(TweetDataProvider.class).getTweet();

        Tweet originalTweet = displayTweet;
        while (originalTweet.isRetweet()) {
            originalTweet = originalTweet.getRetweetedTweet();
        }
        
        Point2D minPosTweetText = new Point2D(layoutBounds.getWidth() / 6d, (layoutBounds.getHeight() - wordleSkin.getLogo().getImage().getHeight()) / 4d);

        double width = layoutBounds.getWidth() * (2 / 3d);

        TweetLayout tweetLayout = TweetLayout.createTweetLayout(new TweetLayout.Configuration(originalTweet, wordleSkin.getFont(), wordleSkin.getTweetFontSize()));       

        List<Transition> fadeOutTransitions = new ArrayList<>();
        List<Transition> moveTransitions = new ArrayList<>();
        List<Transition> fadeInTransitions = new ArrayList<>();
        
        lowerLeft = new Point2D(minPosTweetText.getX(), minPosTweetText.getY());
        tweetLineOffset = new Point2D(0, 0);

        Duration defaultDuration = Duration.seconds(1.5);

        TweetWordNodeFactory wordNodeFactory = TweetWordNodeFactory.createFactory(new TweetWordNodeFactory.Configuration(wordleSkin.getFont(), wordleSkin.getTweetFontSize()));
        
        tweetLayout.getWordLayoutInfo().stream().forEach(tweetWord -> {
            Word word = new Word(tweetWord.text.trim(), -2);
            if (wordleSkin.word2TextMap.containsKey(word)) {
                Text textNode = wordleSkin.word2TextMap.remove(word);
                wordleSkin.tweetWordList.add(new TweetLayout.TweetWordNode(tweetWord, textNode));

                FontSizeTransition ft = new FontSizeTransition(defaultDuration, textNode);
                ft.setFromSize(textNode.getFont().getSize());
                ft.setToSize(wordleSkin.getTweetFontSize());
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

                wordNodeFactory.fontSizeAdaption(textNode, wordleSkin.getTweetFontSize());
                wordleSkin.tweetWordList.add(new TweetLayout.TweetWordNode(tweetWord, textNode));

                Bounds bounds = tweetWord.bounds;

                tweetLineOffset = tweetLayout.tweetWordLineOffset(bounds, lowerLeft, width, tweetLineOffset);
                Point2D twPoint = tweetLayout.layoutTweetWord(bounds, minPosTweetText, tweetLineOffset);

                textNode.setLayoutX(twPoint.getX());
                textNode.setLayoutY(twPoint.getY());
                if (twPoint.getY() > lowerLeft.getY()) {
                    lowerLeft = lowerLeft.add(0, twPoint.getY() - lowerLeft.getY());
                }
                textNode.setOpacity(0);
                wordleSkin.getPane().getChildren().add(textNode);
                FadeTransition ft = new FadeTransition(defaultDuration, textNode);
                ft.setToValue(1);
                fadeInTransitions.add(ft);
            }
        });

        // kill the remaining words from the cloud
        wordleSkin.word2TextMap.entrySet().forEach(entry -> {
            Text textNode = entry.getValue();
            FadeTransition ft = new FadeTransition(defaultDuration, textNode);
            ft.setToValue(0);
            ft.setOnFinished((event) -> {
                wordleSkin.getPane().getChildren().remove(textNode);
            });
            fadeOutTransitions.add(ft);
        });
        wordleSkin.word2TextMap.clear();

        // layout image and meta data first
        GridPane infoBox = new GridPane();
        infoBox.setStyle("-fx-padding: 20px;");
        infoBox.setPrefHeight(80);
        infoBox.setMaxHeight(80);
        infoBox.setLayoutX(lowerLeft.getX());
        infoBox.setLayoutY(lowerLeft.getY());

        HBox hImage = new HBox();
        hImage.setPadding(new Insets(10));

        
        Image profileImage = wordleSkin.getProfileImageCache().get(originalTweet.getUser().getProfileImageUrl());
        ImageView imageView = new ImageView(profileImage);
        Rectangle clip = new Rectangle(64, 64);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);
        hImage.getChildren().add(imageView);
        
        Label name = new Label(originalTweet.getUser().getName());
        name.getStyleClass().setAll("name");

        Label handle = new Label("@" + originalTweet.getUser().getScreenName() + " - " + wordleSkin.getDf().format(originalTweet.getCreatedAt()));
        handle.getStyleClass().setAll("handle");
        HBox firstLineBox = new HBox();
        HBox secondLineBox = new HBox();
        secondLineBox.getChildren().add(name);
        HBox thirdLineBox = new HBox();
        thirdLineBox.getChildren().add(handle);
        if (originalTweet.getUser().isVerified()) {
            FontAwesomeIcon verifiedIcon = new FontAwesomeIcon();
            verifiedIcon.getStyleClass().addAll("verifiedAccount");
            secondLineBox.getChildren().add(verifiedIcon);
            HBox.setMargin(verifiedIcon, new Insets(9,10,0,5));
        } 
        if (displayTweet.isRetweet()) {
            FontAwesomeIcon retweetIconBack = new FontAwesomeIcon();
            retweetIconBack.getStyleClass().addAll("retweetBack");
            FontAwesomeIcon retweetIconFront = new FontAwesomeIcon();
            retweetIconFront.getStyleClass().addAll("retweetFront");
            
            Label retweetName = new Label(displayTweet.getUser().getName());
            retweetName.getStyleClass().setAll("retweetName");
            
            GlyphsStack stackedIcon = GlyphsStack.create()
                    .add(retweetIconBack)
                    .add(retweetIconFront);
            firstLineBox.getChildren().addAll(stackedIcon, retweetName);
            HBox.setMargin(stackedIcon, new Insets(0,10,0,0));
        }
        infoBox.getChildren().addAll(hImage, firstLineBox, secondLineBox, thirdLineBox);
        GridPane.setConstraints(hImage, 0, 1, 1, 2);
        GridPane.setConstraints(firstLineBox, 1, 0);
        GridPane.setConstraints(secondLineBox, 1, 1);
        GridPane.setConstraints(thirdLineBox, 1, 2);
        
        if (wordleSkin.getFavIconsVisible()) {
            if (0 < originalTweet.getRetweetCount()) {
                FontAwesomeIcon faiReTwCount = new FontAwesomeIcon();
                faiReTwCount.getStyleClass().setAll("retweetCount");

                Label reTwCount = new Label(String.valueOf(originalTweet.getRetweetCount()));
                reTwCount.getStyleClass().setAll("handle");
                thirdLineBox.getChildren().addAll(faiReTwCount, reTwCount);
                HBox.setMargin(faiReTwCount, new Insets(5,10,0,5));
            }
            if (0 < originalTweet.getFavoriteCount()) {
                FontAwesomeIcon faiFavCount = new FontAwesomeIcon();
                faiFavCount.getStyleClass().setAll("favoriteCount");
                Label favCount = new Label(String.valueOf(originalTweet.getFavoriteCount()));
                favCount.getStyleClass().setAll("handle");
                thirdLineBox.getChildren().addAll(faiFavCount, favCount);
                HBox.setMargin(faiFavCount, new Insets(5,10,0,5));
            }
        }

        infoBox.setOpacity(0);
        infoBox.setAlignment(Pos.CENTER);
        wordleSkin.setInfoBox(infoBox);
        wordleSkin.getPane().getChildren().add(infoBox);

        if (originalTweet.getMediaEntries().length > 0) {
//            System.out.println("Media detected: " + tweetInFocus.getText() + " " + Arrays.toString(tweetInFocus.getMediaEntities()));
            HBox mediaBox = new HBox(10);
            mediaBox.setOpacity(0);
            mediaBox.setPadding(new Insets(10));
            mediaBox.setAlignment(Pos.CENTER_RIGHT);
            mediaBox.setLayoutX(wordleSkin.getLogo().getImage().getWidth() + 10);
            mediaBox.setLayoutY(lowerLeft.getY() + 100);
            // ensure media box fills the complete area, so that layouting from right to left works
            mediaBox.setMinSize(layoutBounds.getWidth() - (wordleSkin.getLogo().getImage().getWidth() + 80), Math.max(50, layoutBounds.getHeight() - (lowerLeft.getY() + 100)));
            mediaBox.setMaxSize(layoutBounds.getWidth() - (wordleSkin.getLogo().getImage().getWidth() + 80), Math.max(50, layoutBounds.getHeight() - (lowerLeft.getY() + 100)));
            FadeTransition ft = new FadeTransition(defaultDuration, mediaBox);
            ft.setToValue(1);
            fadeInTransitions.add(ft);
            hImage.setPadding(new Insets(10, 10, 10, 10));
            int imageCount = Math.min(3, originalTweet.getMediaEntries().length);   //limit to maximum loading time of 3 images.
            for (int i = 0; i < imageCount; i++) {
                Image mediaImage = wordleSkin.getMediaImageCache().get(originalTweet.getMediaEntries()[i].getMediaUrl());
    //            Image mediaImage = new Image(tweetInFocus.getMediaEntries()[0].getMediaUrl());
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
            wordleSkin.setMediaBox(mediaBox);
            // add fade in for image and meta data
            wordleSkin.getPane().getChildren().add(mediaBox);
        }

        // add fade in for image and meta data
        FadeTransition ft = new FadeTransition(defaultDuration, infoBox);
        ft.setToValue(1);
        fadeInTransitions.add(ft);
        
        ParallelTransition fadeOuts = new ParallelTransition();
        ParallelTransition moves = new ParallelTransition();
        ParallelTransition fadeIns = new ParallelTransition();
        fadeIns.getChildren().addAll(fadeInTransitions);
        moves.getChildren().addAll(moveTransitions);
        fadeOuts.getChildren().addAll(fadeOutTransitions);
        SequentialTransition morph = new SequentialTransition(fadeOuts, moves, fadeIns);

        morph.setOnFinished(e -> context.proceed());
        morph.play();
    }
}
