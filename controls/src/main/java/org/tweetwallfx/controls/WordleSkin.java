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

import org.tweetwallfx.controls.util.ImageCache;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.controls.stepengine.StepIterator;
import org.tweetwallfx.controls.steps.AddTweetToCloudStep;
import org.tweetwallfx.controls.steps.CloudFadeOutStep;
import org.tweetwallfx.controls.steps.CloudToCloudStep;
import org.tweetwallfx.controls.steps.CloudToTweetStep;
import org.tweetwallfx.controls.steps.FadeInCloudStep;
import org.tweetwallfx.controls.steps.ImageMosaicStep;
import org.tweetwallfx.controls.steps.NextTweetStep;
import org.tweetwallfx.controls.steps.PauseStep;
import org.tweetwallfx.controls.steps.TweetToCloudStep;
import org.tweetwallfx.controls.steps.UpdateCloudStep;

/**
 * @author sven
 */
public class WordleSkin extends SkinBase<Wordle> {
        
    private static final Logger log = LogManager.getLogger(WordleSkin.class);
    
    public final Map<Word, Text> word2TextMap = new HashMap<>();
    // used for Tweet Display
    public  final List<TweetLayout.TweetWordNode> tweetWordList = new ArrayList<>();
    private final Pane pane;
    private final Pane stackPane;
    private Pane infoBox;
    private Pane mediaBox;

    private int displayCloudTags = 25;

    private ImageView logo;
    private ImageView secondLogo;
    private ImageView backgroundImage;
    private Font font;
    private int fontSizeMin;
    private int fontSizeMax;
    private int tweetFontSize;
    private final Boolean favIconsVisible;
    private final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    private final ImageCache mediaImageCache = new ImageCache(new ImageCache.DefaultImageCreator());
    private final ImageCache profileImageCache = new ImageCache(new ImageCache.ProfileImageCreator());
    public ImageView getSecondLogo(){
        return secondLogo;
    }
    public ImageView getLogo() {
        return logo;
    }

    public ImageCache getMediaImageCache() {
        return mediaImageCache;
    }

    public ImageCache getProfileImageCache() {
        return profileImageCache;
    }

    public Pane getPane() {
        return pane;
    }

    public int getDisplayCloudTags() {
        return displayCloudTags;
    }

    public Pane getMediaBox() {
        return mediaBox;
    }

    public void setMediaBox(Pane mediaBox) {
        this.mediaBox = mediaBox;
    }

    public Pane getInfoBox() {
        return infoBox;
    }

    public void setInfoBox(Pane infoBox) {
        this.infoBox = infoBox;
    }

    public Boolean getFavIconsVisible() {
        return favIconsVisible;
    }

    public DateFormat getDf() {
        return df;
    }
    
    public Font getFont() {
        return font;
    }
    
    public int getFontSizeMin() {
        return fontSizeMin;
    }
    
    public int getFontSizeMax() {
        return fontSizeMax;
    }
    
    public int getTweetFontSize() {
        return tweetFontSize;
    }
    
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
        
        pane.heightProperty().addListener((observable) -> {
            updateLogoPosition();
        });

        pane.widthProperty().addListener((observable) -> {
            updateLogoPosition();
        });
        
        getSkinnable().logoProperty().addListener((obs, oldValue, newValue) -> {
            updateLogo(newValue);
        });
        
        pane.heightProperty().addListener((observable) -> {
            updateSecondLogoPosition();
        });

        pane.widthProperty().addListener((observable) -> {
            updateSecondLogoPosition();
        });
        
        getSkinnable().secondLogoProperty().addListener((obs, oldValue, newValue) -> {
            updateSecondLogo(newValue);
        });
        
        getSkinnable().backgroundGraphicProperty().addListener((obs, oldValue, newValue) -> {
            updateBackgroundGraphic(newValue);
        });

        updateBackgroundGraphic(getSkinnable().backgroundGraphicProperty().getValue());
        updateLogo(getSkinnable().logoProperty().getValue());
        updateSecondLogo(getSkinnable().secondLogoProperty().getValue());

        favIconsVisible = wordle.favIconsVisibleProperty().get();
        displayCloudTags = wordle.displayedNumberOfTagsProperty().get();
        font = wordle.fontProperty().get();
        fontSizeMin = wordle.fontSizeMinProperty().get();
        fontSizeMax = wordle.fontSizeMaxProperty().get();
        tweetFontSize = wordle.tweetFontSizeProperty().get();
        prepareStepMachine();
    }

    private void updateLogo(final String newLogo) {
        if (null != logo) {
            pane.getChildren().remove(logo);
            logo = null;
        }   
        log.trace("Logo: " + newLogo);
        if (null != newLogo && !newLogo.isEmpty()) {
            logo = new ImageView(newLogo);
            logo.getStyleClass().add("logo");            
            pane.getChildren().add(logo);
            updateLogoPosition();
        }
    }
    
    private void updateLogoPosition() {
        log.trace("Updating logo position");
        if (null != logo) {
            logo.setLayoutX(0);
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
        }
    }
    
    private void updateSecondLogo(final String newLogo) {
        if (null != secondLogo) {
            pane.getChildren().remove(secondLogo);
            secondLogo = null;
        }   
        log.trace("SecondLogo: " + newLogo);
        if (null != newLogo && !newLogo.isEmpty()) {
            secondLogo = new ImageView(newLogo);
            secondLogo.getStyleClass().add("secondlogo");            
            pane.getChildren().add(secondLogo);
            updateSecondLogoPosition();
        }
    }
    
    private void updateSecondLogoPosition() {
        log.trace("Updating secondLogo position");
        if (null != secondLogo) {
            secondLogo.setLayoutX(pane.getWidth() - (secondLogo.getImage().getWidth()*0.8));
            secondLogo.setLayoutY(pane.getHeight() - (secondLogo.getImage().getHeight()*0.8));
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
    private final ExecutorService tickTockExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("TickTock");
            t.setDaemon(true);
            return t;
        });
    
    public void prepareStepMachine() {
        StepIterator steps = new StepIterator(Arrays.asList(//UpdateCloudStep(),
                new FadeInCloudStep(),
                new NextTweetStep(),
                new AddTweetToCloudStep(),
                new CloudToCloudStep(),
                new CloudToTweetStep(),
                new PauseStep(),
                new TweetToCloudStep(),
                new CloudFadeOutStep(),
                new ImageMosaicStep()
        ));
        
        StepEngine s = new StepEngine(steps);
        s.getContext().put("WordleSkin", this);
        
        tickTockExecutor.execute(new Runnable() {
            @Override
            public void run() {
                s.go();
            }
        });
    }

}