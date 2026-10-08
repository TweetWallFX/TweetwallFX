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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A simple {@link MosaicItemSource} backed by a plain list of items.
 */
public final class ListMosaicItemSource implements MosaicItemSource {

    private final List<MosaicItem> remaining;

    /**
     * Creates a source serving the given items in list order.
     *
     * @param items the items to serve
     */
    public ListMosaicItemSource(final List<MosaicItem> items) {
        this.remaining = new ArrayList<>(Objects.requireNonNull(items, "items"));
    }

    @Override
    public Optional<MosaicItem> next() {
        if (remaining.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(remaining.remove(0));
    }

    @Override
    public Optional<MosaicItem> nextMatching(final Predicate<MosaicItem> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        for (int i = 0; i < remaining.size(); i++) {
            final MosaicItem candidate = remaining.get(i);
            if (predicate.test(candidate)) {
                remaining.remove(i);
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    @Override
    public int remaining() {
        return remaining.size();
    }
}
