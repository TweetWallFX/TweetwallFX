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

import org.tweetwallfx.tweet.TweetSetData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.controls.dataprovider.TweetDataProvider;
import org.tweetwallfx.tweet.ThreadingHelper;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

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

        tweetSetData.buildTree(100);
        startupLogger.trace("** create wordle");
        createWordle();
        startupLogger.trace("** create wordle done");

        startupLogger.trace("** 2. Starting new Tweets search for " + tweetSetData.getSearchText());
        showTweetsExecutor.execute(new ShowTweetsTask());
    }

    public void stop() {
        showTweetsExecutor.shutdown();
        try {
            showTweetsExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        }
    }

//    private static class TweetsUpdateTask extends Task<Void> {
//
//        private final Pattern pattern = Pattern.compile("\\s+");                    //Splitter for new tweet text to words.
//        private final TweetSetData tweetSetData;
//        private final Wordle wordle;
//
//        TweetsUpdateTask(final TweetSetData tweetSetData, final Wordle wordle) {
//            this.tweetSetData = tweetSetData;
//            this.wordle = wordle;
//        }
//
//        @Override
//        protected Void call() throws Exception {
//            while (!isCancelled()) {
////                Tweet tweet = tweetSetData.getNextOrRandomTweet(5, 10);
////                if (null != tweet) {
////                parents.put(createTweetInfoBox(tweets.take()));
////                    Platform.runLater(() -> wordle.setTweet(tweet));
////                    addTweetToCloud(tweet);
////                    Thread.sleep(3000);
////                    Platform.runLater(() -> wordle.setLayoutMode(Wordle.LayoutMode.TWEET));
////                    Thread.sleep(8000);
////                    Platform.runLater(() -> wordle.setLayoutMode(Wordle.LayoutMode.WORDLE));
////                    Thread.sleep(5000);
////                removeTweetFromCloud(tweetWords);
//                    Platform.runLater(() -> wordle.setWords(tweetSetData.getTree()
//                            .entrySet()
//                            .stream()
//                            .sorted(TweetSetData.COMPARATOR.reversed())
//                            .limit(TagTweets.NUM_MAX_WORDS)
//                            .map((java.util.Map.Entry<String, Long> entry) -> new Word(entry.getKey(), entry.getValue()))
//                            .collect(Collectors.toList())));
//                    Thread.sleep(5000);
//                }
//            }
//            return null;
//        }

//        private Set<Word> addTweetToCloud(Tweet tweet) {
//            String text = tweet.getTextWithout(UrlTweetEntry.class)
//                    .getTextWithout(UserMentionTweetEntry.class)
//                    .get();
//            Set<Word> tweetWords = pattern.splitAsStream(text)
//                    .map(StopList::trimTail) //no bad word tails
//                    .filter(l -> l.length() > 2) //longer than 2 characters
//                    .filter(StopList.IS_NOT_URL) //no url
//                    .filter(StopList::notIn) //not in stoplist
//                    .map(l -> new Word(l, -2)) //convert to Word
//                    .collect(Collectors.toSet());                   //collect
//            List<Word> words = new ArrayList<>(wordle.wordsProperty().get());
//            tweetWords.removeAll(words);
//            words.addAll(tweetWords);
//            Platform.runLater(() -> wordle.wordsProperty().set(words));
//            return tweetWords;
//        }
//    }

    private class ShowTweetsTask extends Task<Void> {

        private final BlockingQueue<Parent> parents = new ArrayBlockingQueue<>(5);
        private final ExecutorService tweetsCreationExecutor = ThreadingHelper.createSingleThreadExecutor("CreateTweets");
//        private final ExecutorService tweetsUpdateExecutor = ThreadingHelper.createSingleThreadExecutor("UpdateTweets");
        private final Task<Void> tweetsCreationTask;
//        private final Task<Void> tweetsUpdateTask;

        ShowTweetsTask() {
            tweetsCreationTask = tweetSetData.getCreationTask();
//            tweetsUpdateTask = new TweetsUpdateTask(tweetSetData, wordle);

            setOnCancelled(e -> {
                tweetsCreationTask.cancel();
//                tweetsUpdateTask.cancel();
            });
        }

        @Override
        protected Void call() throws Exception {
            tweetsCreationExecutor.execute(tweetsCreationTask);
//            tweetsUpdateExecutor.execute(tweetsUpdateTask);

            tweetsCreationExecutor.shutdown();
//            tweetsUpdateExecutor.shutdown();
            
            return null;
        }
    }

    private void createWordle() {
        if (null == wordle) {
            wordle = new Wordle();
            hWordle.getChildren().setAll(wordle);
            wordle.prefWidthProperty().bind(hWordle.widthProperty());
            wordle.prefHeightProperty().bind(hWordle.heightProperty());
            wordle.addDataProvider(new TweetDataProvider(tweetSetData.getTweeter(), tweetSetData.getSearchText()));
        }
        Platform.runLater(() -> {
            wordle.setWords(tweetSetData.getTree().entrySet().stream()
                    .sorted(TweetSetData.COMPARATOR.reversed())
                    .limit(NUM_MAX_WORDS).map(entry -> new Word(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
//            wordle.setTweet(tweetSetData.getNextOrRandomTweet(0, 3));
        });
    }

}
