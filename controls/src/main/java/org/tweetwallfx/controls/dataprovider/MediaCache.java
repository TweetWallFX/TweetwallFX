/*
 * The MIT License
 *
 * Copyright 2016-2018 TweetWallFX
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

import java.net.URL;
import java.util.EnumSet;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.xml.XmlConfiguration;

public class MediaCache {

    private static final Logger LOG = LogManager.getLogger(MediaCache.class);
    public static final MediaCache INSTANCE = new MediaCache();
    private final CacheManager cacheManager;
    private final Cache<Long, CachedMedia> imageCache;

    private MediaCache() {
        URL myUrl = getClass().getResource("MediaCache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(cacheManager::close, "cache-shutdown"));
        cacheManager.init();
        // init caches
        imageCache = cacheManager.getCache("mediaCache", Long.class, CachedMedia.class);
        CacheEventListener<? super Object, ? super Object> logCacheEvent = event -> {
            LOG.debug("Media id: " + event.getKey() + " - " + event.getType());
        };
        imageCache.getRuntimeConfiguration().registerCacheEventListener(logCacheEvent,
                EventOrdering.UNORDERED, EventFiring.ASYNCHRONOUS, EnumSet.allOf(EventType.class));
    }

    public boolean hasCachedMedia(long mediaId) {
        if (imageCache.containsKey(mediaId)) {
            LOG.debug("Media id: " + mediaId + " - exists in cache");
            return true;
        }
        return false;
    }

    public Optional<CachedMedia> getCachedMedia(long mediaId) {
        return Optional.ofNullable(imageCache.get(mediaId));
    }

    public void putCachedMedia(long mediaId, CachedMedia image) {
        imageCache.put(mediaId, image);
    }
}
