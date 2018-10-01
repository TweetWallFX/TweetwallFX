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
package org.tweetwallfx.stepengine.dataproviders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.EnumSet;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.xml.XmlConfiguration;

final class MediaCache {

    private static final Logger LOG = LogManager.getLogger(MediaCache.class);
    private static final CacheManager CACHE_MANAGER;
    private static final Cache<Long, CachedMedia> IMAGE_CACHE;

    static {
        final URL myUrl = MediaCache.class.getResource("MediaCache.xml");
        final XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);

        CACHE_MANAGER = CacheManagerBuilder.newCacheManager(xmlConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(CACHE_MANAGER::close, "cache-shutdown"));
        CACHE_MANAGER.init();

        // init caches
        IMAGE_CACHE = CACHE_MANAGER.getCache("mediaCache", Long.class, CachedMedia.class);
        IMAGE_CACHE.getRuntimeConfiguration().registerCacheEventListener(
                event -> LOG.debug("Media id: {} - {}", event.getKey(), event.getType()),
                EventOrdering.UNORDERED,
                EventFiring.ASYNCHRONOUS,
                EnumSet.allOf(EventType.class));
    }

    private MediaCache() {
        // prevent instantiation
    }

    public static boolean hasCachedMedia(final long mediaId) {
        if (IMAGE_CACHE.containsKey(mediaId)) {
            LOG.debug("Media id: " + mediaId + " - exists in cache");
            return true;
        }

        return false;
    }

    public static Optional<CachedMedia> getCachedMedia(final long mediaId) {
        return Optional.ofNullable(IMAGE_CACHE.get(mediaId));
    }

    public static void putCachedMedia(final long mediaId, final CachedMedia image) {
        IMAGE_CACHE.put(mediaId, image);
    }

    public static final class CachedMedia implements Externalizable {

        private static final long serialVersionUID = 1L;
        private byte[] data;

        public CachedMedia(final InputStream in) throws IOException {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            int read;

            while ((read = in.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
            }

            data = bout.toByteArray();
        }

        public CachedMedia() {
            data = new byte[0];
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeInt(data.length);
            out.write(data);
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            final int size = in.readInt();
            data = new byte[size];

            final int read = in.read(data);
            if (read != size) {
                throw new IOException("Unexpected amount of data read. Expected " + size + ", got " + read);
            }
        }
    }
}
