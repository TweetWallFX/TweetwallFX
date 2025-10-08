/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 TweetWallFX
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

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.util.JsonDataConverter;
import org.tweetwallfx.util.Stopwatch;

/**
 * Cache implementation for caches that simply forget an entry after a specified
 * time (e.g. 15 or 60 minutes).
 */
public class TimeToForgetCache extends URLContentCacheBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeToForgetCache.class);

    /**
     * Cache forgetting a cached entry after 15 minutes.
     */
    public static final TimeToForgetCache INSTANCE_15M = new TimeToForgetCache("15m");

    /**
     * Cache forgetting a cached entry after 60 minutes.
     */
    public static final TimeToForgetCache INSTANCE_60M = new TimeToForgetCache("60m");

    private TimeToForgetCache(final String suffix) {
        super("timeToForgetCache" + suffix);
    }

    public <T> T getJson(final URI uri, final Class<T> type) {
        final URLContent urlc = Stopwatch.measure(
                () -> super.getCachedOrLoad(uri.toString()),
                duration -> LOGGER.info("URI call to {} took {}", uri, duration));
        return JsonDataConverter.convertFromInputStream(urlc.getInputStream(), type);
    }

    public List<?> getJsonList(final URI uri) {
        return getJson(uri, List.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getJsonMap(final URI uri) {
        return getJson(uri, Map.class);
    }
}
