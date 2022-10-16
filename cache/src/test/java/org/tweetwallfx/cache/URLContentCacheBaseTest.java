/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.tweetwallfx.cache.URLContent.NO_CONTENT;

@MockitoSettings
class URLContentCacheBaseTest {
    @Mock
    private Cache<String, URLContent> urlContentCache;
    @Mock
    private ExecutorService contentLoader;
    @Mock
    private URLContent cachedValue;
    private URLContentCacheBase cacheBase;

    @BeforeEach
    void setUp() {
        cacheBase = new URLContentCacheBase("test", urlContentCache, contentLoader) {
        };
    }

    @Test
    void testGetDefault() {
        try (var cacheManagerProvider = mockStatic(CacheManagerProvider.class);
             var executors = mockStatic(Executors.class)) {
            cacheManagerProvider.when(() -> CacheManagerProvider.getCache("default", String.class,
                    URLContent.class)).thenReturn(urlContentCache);
            executors.when(() -> Executors.newFixedThreadPool(eq(2), isA(ThreadFactory.class))).thenReturn(contentLoader);
            assertThat(URLContentCacheBase.getDefault()).isNotNull();
            verifyNoMoreInteractions(urlContentCache, contentLoader, cachedValue);
        }
    }

    @Test
    void hasCachedContent() {
        when(urlContentCache.containsKey("file:///one")).thenReturn(false);
        when(urlContentCache.containsKey("file:///two")).thenReturn(true);
        assertThat(cacheBase.hasCachedContent("file:///one")).isFalse();
        assertThat(cacheBase.hasCachedContent("file:///two")).isTrue();
    }

    @Test
    void getCachedContent() {
        when(urlContentCache.get("file:///one")).thenReturn(null);
        when(urlContentCache.get("file:///two")).thenReturn(cachedValue);
        assertThat(cacheBase.getCachedContent("file:///one")).isEmpty();
        assertThat(cacheBase.getCachedContent("file:///two")).contains(cachedValue);
    }

    @Test
    void getCachedOrLoad() {
        when(urlContentCache.get("file:///one")).thenReturn(null);
        when(urlContentCache.get("file:///two")).thenReturn(cachedValue);
        assertThat(cacheBase.getCachedOrLoad("file:///one")).isEqualTo(NO_CONTENT);
        assertThat(cacheBase.getCachedOrLoad("file:///two")).isEqualTo(cachedValue);
    }

    @Test
    void putCachedContent() {
        cacheBase.putCachedContent("file:///one", InputStream.nullInputStream());
        verify(urlContentCache).put("file:///one", NO_CONTENT);
        verifyNoMoreInteractions(urlContentCache, contentLoader, cachedValue);
    }
}
