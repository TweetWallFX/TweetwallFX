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
package org.tweetwallfx.twod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import org.tweetwallfx.controls.StopList;

import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * Tasks to perform a search on Twitter for some hashtag, create an HBox with
 * each tweets, crate a snapshot and then load the image as diffuseMap of a
 * segmented torus Tasks and blockingQueues take care of this complex process
 *
 * @author José Pereda
 */
public class TagTweets {

    private final static int MIN_WEIGHT = 4;
    private final static int NUM_MAX_WORDS = 40;
    private final Pattern pattern = Pattern.compile("\\s+");
    private final ExecutorService showTweetsExecutor = createExecutor("ShowTweets");
    private final ShowTweetsTask showTweetsTask;
    private Wordle wordle;
    private Map<String, Long> tree;
    private final String searchText;
    private final Tweeter tweeter;
    private final BorderPane root;
    private final HBox hBottom = new HBox();
    private final HBox hWordle = new HBox();
    private final Comparator<Map.Entry<String, Long>> comparator = Comparator.comparingLong(Map.Entry::getValue);
    private final ObservableList<Word> obsFadeOutWords = FXCollections.<Word>observableArrayList();
    private final ObservableList<Text> obsFadeInWords = FXCollections.<Text>observableArrayList();

    public TagTweets(Tweeter tweeter, String searchText, BorderPane root) {
        this.tweeter = tweeter;
        this.searchText = searchText;
        this.root = root;
        this.showTweetsTask = new ShowTweetsTask(tweeter, searchText);
    }

    public void start() {
        hWordle.setAlignment(Pos.CENTER);
        hWordle.setPadding(new Insets(20));
//        hWordle.setStyle("-fx-border-width: 2px; -fx-border-color: red;");
//            VBox.setVgrow(hWordle,Priority.ALWAYS);            

//            hBottom.setMinHeight(150);
//            hBottom.setPrefHeight(150);
//            VBox vbox = new VBox(hWordle, hBottom);
//
//            vbox.setMaxHeight(imageView.getFitHeight());
//            
//            stackPane.getChildren().addAll(imageView, vbox);
//            
//            root.getChildren().setAll(stackPane);
        hWordle.prefWidthProperty().bind(root.widthProperty());
        hWordle.prefHeightProperty().bind(root.heightProperty());

        root.setCenter(hWordle);

        System.out.println("** 1. Creating Tag Cloud for " + searchText);
        buildTagCloud(tweeter.search(new TweetQuery().query(searchText).count(100)));
        createWordle();

        System.out.println("** 2. Starting new Tweets search for " + searchText);
        showTweetsExecutor.execute(showTweetsTask);
    }

    public void stop() {
        showTweetsExecutor.shutdown();
        try {
            showTweetsExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        }
    }

    private class TweetsCreationTask extends Task<Void> {

        private final String searchText;
        private TweetStream stream;
        private final Tweeter tweeter;
        private final BlockingQueue<Tweet> tweets;

        public TweetsCreationTask(Tweeter tweeter, String searchText, BlockingQueue<Tweet> tweets) {
            this.tweeter = tweeter;
            this.searchText = searchText;
            this.tweets = tweets;
        }

        @Override
        protected Void call() throws Exception {
            if (tweeter != null) {
                stream = tweeter.createTweetStream();
                stream.onTweet(tweet -> {
                    try {
                        Tweet tw = checkNewTweetHasTags(tweet);
                        if (tw != null) {
                            tweets.put(tw);
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Error: " + ex);
                    }
                });
                stream.filter(new TweetFilterQuery().track(new String[]{searchText}));
            }

            return null;
        }

        private Tweet checkNewTweetHasTags(Tweet tweet) {
            String status = tweet.getText().replaceAll("[^\\dA-Za-z ]", " ");

            List<String> collect = pattern.splitAsStream(status)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // add words to tree and update weights
            collect.stream()
                    .filter(w -> w.length() > 2)
                    .filter(w -> !StopList.contains(w))
                    .forEach(w -> tree.put(w, (tree.containsKey(w) ? tree.get(w) : 0) + 1l));

//            // check if there is any word in the tags in the wall 
//            if (tree.entrySet().stream()
//                    .sorted(comparator.reversed())
//                    .limit(NUM_MAX_WORDS)
//                    .anyMatch(entry -> collect.contains(entry.getKey()))) {
//
//                // return the tweet
//                return info;
//            }
            return tweet;

//            return null;
        }
    }

