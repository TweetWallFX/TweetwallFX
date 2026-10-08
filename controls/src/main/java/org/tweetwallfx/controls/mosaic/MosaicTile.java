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

import java.util.Objects;

/**
 * A placed {@link MosaicItem}: absolute position and size within the mosaic
 * area. The rectangle is expected to honor the item's aspect ratio for all
 * layout types except {@link LayoutType#MATRIX}.
 *
 * @param item the placed item
 * @param x absolute x position of the tile
 * @param y absolute y position of the tile
 * @param width the placed width
 * @param height the placed height
 */
public record MosaicTile(MosaicItem item, double x, double y, double width, double height) {

    public MosaicTile {
        Objects.requireNonNull(item, "item must not be null");

        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(
                    "tile position must be finite but was " + x + "," + y);
        }
        if (!Double.isFinite(width) || width <= 0) {
            throw new IllegalArgumentException("width must be > 0 but was " + width);
        }
        if (!Double.isFinite(height) || height <= 0) {
            throw new IllegalArgumentException("height must be > 0 but was " + height);
        }
    }
}
