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
package org.tweetwallfx.tweet.api.entry;

public class EmojiTweetEntry implements TweetEntry {

    private final String emojiText;
    private final int codePointIndex;

    public EmojiTweetEntry(final String emojiText, final int codePointIndex) {
        this.emojiText = emojiText;
        this.codePointIndex = codePointIndex;
    }

    @Override
    public String getText() {
        return emojiText;
    }

    @Override
    public int getStart() {
        return codePointIndex;
    }

    @Override
    public int getEnd() {
        return codePointIndex;
    }

    @Override
    public String toString() {
        return "EmojiTweetEntry{"
                + "\n    text=" + getText()
                + "\n    start=" + getStart()
                + "\n    end=" + getEnd()
                + "\n} extends " + super.toString();
    }
}
