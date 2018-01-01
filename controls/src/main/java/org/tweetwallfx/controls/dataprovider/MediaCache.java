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

public enum MediaCache {
    INSTANCE;

    private final Logger log;
    private final CacheManager cacheManager;
    private final Cache<Long, CachedMedia> imageCache;

    private MediaCache() {
        URL myUrl = getClass().getResource("MediaCache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
        log = LogManager.getLogger(MediaCache.class);
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(cacheManager::close, "cache-shutdown"));
        cacheManager.init();
        // init caches
        imageCache = cacheManager.getCache("mediaCache", Long.class, CachedMedia.class);
        CacheEventListener<? super Object, ? super Object> logCacheEvent = event -> {
            log.debug("Media id: " + event.getKey() + " - " + event.getType());
        };
        imageCache.getRuntimeConfiguration().registerCacheEventListener(logCacheEvent,
                EventOrdering.UNORDERED, EventFiring.ASYNCHRONOUS, EnumSet.allOf(EventType.class));
    }

    public boolean hasCachedMedia(long mediaId) {
        if (imageCache.containsKey(mediaId)) {
            log.debug("Media id: " + mediaId + " - exists in cache");
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
