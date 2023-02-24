/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 TweetWallFX
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
package org.tweetwallfx.tweet.impl.twitter4j;

import org.tweetwallfx.tweet.api.entry.TweetEntry;
import twitter4j.v1.TweetEntity;

import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

public class BaseTwitterTweetEntry<T extends TweetEntity> implements TweetEntry {

    private final T t;

    public BaseTwitterTweetEntry(T t) {
        this.t = t;
    }

    protected T getT() {
        return t;
    }

    @Override
    public final String getText() {
        return t.getText();
    }

    @Override
    public final int getStart() {
        return t.getStart();
    }

    @Override
    public final int getEnd() {
        return t.getEnd();
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "text", getText(),
                "start", getStart(),
                "end", getEnd()
        ), super.toString());
    }
}
