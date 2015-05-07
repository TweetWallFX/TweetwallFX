package org.tweetwallfx.twod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.tweetwallfx.controls.StopList;

import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.twitter.TweetInfo;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

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
    private final Configuration conf;
    private final BorderPane root;
    private final HBox hBottom = new HBox();
    private final HBox hWordle = new HBox();

    private final Comparator<Map.Entry<String, Long>> comparator = Comparator.comparingLong(Map.Entry::getValue);

    private final ObservableList<Word> obsFadeOutWords = FXCollections.<Word>observableArrayList();
    private final ObservableList<Text> obsFadeInWords = FXCollections.<Text>observableArrayList();
    private Twitter twitter;

    public TagTweets(Configuration conf, String searchText, BorderPane root) {
        this.conf = conf;
        this.searchText = searchText;
        this.root = root;
        this.showTweetsTask = new ShowTweetsTask(conf, searchText);
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

        try {
            System.out.println("** 1. Creating Tag Cloud for " + searchText);
            Query query = new Query(searchText);
            query.setCount(100);
            twitter = new TwitterFactory(conf).getInstance();
            QueryResult result = twitter.search(query);

            buildTagCloud(result.getTweets());
            createWordle();

        } catch (TwitterException ex) {
            System.out.println("Error Twitter: " + ex);
            return;
        }

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
        private TwitterStream stream;
        private final Configuration conf;
        private final BlockingQueue<TweetInfo> tweets;

        public TweetsCreationTask(Configuration conf, String searchText, BlockingQueue<TweetInfo> tweets) {
            this.conf = conf;
            this.searchText = searchText;
            this.tweets = tweets;
        }

        @Override
        protected Void call() throws Exception {
            FilterQuery query = new FilterQuery();
            query.track(new String[]{searchText});
            if (conf != null) {
                stream = new TwitterStreamFactory(conf).getInstance();
                addListener(s -> {
                    try {
                        TweetInfo tw = checkNewTweetHasTags(new TweetInfo(s));
                        if (tw != null) {
                            tweets.put(tw);
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Error: " + ex);
                    }
                });
                stream.filter(query);
            }
            return null;

        }

        private void addListener(Consumer<Status> consumer) {
            stream.addListener(new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    consumer.accept(status);
                }
            });
        }

        private TweetInfo checkNewTweetHasTags(TweetInfo info) {

            String status = info.getText().replaceAll("[^\\dA-Za-z ]", " ");

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
            return info;

//            return null;
        }
    }

    private class TweetsUpdateTask extends Task<Void> {

        private final BlockingQueue<TweetInfo> tweets;
        private final BlockingQueue<Parent> parents;

        TweetsUpdateTask(BlockingQueue<TweetInfo> tweets, BlockingQueue<Parent> parents) {
            this.tweets = tweets;
            this.parents = parents;
        }

        private TweetInfo getTweet() throws InterruptedException, TwitterException {
            TweetInfo tweet = tweets.poll(5, TimeUnit.SECONDS);
            if (tweet == null) {
                Query query = new Query(searchText);
                query.setCount(10);
                QueryResult result = twitter.search(query);
                long skipping = (long) (Math.random() * 10);
                Optional<Status> status = result.getTweets().stream().skip(skipping).findFirst();
                if (status.isPresent()) {
                    tweet = new TweetInfo(status.get());
                }
            }
            return tweet;
        }

        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }

                TweetInfo tweet = getTweet();
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

        private void removeTweetFromCloud(Set<Word> tweetWords) {
            System.out.println("Remove tweet from cloud");
            List<Word> words = new ArrayList<>(wordle.wordsProperty().get());
            words.removeAll(tweetWords);
            Platform.runLater(() -> wordle.wordsProperty().set(words));
        }

        private Set<Word> addTweetToCloud(TweetInfo tweetInfo) {
//            System.out.println("Add tweet to cloud");
            String text = tweetInfo.getText();
            Set<Word> tweetWords = pattern.splitAsStream(text)
                    .filter(l -> l.length() > 2)
                    .filter(l -> !l.startsWith("@"))
                    .filter(l -> !l.startsWith("http:"))
                    .filter(l -> !l.startsWith("https:"))
                    .filter(l -> !StopList.contains(l)).map(l -> new Word(l, -2)).collect(Collectors.toSet());
            List<Word> words = new ArrayList<>(wordle.wordsProperty().get());
            tweetWords.removeAll(words);
            words.addAll(tweetWords);
            Platform.runLater(() -> wordle.wordsProperty().set(words));
            return tweetWords;
        }

        private Parent createTweetInfoBox(TweetInfo info) {

            // update & redraw wordle with new/same words
            Platform.runLater(() -> createWordle());

            HBox hbox = new HBox(20);
            hbox.setStyle("-fx-padding: 20px;");
            hbox.setPrefHeight(150);

            HBox hImage = new HBox();
            hImage.setPadding(new Insets(10));
            Image image = new Image(info.getImageURL(), 48, 48, true, false);
            ImageView imageView = new ImageView(image);
            Rectangle clip = new Rectangle(48, 48);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            imageView.setClip(clip);
            hImage.getChildren().add(imageView);

            HBox hName = new HBox(20);
            Label name = new Label(info.getName());
            name.setStyle("-fx-font: 24px \"Andalus\"; -fx-text-fill: #292F33; -fx-font-weight: bold;");
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            Label handle = new Label("@" + info.getHandle() + " · " + df.format(info.getDate()));
            handle.setStyle("-fx-font: 22px \"Andalus\"; -fx-text-fill: #8899A6;");
            hName.getChildren().addAll(name, handle);

            List<String> words = tree.entrySet().stream()
                    .sorted(comparator.reversed())
                    .limit(NUM_MAX_WORDS)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            List<String> fadeOutWords = new ArrayList<>();
            List<Text> fadeInWords = new ArrayList<>();
            System.out.println("Tw: " + info.getText());
            TextFlow flow = new TextFlow();
            pattern.splitAsStream(info.getText())
                    .forEach(w -> {
                        Text textWord = new Text(w.concat(" "));
                        String color = "#292F33";
                        if (words.stream().anyMatch(tag -> {
                            if (w.toLowerCase().contains(tag)) {
                                fadeOutWords.add(tag);
                                return true;
                            } else {
                                fadeInWords.add(textWord);
                            }
                            return false;
                        })) {
                            color = "red";
                        }
                        textWord.setStyle("-fx-font: 18px \"Andalus\"; -fx-fill: " + color + ";");
                        flow.getChildren().add(textWord);
                    });
//            List<Word> wordsToFade = wordle.formatWords(fadeOutWords);
//            obsFadeOutWords.setAll(wordsToFade);
            obsFadeInWords.setAll(fadeInWords);

            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(hName, flow);
            hbox.getChildren().addAll(hImage, vbox);

            return hbox;
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

    private class ShowTweetsTask<Void> extends Task {

        private final BlockingQueue<TweetInfo> tweets = new ArrayBlockingQueue<>(5);
        private final BlockingQueue<Parent> parents = new ArrayBlockingQueue<>(5);
        private final ExecutorService tweetsCreationExecutor = createExecutor("CreateTweets");
        private final ExecutorService tweetsUpdateExecutor = createExecutor("UpdateTweets");
        private final ExecutorService tweetsSnapshotExecutor = createExecutor("TakeSnapshots");
        private final TweetsCreationTask tweetsCreationTask;
        private final TweetsUpdateTask tweetsUpdateTask;
        private final TweetsSnapshotTask tweetsSnapshotTask;

        ShowTweetsTask(final Configuration conf, final String textSearch) {
            tweetsCreationTask = new TweetsCreationTask(conf, textSearch, tweets);
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

    private void buildTagCloud(List<Status> tweets) {
        Stream<String> stringStream = tweets.stream()
                //                .map(t -> t.getText());
                .map(t -> t.getText().replaceAll("[.,!?:´`']((\\s+)|($))", " ").replaceAll("http[s]?:.*((\\s+)|($))", " ").replaceAll("['\"]", " "));
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