    private class TweetsUpdateTask extends Task<Void> {

        private final BlockingQueue<Tweet> tweets;

        TweetsUpdateTask(BlockingQueue<Tweet> tweets, BlockingQueue<Parent> parents) {
            this.tweets = tweets;
        }

        private Tweet getTweet() throws InterruptedException {
            Tweet tweet = tweets.poll(5, TimeUnit.SECONDS);

            if (tweet == null) {
                tweet = tweeter.search(new TweetQuery().query(searchText).count(10))
                        .skip((long) (Math.random() * 10))
                        .findFirst().orElse(null);
            }

            return tweet;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }

                Tweet tweet = getTweet();
                if (null != tweet) {
//                parents.put(createTweetInfoBox(tweets.take()));
                    Platform.runLater(() -> wordle.setTweet(tweet));
                    addTweetToCloud(tweet);
                    Thread.sleep(3000);
                    Platform.runLater(() -> wordle.setLayoutMode(Wordle.LayoutMode.TWEET));
                    Thread.sleep(8000);
                    Platform.runLater(() -> wordle.setLayoutMode(Wordle.LayoutMode.WORDLE));
                    Thread.sleep(5000);
//                    removeTweetFromCloud(tweetWords);
                    Platform.runLater(()
                            -> wordle.setWords(tree.entrySet().stream()
                                    .sorted(comparator.reversed())
                                    .limit(NUM_MAX_WORDS).map(entry -> new Word(entry.getKey(), entry.getValue())).collect(Collectors.toList()))
                    );
                    Thread.sleep(5000);
                }
            }
            return null;
        }

