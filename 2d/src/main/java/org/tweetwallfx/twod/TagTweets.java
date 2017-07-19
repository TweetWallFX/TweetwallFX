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
package org.tweetwallfx.twod;

import java.util.List;
import org.tweetwallfx.tweet.TweetSetData;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.controls.dataprovider.ImageMosaicDataProvider;
import org.tweetwallfx.controls.dataprovider.TagCloudDataProvider;
import org.tweetwallfx.controls.dataprovider.TweetDataProvider;
import org.tweetwallfx.tweet.ThreadingHelper;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetQuery;

/**
 * TweetWallFX - Devoxx 2014,15,16 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * Tasks to perform a search on Twitter for some hashtag, create an HBox with
 * each tweets, crate a snapshot and then load the image as diffuseMap of a
 * segmented torus Tasks and blockingQueues take care of this complex process
 *
 * @author José Pereda
 * @author Sven Reimers
 */
public class TagTweets {

    private static final String STARTUP = "org.tweetwallfx.startup";
    Logger startupLogger = Logger.getLogger(STARTUP);

    private final static int MIN_WEIGHT = 4;
    private final static int NUM_MAX_WORDS = 40;
    private final ExecutorService showTweetsExecutor = ThreadingHelper.createSingleThreadExecutor("ShowTweets");
    private Wordle wordle;
    private final TweetSetData tweetSetData;
    private final BorderPane root;
    private final HBox hBottom = new HBox();
    private final HBox hWordle = new HBox();
    private ImageMosaicDataProvider imageMosaicDataProvider;
    private TagCloudDataProvider tagCloudDataProvider;

    public TagTweets(final TweetSetData tweetSetData, final BorderPane root) {
        this.tweetSetData = tweetSetData;
        this.root = root;
    }

    public void start() {
        startupLogger.trace("TagTweets.start");
        hWordle.setAlignment(Pos.CENTER);
        hWordle.setPadding(new Insets(20));
        hWordle.prefWidthProperty().bind(root.widthProperty());
        hWordle.prefHeightProperty().bind(root.heightProperty());

        root.setCenter(hWordle);

        startupLogger.trace("** 1. Creating Tag Cloud for " + tweetSetData.getSearchText());

        imageMosaicDataProvider = new ImageMosaicDataProvider(tweetSetData.getTweetStream());
        tagCloudDataProvider = new TagCloudDataProvider(tweetSetData.getTweetStream());
        
        List<Tweet> tweets = tweetSetData.getTweeter().searchPaged(new TweetQuery().query(tweetSetData.getSearchText()).count(100), 20)
                .collect(Collectors.toList());
        
        tweets.stream().forEach(tweet -> {
            imageMosaicDataProvider.processTweet(tweet);
            tagCloudDataProvider.processTweet(tweet);
        });        
        
        startupLogger.trace("** create wordle");
        createWordle();
        startupLogger.trace("** create wordle done");

        startupLogger.trace("** 2. Starting new Tweets search for " + tweetSetData.getSearchText());
    }

    public void stop() {
        tweetSetData.getTweeter().shutdown();
        showTweetsExecutor.shutdown();
        try {
            showTweetsExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        }
    }

    private void createWordle() {
        if (null == wordle) {
            wordle = new Wordle();
            hWordle.getChildren().setAll(wordle);
            wordle.prefWidthProperty().bind(hWordle.widthProperty());
            wordle.prefHeightProperty().bind(hWordle.heightProperty());
            wordle.addDataProvider(new TweetDataProvider(tweetSetData.getTweeter(), tweetSetData.getTweetStream(), tweetSetData.getSearchText()));
            wordle.addDataProvider(tagCloudDataProvider);
            wordle.addDataProvider(imageMosaicDataProvider);
        }
    }

}
