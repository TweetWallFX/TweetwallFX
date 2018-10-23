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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.ehcache.config.units.MemoryUnit;
import org.tweetwallfx.config.ConfigurationConverter;
import static org.tweetwallfx.util.ToString.*;

public class CacheSettings {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "cacheConfiguration";
    private String persistenceDirectoryName = "tweetwall-cache";
    private Map<String, CacheSetting> caches = Collections.emptyMap();

    public String getPersistenceDirectoryName() {
        return persistenceDirectoryName;
    }

    public void setPersistenceDirectoryName(final String persistenceDirectoryName) {
        Objects.requireNonNull(persistenceDirectoryName, "persistenceDirectoryName must not be null!");
        this.persistenceDirectoryName = persistenceDirectoryName;
    }

    public Map<String, CacheSetting> getCaches() {
        return caches;
    }

    public void setCaches(final Map<String, CacheSetting> caches) {
        Objects.requireNonNull(caches, "caches must not be null!");
        this.caches = caches;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "persistenceDirectoryName", getPersistenceDirectoryName(),
                "caches", getCaches()
        ), super.toString());
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

    public static class CacheSetting {

        private String keyType = null;
        private String valueType = null;
        private CacheExpiry expiry = null;
        private int contentLoaderThreads;
        private List<CacheResource> cacheResources = Collections.emptyList();

        public String getKeyType() {
            return keyType;
        }

        public void setKeyType(final String keyType) {
            this.keyType = keyType;
        }

        public String getValueType() {
            return valueType;
        }

        public void setValueType(final String valueType) {
            this.valueType = valueType;
        }

        public CacheExpiry getExpiry() {
            return expiry;
        }

        public void setExpiry(final CacheExpiry expiry) {
            this.expiry = expiry;
        }

        public int getContentLoaderThreads() {
            return contentLoaderThreads;
        }

        public void setContentLoaderThreads(int contentLoaderThreads) {
            this.contentLoaderThreads = contentLoaderThreads;
        }

        public List<CacheResource> getCacheResources() {
            return cacheResources;
        }

        public void setCacheResources(final List<CacheResource> cacheResources) {
            this.cacheResources = cacheResources;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "keyType", getKeyType(),
                    "valueType", getValueType(),
                    "expiry", getExpiry(),
                    "cacheResources", getCacheResources()
            ), super.toString());
        }
    }

    public static class CacheExpiry {

        private CacheExpiryType type = CacheExpiryType.NONE;
        private long amount;
        private ChronoUnit unit;

        public CacheExpiryType getType() {
            return type;
        }

        public void setType(final CacheExpiryType type) {
            this.type = Objects.requireNonNull(type, "type must not be null!");
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(final long amount) {
            this.amount = amount;
        }

        public ChronoUnit getUnit() {
            return unit;
        }

        public void setUnit(final ChronoUnit unit) {
            this.unit = Objects.requireNonNull(unit, "unit must not be null!");
        }

        public Duration produceDuration() {
            return Duration.of(amount, unit);
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "type", getType(),
                    "amount", getAmount(),
                    "unit", getUnit()
            ), super.toString());
        }
    }

    public enum CacheExpiryType {

        NONE,
        TIME_TO_IDLE,
        TIME_TO_LIVE;
    }

    public static class CacheResource {

        private CacheResourceType type;
        private long amount;
        private MemoryUnit unit;

        public CacheResourceType getType() {
            return type;
        }

        public void setType(final CacheResourceType type) {
            this.type = Objects.requireNonNull(type, "type must not be null!");
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(final long amount) {
            this.amount = amount;
        }

        public MemoryUnit getUnit() {
            return unit;
        }

        public void setUnit(final MemoryUnit unit) {
            this.unit = Objects.requireNonNull(unit, "unit must not be null!");
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "type", getType(),
                    "amount", getAmount(),
                    "unit", getUnit()
            ), super.toString());
        }
    }

    public enum CacheResourceType {

        DISK,
        HEAP,
        OFFHEAP;
    }
}
