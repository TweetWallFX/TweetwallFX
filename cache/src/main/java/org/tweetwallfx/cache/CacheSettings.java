/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 TweetWallFX
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

import static org.tweetwallfx.util.Nullable.nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.tweetwallfx.config.ConfigurationConverter;

public record CacheSettings(
        String persistenceDirectoryName,
        Map<String, CacheSetting> caches) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "cacheConfiguration";

    public CacheSettings {
        persistenceDirectoryName = Objects.requireNonNullElse(persistenceDirectoryName, "tweetwall-cache");
        caches = nullable(caches);
    }

    @Override
    public Map<String, CacheSetting> caches() {
        return nullable(caches);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link CacheSettings#CONFIG_KEY} into {@link CacheSettings}.
     */
    public static final class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return CacheSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return CacheSettings.class;
        }
    }

    public static record CacheSetting(
            String keyType,
            String valueType,
            CacheExpiry expiry,
            Integer contentLoaderThreads,
            List<CacheResource> cacheResources) {

        public CacheSetting {
            Objects.requireNonNull(keyType, "keyType must not be null");
            Objects.requireNonNull(valueType, "valueType must not be null");
            contentLoaderThreads = Objects.requireNonNullElse(contentLoaderThreads, 0);
            cacheResources = nullable(cacheResources);
        }

        @Override
        public List<CacheResource> cacheResources() {
            return nullable(cacheResources);
        }
    }

    public static record CacheExpiry(
            CacheExpiryType type,
            Long amount,
            ChronoUnit unit) {

        public CacheExpiry {
            type = Objects.requireNonNullElse(type, CacheExpiryType.NONE);
            Objects.requireNonNull(amount, "amount must not be null");
            Objects.requireNonNull(unit, "unit must not be null");
        }

        public Duration produceDuration() {
            return Duration.of(amount, unit);
        }
    }

    public enum CacheExpiryType {

        NONE,
        TIME_TO_IDLE,
        TIME_TO_LIVE;
    }

    public static record CacheResource(
            CacheResourceType type,
            Long amount,
            MemUnit unit) {

        public CacheResource {
            Objects.requireNonNull(type, "type must not be null!");
            Objects.requireNonNull(amount, "amount must not be null!");
            if (type.requiresUnit) {
                Objects.requireNonNull(unit, "unit must not be null!");
            }
        }
    }

    public enum CacheResourceType {

        DISK(true),
        HEAP(false),
        OFFHEAP(true);
        private final boolean requiresUnit;

        CacheResourceType(final boolean requiresUnit) {
            this.requiresUnit = requiresUnit;
        }
    }

    /**
     * A memory quantity unit.
     */
    public enum MemUnit {
        /**
         * Bytes.
         */
        B,
        /**
         * Kilobytes.
         */
        KB,
        /**
         * Megabytes.
         */
        MB,
        /**
         * Gigabytes.
         */
        GB,
        /**
         * Terabytes.
         */
        TB,
        /**
         * Petabytes.
         */
        PB;
    }
}
