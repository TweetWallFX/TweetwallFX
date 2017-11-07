/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwall.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Support for generating readable String for use in toString() method.
 */
public class ToString {

    private ToString() {
        // prevent instantiation
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param key1 the key of entry one
     *
     * @param value1 the value of entry one
     *
     * @return the created map
     */
    public static <K, V> Map<K, V> map(
            final K key1, final V value1) {
        final Map<K, V> map = new LinkedHashMap<>();

        map.put(key1, value1);

        return map;
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param key1 the key of entry one
     *
     * @param value1 the value of entry one
     *
     * @param key2 the key of entry two
     *
     * @param value2 the value of entry two
     *
     * @return the created map
     */
    public static <K, V> Map<K, V> map(
            final K key1, final V value1,
            final K key2, final V value2) {
        final Map<K, V> map = new LinkedHashMap<>();

        map.put(key1, value1);
        map.put(key2, value2);

        return map;
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param key1 the key of entry one
     *
     * @param value1 the value of entry one
     *
     * @param key2 the key of entry two
     *
     * @param value2 the value of entry two
     *
     * @param key3 the key of entry three
     *
     * @param value3 the value of entry three
     *
     * @return the created map
     */
    public static <K, V> Map<K, V> map(
            final K key1, final V value1,
            final K key2, final V value2,
            final K key3, final V value3) {
        final Map<K, V> map = new LinkedHashMap<>();

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        return map;
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param key1 the key of entry one
     *
     * @param value1 the value of entry one
     *
     * @param key2 the key of entry two
     *
     * @param value2 the value of entry two
     *
     * @param key3 the key of entry three
     *
     * @param value3 the value of entry three
     *
     * @param key4 the key of entry four
     *
     * @param value4 the value of entry four
     *
     * @return the created map
     */
    public static <K, V> Map<K, V> map(
            final K key1, final V value1,
            final K key2, final V value2,
            final K key3, final V value3,
            final K key4, final V value4) {
        final Map<K, V> map = new LinkedHashMap<>();

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);

        return map;
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param key1 the key of entry one
     *
     * @param value1 the value of entry one
     *
     * @param key2 the key of entry two
     *
     * @param value2 the value of entry two
     *
     * @param key3 the key of entry three
     *
     * @param value3 the value of entry three
     *
     * @param key4 the key of entry four
     *
     * @param value4 the value of entry four
     *
     * @param key5 the key of entry five
     *
     * @param value5 the value of entry five
     *
     * @return the created map
     */
    public static <K, V> Map<K, V> map(
            final K key1, final V value1,
            final K key2, final V value2,
            final K key3, final V value3,
            final K key4, final V value4,
            final K key5, final V value5) {
        final Map<K, V> map = new LinkedHashMap<>();

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);

        return map;
    }

    /**
     * Creates a Map containing one entry with the provided values.
     *
     * @param <K> the type of the keys in the map
     *
     * @param <V> the type of the values in the map
     *
     * @param entries the entries that are to comprise the Map's values
     *
     * @return the created map
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> Map<K, V> mapOf(final Map.Entry<? extends K, ? extends V>... entries) {
        if (null == entries) {
            return Collections.emptyMap();
        }

        final Map<K, V> map = new LinkedHashMap<>(entries.length);

        Arrays.stream(entries)
                .filter(Objects::nonNull)
                .filter(e -> Objects.nonNull(e.getKey()))
                .forEach(e -> map.put(e.getKey(), e.getValue()));

        return map;
    }

    /**
     * Creates a Map.Entry with the provided values.
     *
     * @param <K> the type of the key in the entry
     *
     * @param <V> the type of the value in the entry
     *
     * @param key the key of the entry
     *
     * @param value the value of the entry
     *
     * @return the created entry
     */
    public static <K, V> Map.Entry<K, V> mapEntry(final K key, final V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * Creates a readable String for use in implementations of
     * {@link Object#toString()} methods.
     *
     * @param object the object for which the String shall be created
     *
     * @param parameters the parameters to list in the created String
     *
     * @return the created String
     */
    public static String createToString(final Object object, final Map<String, Object> parameters) {
        return object.getClass().getSimpleName() + Optional.ofNullable(parameters)
                .filter(m -> !m.isEmpty())
                .map(Map::entrySet)
                .map(Collection::stream)
                .map(s -> s
                .map(e -> e.getKey() + ": " + getValueToString(e.getValue()))
                .collect(Collectors.joining(",\n    ", " {\n    ", "\n}")))
                .orElse("");
    }

    private static String getValueToString(final Object object) {
        return getValueToStringImpl(object).replace("\n", "\n    ");
    }

    private static String getValueToStringImpl(final Object object) {
        if (null == object) {
            return "null";
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>) object;

            if (map.isEmpty()) {
                return "{}";
            }

            return map.entrySet()
                    .stream()
                    .map(e -> getValueToString(e.getKey()) + ": " + getValueToString(e.getValue()))
                    .collect(Collectors.joining(",\n    ", "{\n    ", "\n}"));
        } else if (object instanceof Iterable) {
            @SuppressWarnings("unchecked")
            final Iterable<?> iterable = (Iterable<?>) object;

            if (!iterable.iterator().hasNext()) {
                return "[]";
            }

            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(ToString::getValueToString)
                    .collect(Collectors.joining(",\n    ", "[\n    ", "\n]"));
        } else if (object.getClass().isArray()) {
            @SuppressWarnings("unchecked")
            final Object[] array = (Object[]) object;

            if (0 == array.length) {
                return "[]";
            }

            return Arrays.stream(array)
                    .map(ToString::getValueToString)
                    .collect(Collectors.joining(",\n    ", "[\n    ", "\n]"));
        } else {
            return String.valueOf(object);
        }
    }
}
