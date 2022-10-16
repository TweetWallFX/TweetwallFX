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

import java.util.Map;
import java.util.Set;

/**
 * A {@link Cache} is a Map-like data structure that provides temporary storage
 * of application data.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class Cache<K, V> {

    private final org.ehcache.Cache<K, V> cache;

    Cache(final org.ehcache.Cache<K, V> cache) {
        this.cache = cache;
    }

    /**
     * Gets an entry from the cache.
     *
     * @param key the key whose associated value is to be returned
     *
     * @return the element, or null, if it does not exist.
     *
     * @throws NullPointerException if the key is null
     */
    public V get(final K key) {
        return cache.get(key);
    }

    /**
     * Gets a collection of entries from the {@link Cache}, returning them as a
     * {@link Map} of the values associated with the set of keys requested.
     *
     * @param keys The keys whose associated values are to be returned
     *
     * @return Map of entries that were found for the given keys
     */
    public Map<K, V> getAll(final Set<? extends K> keys) {
        return cache.getAll(keys);
    }

    /**
     * Determines if the {@link Cache} contains an entry for the specified key.
     *
     * @param key key whose presence in this cache is to be tested.
     *
     * @return {@code true} if this map contains a mapping for the specified key
     *
     * @throws NullPointerException in case key is {@code null}
     *
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(final K key) {
        return cache.containsKey(key);
    }

    /**
     * Associates the specified value with the specified key in the cache.
     *
     * @param key key with which the specified value is to be associated
     *
     * @param value value to be associated with the specified key
     *
     * @throws NullPointerException in case either the key or the value are
     * {@code null}
     *
     * @see java.util.Map#put(Object, Object)
     */
    public void put(final K key, final V value) {
        cache.put(key, value);
    }

    /**
     * Copies all of the entries from the specified map to the {@link Cache}.
     *
     * @param map mappings to be stored in this cache
     *
     * @throws NullPointerException in case either the map, any of its keys or
     * any of its values is {@code null}
     */
    public void putAll(final Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    /**
     * Atomically associates the specified key with the given value if it is not
     * already associated with a value.
     *
     * @param key key with which the specified value is to be associated
     *
     * @param value value to be associated with the specified key
     *
     * @return true if a value was set.
     *
     * @throws NullPointerException in case either the key or the value are
     * {@code null}
     */
    public boolean putIfAbsent(final K key, final V value) {
        return null != cache.putIfAbsent(key, value);
    }

    /**
     * Removes the mapping for a key from this cache if it is present.
     *
     * @param key key whose mapping is to be removed from the cache
     *
     * @return returns false if there was no matching key
     *
     * @throws NullPointerException in case the key is {@code null}
     */
    public boolean remove(final K key) {
        final boolean contained = containsKey(key);
        cache.remove(key);
        return contained;
    }

    /**
     * Atomically removes the mapping for a key only if currently mapped to the
     * given value.
     *
     * @param key key whose mapping is to be removed from the cache
     *
     * @param oldValue value expected to be associated with the specified key
     *
     * @return returns false if there was no matching key
     *
     * @throws NullPointerException in case the key is {@code null}
     */
    public boolean remove(final K key, final V oldValue) {
        return cache.remove(key, oldValue);
    }

    /**
     * Atomically replaces the entry for a key only if currently mapped to a
     * given value.
     *
     * @param key key with which the specified value is associated
     *
     * @param oldValue value expected to be associated with the specified key
     *
     * @param newValue value to be associated with the specified key
     *
     * @return {@code true} if the value was replaced
     *
     * @throws NullPointerException in case either the key or any of the values
     * are {@code null}
     */
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return cache.replace(key, oldValue, newValue);
    }

    /**
     * Removes entries for the specified keys.
     *
     * @param keys the keys to remove
     *
     * @throws NullPointerException in case either keys or any of its entries is
     * {@code null}
     */
    public void removeAll(final Set<? extends K> keys) {
        cache.removeAll(keys);
    }

    /**
     * Clears the contents of the cache.
     */
    public void clear() {
        cache.clear();
    }
}
