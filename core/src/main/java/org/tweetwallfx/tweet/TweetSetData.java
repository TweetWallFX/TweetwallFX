/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.tweetwallfx.tweet.api.TweetFilterQuery;
import org.tweetwallfx.tweet.api.TweetStream;
import org.tweetwallfx.tweet.api.Tweeter;

public final class TweetSetData {

    public static final Comparator<Map.Entry<String, Long>> COMPARATOR = Comparator.comparingLong(Map.Entry::getValue);
    private final Tweeter tweeter;
    private final String searchText;
    private final TweetStream tweetStream;

    public TweetSetData(final Tweeter tweeter, final String searchText) {
        this(tweeter, searchText, 5);
    }

    public TweetSetData(final Tweeter tweeter, final String searchText, final int transferSize) {
        this.tweeter = Objects.requireNonNull(tweeter, "tweeter must not be null!");
        this.searchText = searchText;
        TweetFilterQuery query = new TweetFilterQuery().track(Pattern.compile(" [oO][rR] ").splitAsStream(searchText).toArray(n -> new String[n]));
        tweetStream = tweeter.createTweetStream(query);
    }

    public String getSearchText() {
        return searchText;
    }

    public TweetStream getTweetStream() {
        return tweetStream;
    }

    public Tweeter getTweeter() {
        return tweeter;
    }
}
