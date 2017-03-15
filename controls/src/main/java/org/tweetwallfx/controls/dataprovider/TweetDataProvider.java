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
package org.tweetwallfx.controls.dataprovider;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 *
 * @author sven
 */
public class TweetDataProvider implements DataProvider {
    
    private static final Logger log = LogManager.getLogger(TweetDataProvider.class);
    private static final int HISTORY_SIZE = 20; 

    private final Tweeter tweeter;
    
    private volatile Tweet tweet;
    private volatile Tweet nextTweet;
    private final String searchText;
    private final Deque<Long> history = new ArrayDeque<>();
    private volatile List<Tweet> lastTweetCollection;
    
    public TweetDataProvider(Tweeter tweeter, TweetStream tweetStream, final String searchText) {
        this.tweeter = tweeter;
        this.searchText = searchText;
        tweetStream.onTweet(tweet -> {
            log.info("new Tweet received");
            this.nextTweet = tweet;
            this.lastTweetCollection = null;
        });
    }
    
    public Tweet getTweet() {
        return this.tweet;
    }

    private List<Tweet> getLatestHistory() {
        log.info("Reinit the history");
        return tweeter.search(new TweetQuery()
                        .query(searchText)
                        .count(HISTORY_SIZE)).collect(Collectors.toList());        
    }
    
    public Tweet nextTweet() {
        if (null == nextTweet) {
            if (null == lastTweetCollection) {
                lastTweetCollection = getLatestHistory();
            }
            nextTweet = lastTweetCollection.stream()
                    .filter(tweet -> !history.contains(tweet.getId()))
                    .skip((long) (Math.random() * (HISTORY_SIZE - history.size())))
                    .findFirst()
                    .orElse(null);
        } 
        if (null != nextTweet) {
            tweet = nextTweet;
            nextTweet = null;
        }
        if (tweet != null) {
            history.addLast(tweet.getId());
            if (history.size() > HISTORY_SIZE -1 ) {
                history.removeFirst();
            }
        }
        return tweet;
    }

    @Override
    public String getName() {
        return "Tweet";
    }

}
