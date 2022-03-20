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
package org.tweetwallfx.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Nullable value factory. Takes a nullable value and if it is null return the
 * non-null equivalent of its type. An empty list, set, map or a default value
 * provided.
 */
public final class Nullable {

    private Nullable() {
        // prevent instantiation
    }

    /**
     * Produces the given {@code value} if it is non-null or the provided
     * {@code defaultValue} otherwise.
     *
     * @param <T> the type of the value(s)
     *
     * @param value the value to turn nullable
     *
     * @param defaultValue the non-null default value
     *
     * @return the non-null value
     */
    public static <T> T valueOrDefault(final T value, final T defaultValue) {
        return null == value
                ? defaultValue
                : value;
    }

    /**
     * Produces a non-null unmodifiable List from the given {@code list}.
     *
     * @param <T> the type of the list elements
     *
     * @param list the original List
     *
     * @return the non-null unmodifiable List
     */
    public static <T> List<T> nullable(final List<T> list) {
        return List.copyOf(valueOrDefault(list, List.of()));
    }

    /**
     * Produces a non-null unmodifiable Set from the given {@code set}.
     *
     * @param <T> the type of the set elements
     *
     * @param set the original Set
     *
     * @return the non-null unmodifiable Set
     */
    public static <T> Set<T> nullable(final Set<T> set) {
        return Set.copyOf(valueOrDefault(set, Set.of()));
    }

    /**
     * Produces a non-null unmodifiable Map from the given {@code map}.
     *
     * @param <K> the type of the map elements keys
     *
     * @param <V> the type of the map elements values
     *
     * @param map the original Map
     *
     * @return the non-null unmodifiable Map
     */
    public static <K, V> Map<K, V> nullable(final Map<K, V> map) {
        return Map.copyOf(valueOrDefault(map, Map.of()));
    }
}
