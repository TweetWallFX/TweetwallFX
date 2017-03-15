package org.tweetwallfx.controls.dataprovider;

import java.net.URL;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.xml.XmlConfiguration;

public final class TweetCache {
    private static final Logger log = LogManager.getLogger(TweetCache.class);

    private final CacheManager cacheManager;
    private final Cache<Long, CachedMedia> imageCache;

    public static TweetCache getInstance() {
        return new TweetCache();
    }

    TweetCache() {
        URL myUrl = getClass().getResource("TweetCache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        cacheManager.init();
        // init caches
        imageCache = cacheManager.getCache("imageCache", Long.class, CachedMedia.class);
        CacheEventListener<? super Object, ? super Object> logCacheEvent = event -> {
            log.info("Media " + event.getType());
        };
        imageCache.getRuntimeConfiguration().registerCacheEventListener(logCacheEvent,
                EventOrdering.UNORDERED, EventFiring.ASYNCHRONOUS, EnumSet.allOf(EventType.class));
    }


    boolean hasCachedMedia(long mediaId) {
        return imageCache.containsKey(Long.valueOf(mediaId));
    }

    Optional<CachedMedia> getCachedMedia(long mediaId) {
        return Optional.ofNullable(imageCache.get(Long.valueOf(mediaId)));
    }

    void putCachedMedia(long mediaId, CachedMedia image) {
        imageCache.put(Long.valueOf(mediaId), image);
    }
}
