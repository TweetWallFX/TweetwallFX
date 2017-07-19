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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.tweet.TweetSetData;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.TweetQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

/**
 *
 * @author sven
 */
public class TagCloudDataProvider implements DataProvider{

    private final static int NUM_MAX_WORDS = 40;
    
    private List<Word> additionalTweetWords = null;
    private Map<String, Long> tree = new TreeMap<>();

    
    public TagCloudDataProvider(TweetStream tweetStream) {
        tweetStream.onTweet(tweet -> processTweet(tweet));
    }

    public void processTweet(Tweet tweet) {
        updateTree(tweet);
    }

    
    public void setAdditionalTweetWords(List<Word> newWordList) {
        this.additionalTweetWords = newWordList;
    }
    
    public List<Word> getAdditionalTweetWords() {
        return this.additionalTweetWords;
    }
    
    public List<Word> getWords() {
        return tree.entrySet().stream()
                    .sorted(TweetSetData.COMPARATOR.reversed())
                    .limit(NUM_MAX_WORDS).map(entry -> new Word(entry.getKey(), entry.getValue())).collect(Collectors.toList());        
    }

    private void updateTree(final Tweet tweet) {
        StopList.WORD_SPLIT.splitAsStream(tweet.getTextWithout(UrlTweetEntry.class)
                .getTextWithout(MediaTweetEntry.class)
                .getTextWithout(UserMentionTweetEntry.class)
                .get()
                .replaceAll("[.,!?:´`']((\\s+)|($))", " ")
                .replaceAll("['\"()]", " "))
                .filter(l -> l.length() > 2)
                .map(String::toLowerCase)
                .map(StopList::removeEmojis)
                .filter(StopList::notIn)                
                .forEach(w -> 
                    tree.put(w, (tree.containsKey(w) ? tree.get(w) : 0) + 1L)
                );
    }
    
    
    @Override
    public String getName() {
        return "TagCloud";
    }
    
}
