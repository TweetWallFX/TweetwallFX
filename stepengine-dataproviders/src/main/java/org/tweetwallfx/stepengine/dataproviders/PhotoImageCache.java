/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.cache.URLContentCacheBase;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;

/**
 * Cache used to provide images for photos of e.g. a {@link Tweet}.
 */
public final class PhotoImageCache extends URLContentCacheBase {

    private static final Map<Integer, Function<MediaTweetEntry, String>> MTE_SIZE_TO_URL_FUNCTIONS;

    static {
        final Map<Integer, Function<MediaTweetEntry, String>> tmp = new HashMap<>();

        tmp.put(0, mte -> mte.getMediaUrl() + ":thumb");
        tmp.put(1, mte -> mte.getMediaUrl() + ":small");
        tmp.put(2, mte -> mte.getMediaUrl() + ":medium");
        tmp.put(3, mte -> mte.getMediaUrl() + ":large");

        MTE_SIZE_TO_URL_FUNCTIONS = Collections.unmodifiableMap(tmp);
    }

    /**
     * Cache instance.
     */
    public static final PhotoImageCache INSTANCE = new PhotoImageCache();

    private PhotoImageCache() {
        super("photoImage");
    }

    public void addToCacheAsync(final MediaTweetEntry mte) {
        getCachedOrLoad(
                mte,
                this::handleLoadedContent);
    }

    public URLContent getCached(final MediaTweetEntry mte) {
        return getCachedOrLoad(getImageUrlString(mte));
    }

    public void getCachedOrLoad(final MediaTweetEntry mte, final Consumer<URLContent> consumer) {
        getCachedOrLoad(
                getImageUrlString(mte),
                consumer);
    }

    private String getImageUrlString(final MediaTweetEntry mte) {
        final String urlString = MTE_SIZE_TO_URL_FUNCTIONS
                .getOrDefault(
                        mte.getSizes().keySet().stream().max(Comparator.naturalOrder()).orElse(Integer.MAX_VALUE),
                        this::unsupportedSize)
                .apply(mte);

        LogManager.getLogger(PhotoImageCache.class).info("MediaTweetEntry({}): {}", mte.getId(), urlString);
        return urlString;
    }

    private String unsupportedSize(final MediaTweetEntry mte) {
        throw new IllegalArgumentException("Illegal value");
    }

    private void handleLoadedContent(final URLContent urlc) {
        // do nothing
    }
}
