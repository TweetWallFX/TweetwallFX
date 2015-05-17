/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.tweet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * @author martin
 */
public final class TweetSetData {

    public static final Comparator<Map.Entry<String, Long>> COMPARATOR = Comparator.comparingLong(Map.Entry::getValue);
    private static final Pattern pattern = Pattern.compile("\\s+");
    private final Tweeter tweeter;
    private final String searchText;
    private final BlockingQueue<Tweet> transferTweets;
    private final TweetsCreationTask tweetsCreationTask;
    private Map<String, Long> tree;

    public TweetSetData(final Tweeter tweeter, final String searchText) {
        this(tweeter, searchText, 5);
    }

    public TweetSetData(final Tweeter tweeter, final String searchText, final int transferSize) {
        this.tweeter = Objects.requireNonNull(tweeter, "tweeter must not be null!");
        this.searchText = searchText;
        this.transferTweets = new ArrayBlockingQueue<>(transferSize);
        this.tweetsCreationTask = new TweetsCreationTask(this);
    }

    public Task<Void> getCreationTask() {
        return tweetsCreationTask;
    }

    public String getSearchText() {
        return searchText;
    }

    public Tweet getNextOrRandomTweet(final int waitSeconds, final int random) throws InterruptedException {
        Tweet tweet = transferTweets.poll(5, TimeUnit.SECONDS);

        if (tweet == null) {
            tweet = tweeter.search(new TweetQuery()
                    .query(searchText)
                    .count(10))
                    .skip((long) (Math.random() * 10))
                    .findFirst()
                    .orElse(null);
        }

        return tweet;
    }

    public BlockingQueue<Tweet> getTransferTweets() {
        return transferTweets;
    }

    public Map<String, Long> getTree() {
        return Collections.unmodifiableMap(tree);
    }

    public Tweeter getTweeter() {
        return tweeter;
    }

    public void buildTree(final int numberOfTweets) {
        final Stream<String> stringStream = tweeter
                .search(new TweetQuery().query(searchText).count(100))
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

    public void updateTree(final Tweet tweet) {
        final String status = tweet.getText().replaceAll("[^\\dA-Za-z ]", " ");
        // add words to tree and update weights

        pattern.splitAsStream(status)
                .map(String::toLowerCase)
                .filter((w) -> w.length() > 2)
                .filter((java.lang.String w) -> !StopList.contains(w))
                .forEach((java.lang.String w) -> tree.put(w, (tree.containsKey(w)
                                        ? tree.get(w)
                                        : 0) + 1l));
    }

    private static final class TweetsCreationTask extends Task<Void> {

        private final TweetSetData tweetSetData;

        public TweetsCreationTask(final TweetSetData tweetSetData) {
            this.tweetSetData = tweetSetData;
        }

        @Override
        protected Void call() throws Exception {
            final TweetStream stream = tweetSetData.getTweeter().createTweetStream();

            stream.onTweet(tweet -> {
                try {
                    tweetSetData.updateTree(tweet);
                    tweetSetData.getTransferTweets().put(tweet);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex);
                }
            });
            stream.filter(new TweetFilterQuery().track(new String[]{tweetSetData.getSearchText()}));

            return null;
        }
    }
}