        private Set<Word> addTweetToCloud(Tweet tweet) {
//            System.out.println("Add tweet to cloud");
            String text = tweet.getTextWithout(UrlTweetEntry.class, UserMentionTweetEntry.class);
            Set<Word> tweetWords = pattern.splitAsStream(text)
                    .filter(l -> l.length() > 2)
                    .filter(l -> !StopList.contains(l))
                    .map(l -> new Word(l, -2))
                    .peek(System.out::println)
                    .collect(Collectors.toSet());
            List<Word> words = new ArrayList<>(wordle.wordsProperty().get());
            tweetWords.removeAll(words);
            words.addAll(tweetWords);
            Platform.runLater(() -> wordle.wordsProperty().set(words));
            return tweetWords;
        }
    }

    private class TweetsSnapshotTask extends Task<Void> {

        private final BlockingQueue<Parent> tweets;
        private final ParallelTransition parallelWords = new ParallelTransition();
        private final ParallelTransition parallelTexts = new ParallelTransition();
        private final SequentialTransition sequential = new SequentialTransition(parallelWords, parallelTexts);

        TweetsSnapshotTask(BlockingQueue<Parent> tweets) {
            this.tweets = tweets;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                snapshotTweet(tweets.take());
            }
            return null;
        }

        private void snapshotTweet(final Parent tweetContainer) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            // render the chart in an offscreen scene (scene is used to allow css processing) and snapshot it to an image.
            // the snapshot is done in runlater as it must occur on the javafx application thread.
            Platform.runLater(() -> {
                hBottom.getChildren().setAll(tweetContainer);
                // animation
                obsFadeOutWords.stream().forEach(w -> parallelWords.getChildren().add(fadeOut(w)));
                obsFadeInWords.stream().forEach(w -> parallelTexts.getChildren().add(fadeIn(w)));

                sequential.setOnFinished(e -> {
                    parallelWords.getChildren().clear();
                    parallelTexts.getChildren().clear();
                    latch.countDown();
                });

                sequential.play();
            });

            latch.await();
        }

        private Timeline fadeOut(Word word) {
//            word.getNode().setOpacity(1);
            Timeline timeline = new Timeline();
//            KeyFrame key = new KeyFrame(Duration.millis(1000),
//                    new KeyValue(word.getNode().opacityProperty(), 0.1, Interpolator.EASE_OUT));
//            timeline.getKeyFrames().add(key);
            return timeline;
        }

        private Timeline fadeIn(Text word) {
            word.setOpacity(0.1);
            Timeline timeline = new Timeline();
            KeyFrame key = new KeyFrame(Duration.millis(1000),
                    new KeyValue(word.opacityProperty(), 1, Interpolator.EASE_IN));
            timeline.getKeyFrames().add(key);
            return timeline;
        }
    }

    private class ShowTweetsTask extends Task<Void> {

        private final BlockingQueue<Tweet> tweets = new ArrayBlockingQueue<>(5);
        private final BlockingQueue<Parent> parents = new ArrayBlockingQueue<>(5);
        private final ExecutorService tweetsCreationExecutor = createExecutor("CreateTweets");
        private final ExecutorService tweetsUpdateExecutor = createExecutor("UpdateTweets");
        private final ExecutorService tweetsSnapshotExecutor = createExecutor("TakeSnapshots");
        private final TweetsCreationTask tweetsCreationTask;
        private final TweetsUpdateTask tweetsUpdateTask;
        private final TweetsSnapshotTask tweetsSnapshotTask;

        ShowTweetsTask(final Tweeter tweeter, final String textSearch) {
            tweetsCreationTask = new TweetsCreationTask(tweeter, textSearch, tweets);
            tweetsUpdateTask = new TweetsUpdateTask(tweets, parents);
            tweetsSnapshotTask = new TweetsSnapshotTask(parents);

            setOnCancelled(e -> {
                tweetsCreationTask.cancel();
                tweetsUpdateTask.cancel();
                tweetsSnapshotTask.cancel();
            });

        }

        @Override
        protected Void call() throws Exception {
            tweetsCreationExecutor.execute(tweetsCreationTask);
            tweetsUpdateExecutor.execute(tweetsUpdateTask);
            tweetsSnapshotExecutor.execute(tweetsSnapshotTask);

            tweetsCreationExecutor.shutdown();
            tweetsUpdateExecutor.shutdown();
            tweetsSnapshotExecutor.shutdown();

            try {
                tweetsSnapshotExecutor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
            }

            return null;
        }
    }

    private ExecutorService createExecutor(final String name) {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setName(name);
            t.setDaemon(true);
            return t;
        };
        return Executors.newSingleThreadExecutor(factory);
    }

    private void buildTagCloud(Stream<Tweet> tweets) {
        Stream<String> stringStream = tweets
                .map(t -> t.getText()
                        .replaceAll("[.,!?:´`']((\\s+)|($))", " ")
                        .replaceAll("http[s]?:.*((\\s+)|($))", " ")
                        .replaceAll("['\"]", " "));
        tree = stringStream
                .flatMap(c -> pattern.splitAsStream(c))
                .filter(l -> l.length() > 2)
                .filter(l -> !l.startsWith("@"))
                .map(l -> l.toLowerCase())
                .filter(l -> !StopList.contains(l))
                .collect(Collectors.groupingBy(String::toLowerCase, TreeMap::new, Collectors.counting()));
    }

    private void createWordle() {
        if (null == wordle) {
            wordle = new Wordle();
            hWordle.getChildren().setAll(wordle);
            wordle.prefWidthProperty().bind(hWordle.widthProperty());
            wordle.prefHeightProperty().bind(hWordle.heightProperty());
        }
        Platform.runLater(()
                -> wordle.setWords(tree.entrySet().stream()
                        .sorted(comparator.reversed())
                        .limit(NUM_MAX_WORDS).map(entry -> new Word(entry.getKey(), entry.getValue())).collect(Collectors.toList()))
        );
    }

}
