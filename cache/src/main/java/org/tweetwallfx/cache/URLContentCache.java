/*
 * The MIT License
 *
 * Copyright 2018 TweetWallFX
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
package org.tweetwallfx.cache;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;

public final class URLContentCache {

    private static final Logger LOG = LogManager.getLogger(URLContentCache.class);
    private static final Cache<String, URLContent> URL_CONTENT_CACHE = CacheManagerProvider.getCache(
            "urlContent",
            String.class,
            URLContent.class);

    private URLContentCache() {
        // prevent instantiation
    }

    public static boolean hasCachedContent(final String urlString) {
        if (URL_CONTENT_CACHE.containsKey(urlString)) {
            LOG.debug("Content for '{}': - exists in cache");
            return true;
        }

        return false;
    }

    public static Optional<URLContent> getCachedContent(final String urlString) {
        LOG.debug("Getting Content for '{}'", urlString);
        return Optional.ofNullable(URL_CONTENT_CACHE.get(urlString));
    }

    public static void putCachedContent(final String urlString, final URLContent image) {
        LOG.debug("Setting Content for '{}'", urlString);
        URL_CONTENT_CACHE.put(urlString, image);
    }
}
