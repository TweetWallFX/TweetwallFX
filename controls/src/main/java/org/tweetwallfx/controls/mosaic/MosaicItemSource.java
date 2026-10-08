/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 TweetWallFX
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
package org.tweetwallfx.controls.mosaic;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Supplies items to a {@link MosaicLayout} in random order. Layouts draw from
 * the source while laying out; a source may serve more items than a layout
 * eventually places.
 */
public interface MosaicItemSource {

    /**
     * Returns the next item in random order, consuming it.
     *
     * @return the next item or {@link Optional#empty()} if exhausted
     */
    Optional<MosaicItem> next();

    /**
     * Returns the next item matching the given {@code predicate}, consuming
     * it. Items not matching are skipped but stay available for subsequent
     * calls.
     *
     * @param predicate the predicate to match
     *
     * @return the next matching item or {@link Optional#empty()} if no
     * remaining item matches
     */
    Optional<MosaicItem> nextMatching(Predicate<MosaicItem> predicate);

    /**
     * Returns the number of items still available (consumed and skipped
     * alike).
     *
     * @return the number of remaining items
     */
    int remaining();
}
