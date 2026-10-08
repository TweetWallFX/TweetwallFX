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

/**
 * An image-like item to be placed by a {@link MosaicLayout}. Only the
 * intrinsic dimensions matter for the layout; the optional payload carries
 * whatever the caller needs at rendering time (e.g. the decoded image).
 *
 * @param width the intrinsic width, must be &gt; 0
 * @param height the intrinsic height, must be &gt; 0
 * @param payload an opaque payload carried through the layout untouched
 */
public record MosaicItem(double width, double height, Object payload) {

    public MosaicItem {
        if (Double.isNaN(width) || width <= 0) {
            throw new IllegalArgumentException("width must be > 0 but was " + width);
        }
        if (Double.isNaN(height) || height <= 0) {
            throw new IllegalArgumentException("height must be > 0 but was " + height);
        }
        if (!Double.isFinite(width / height)) {
            throw new IllegalArgumentException("width / height must be finite but was " + width / height);
        }
    }

    /**
     * Creates a {@link MosaicItem} without payload.
     *
     * @param width the intrinsic width
     * @param height the intrinsic height
     *
     * @return the created MosaicItem
     */
    public static MosaicItem of(final double width, final double height) {
        return new MosaicItem(width, height, null);
    }

    /**
     * Returns the aspect ratio (width / height) of this item.
     *
     * @return the aspect ratio
     */
    public double aspectRatio() {
        return width / height;
    }
}
