/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2023 TweetWallFX
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;

import static org.tweetwallfx.cache.URLContent.NO_CONTENT;

/**
 * Caches the content urlString to their urlString.
 */
public abstract class URLContentCacheBase {

    private static final String MESSAGE_LOAD_FAILED = "{}: Failed to load content from {}";
    private static final Logger LOG = LoggerFactory.getLogger(URLContentCacheBase.class);
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("URLContentCache");
    private final String cacheName;
    private final Executor contentLoader;
    private final Cache<String, URLContent> urlContentCache;

    protected URLContentCacheBase(final String cacheName) {
        this(cacheName, initializeCache(cacheName), initializeExecutor(cacheName));
    }

    URLContentCacheBase(final String cacheName, final Cache<String, URLContent> urlContentCache, final Executor contentLoader) {
        this.cacheName = cacheName;
        this.urlContentCache = urlContentCache;
        this.contentLoader = contentLoader;
    }

    private static Cache<String, URLContent> initializeCache(String cacheName) {
        return CacheManagerProvider.getCache(
                cacheName,
                String.class,
                URLContent.class);
    }

    private static Executor initializeExecutor(String cacheName) {
        return createExecutor(
                Configuration.getInstance()
                        .getConfigTyped(CacheSettings.CONFIG_KEY, CacheSettings.class)
                        .caches()
                        .get(cacheName)
                        .contentLoaderThreads(),
                cacheName);
    }

    /**
     * Determines if the cache - at this moment - has a cache entry for the
     * {@code urlString}.
     *
     * @param urlString the URL of the content
     *
     * @return a boolean flag that if {@code true} indicates that the cache
     * currently has an entry for the {@code urlString}
     */
    public final boolean hasCachedContent(final String urlString) {
        if (urlContentCache.containsKey(urlString)) {
            LOG.debug("{}: Content for '{}': - exists in cache", cacheName, urlString);
            return true;
        }

        return false;
    }

    /**
     * Retrieves the current cache value of the cache entry with
     * {@code urlString} as key wrapped in an Optional instance.
     *
     * @param urlString the URL of the content
     *
     * @return an {@link Optional} containing the current cache content for the
     * {code urlString}
     */
    public final Optional<URLContent> getCachedContent(final String urlString) {
        LOG.debug("{}: Getting Content for '{}'", cacheName, urlString);
        return Optional.ofNullable(urlContentCache.get(urlString));
    }

    /**
     * Retrieves the cached content asyncronuously for {code urlString} and
     * passes it to {@code contentConsumer}. If no cached content exists the
     * content loaded and cached and then passed to {@code contentConsumer}.
     *
     * @param urlString the string of the URL content to get
     *
     * @return a Supplier of InputStream in case content was loaded or
     * {@code null}
     */
    public final URLContent getCachedOrLoad(final String urlString) {
        try {
            return getCachedOrLoadSync(urlString);
        } catch (IOException ex) {
            LOG.error(MESSAGE_LOAD_FAILED, cacheName, urlString, ex);
            return NO_CONTENT;
        }
    }

    /**
     * Retrieves the cached content asyncronuously for {code urlString} and
     * passes it to {@code contentConsumer}. If no cached content exists the
     * content loaded and cached and then passed to {@code contentConsumer}.
     *
     * @param urlString the string of the URL content to get
     *
     * @param contentConsumer the Consumer processing the content
     */
    public final void getCachedOrLoad(final String urlString, final Consumer<URLContent> contentConsumer) {
        Objects.requireNonNull(urlString, "urlString must not be null");
        Objects.requireNonNull(contentConsumer, "contentConsumer must not be null");

        contentLoader.execute(() -> {
            try {
                final URLContent content = getCachedOrLoadSync(urlString);
                contentConsumer.accept(content);
            } catch (final IOException ioe) {
                LOG.error(MESSAGE_LOAD_FAILED, cacheName, urlString, ioe);
            }
        });
    }

    private URLContent getCachedOrLoadSync(final String urlString) throws IOException {
        Objects.requireNonNull(urlString, "urlString must not be null");
        URLContent urlc = urlContentCache.get(urlString);

        if (null == urlc) {
            urlc = URLContent.of(urlString);
            putCachedContent(urlString, urlc);
        }

        return urlc;
    }

    /**
     * Adds the {@code content} to the cache under the {@code urlString} key.
     *
     * @param urlString the string of the URL to cache
     *
     * @param content the content to cache
     */
    public final void putCachedContent(final String urlString, final InputStream content) {
        try {
            putCachedContent(urlString, URLContent.of(content));
        } catch (IOException ex) {
            LOG.error("{}: Failed to read content from InputStream for {}", cacheName, urlString, ex);
        }
    }

    private void putCachedContent(final String urlString, final URLContent content) {
        LOG.debug("{}: Setting Content for '{}'", cacheName, urlString);
        urlContentCache.put(urlString, content);
    }

    /**
     * Loads the content from {@code urlString} and adds that content to the
     * cache under the key {@code urlString} in case loading the content
     * succeeded.
     *
     * @param urlString the string of the URL to cache
     */
    public final void putCachedContent(final String urlString) {
        putCachedContentAsync(urlString, null);
    }

    /**
     * Puts the content of the provided {@code urlString} into the cache (should
     * loading succeed).
     *
     * @param urlString the string of the URL to cache
     *
     * @param contentConsumer the Consumer processing the content
     */
    public final void putCachedContent(final String urlString, final Consumer<URLContent> contentConsumer) {
        putCachedContentAsync(urlString, contentConsumer);
    }

    private void putCachedContentAsync(final String urlString, final Consumer<URLContent> contentConsumer) {
        contentLoader.execute(() -> {
            try {
                final URLContent content = URLContent.of(urlString);
                putCachedContent(urlString, content);

                if (null != contentConsumer) {
                    contentConsumer.accept(content);
                }
            } catch (final IOException ioe) {
                LOG.error(MESSAGE_LOAD_FAILED, cacheName, urlString, ioe);
            }
        });
    }

    public static URLContentCacheBase getDefault() {
        return Default.INSTANCE;
    }

    private static Executor createExecutor(final int nrThreads, final String name) {
        final AtomicInteger counter = new AtomicInteger();
        final ThreadGroup threadGroup = new ThreadGroup(THREAD_GROUP, name);
        final ThreadFactory threadFactory = r -> {
            final Thread t = new Thread(threadGroup, r, "contentLoader" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        };

        if (nrThreads < 2) {
            return Executors.newSingleThreadExecutor(threadFactory);
        } else {
            return Executors.newFixedThreadPool(nrThreads, threadFactory);
        }
    }

    private static final class Default extends URLContentCacheBase {

        private static final Default INSTANCE = new Default();

        public Default() {
            super("default");
        }
    }
}
