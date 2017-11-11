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
package org.tweetwallfx.tweet.impl.twitter4j;

import java.util.Map;
import java.util.TreeMap;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;
import twitter4j.MediaEntity;

final class TwitterMediaTweetEntry extends BaseTwitterTweetEntry<MediaEntity> implements MediaTweetEntry {

    TwitterMediaTweetEntry(final MediaEntity mediaEntity) {
        super(mediaEntity);
    }

    @Override
    public long getId() {
        return getT().getId();
    }

    @Override
    public String getMediaUrl() {
        return getT().getMediaURL();
    }

    @Override
    public Map<Integer, Size> getSizes() {
        final Map<Integer, Size> map = new TreeMap<>();

        getT().getSizes().entrySet().stream().forEach(e -> map.put(
                e.getKey(),
                MediaTweetEntry.createSize(
                        e.getValue().getWidth(),
                        e.getValue().getHeight(),
                        e.getValue().getResize())));

        return map;
    }

    @Override
    public MediaTweetEntryType getType() {
        return MediaTweetEntryType.valueOf(getT().getType());
    }
}
