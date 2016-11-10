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
package org.tweetwallfx.tweet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

/**
 * @author martin
 */
public final class TweetSetData {

    private static final Logger log = LogManager.getLogger(TweetSetData.class);

    public static final Comparator<Map.Entry<String, Long>> COMPARATOR = Comparator.comparingLong(Map.Entry::getValue);
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

    public TweetStream getTweetStream() {
        return tweetsCreationTask.stream;
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

    private static void downloadContent(URL url, File file) {
        boolean directoryCreated = file.getParentFile().mkdirs();
        if (directoryCreated) {
            log.info("directory created " + file.getPath());
        }

        try (InputStream inputStream = url.openStream();
                FileOutputStream outputStream = new FileOutputStream(file)) {

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException exception) {

        }
    }

    public void buildTree(final int numberOfTweets, Consumer<Tweet> tweetConsumer) {
        List<Tweet> tweets = tweeter.searchPaged(new TweetQuery().query(searchText).count(100), 20)
                .collect(Collectors.toList());
        tweets.stream().forEach(tweet -> tweetConsumer.accept(tweet));

        final Stream<String> stringStream = tweets.stream()
                .map(t -> t.getText()
                .replaceAll("[.,!?:´`']((\\s+)|($))", " ")
                .replaceAll("http[s]?:.*((\\s+)|($))", " ")
                .replaceAll("['\"()]", " "));

        tree = stringStream
                .flatMap(StopList.WORD_SPLIT::splitAsStream)
                .filter(l -> l.length() > 2)
                .filter(l -> !l.startsWith("@"))
                .map(String::toLowerCase)
                .map(StopList::removeEmojis)
                .filter(StopList::notIn)
                .collect(Collectors.groupingBy(String::toLowerCase, TreeMap::new, Collectors.counting()));
    }

    public void updateTree(final Tweet tweet) {
        final String status = tweet.getTextWithout(UrlTweetEntry.class)
                .getTextWithout(UserMentionTweetEntry.class)
                .get()
                .replaceAll("[^\\dA-Za-z ]", " ");

        final String noEmojiStatus = StopList.removeEmojis(status);
        // add words to tree and update weights

        StopList.WORD_SPLIT.splitAsStream(noEmojiStatus)
                .map(String::toLowerCase)
                .filter((w) -> w.length() > 2)
                .filter(StopList::notIn)
                .forEach((java.lang.String w) -> tree.put(w, (tree.containsKey(w)
                ? tree.get(w)
                : 0) + 1l));
    }

    private static final class TweetsCreationTask extends Task<Void> {

        private final TweetSetData tweetSetData;
        private final TweetStream stream;

        public TweetsCreationTask(final TweetSetData tweetSetData) {
            this.tweetSetData = tweetSetData;
            TweetFilterQuery query = new TweetFilterQuery().track(Pattern.compile(" [oO][rR] ").splitAsStream(tweetSetData.getSearchText()).toArray(n -> new String[n]));

            stream = tweetSetData.getTweeter().createTweetStream(query);
        }

        @Override
        protected Void call() throws Exception {

            stream.onTweet(tweet -> {
                tweetSetData.updateTree(tweet);
            });

            return null;
        }
    }
}
