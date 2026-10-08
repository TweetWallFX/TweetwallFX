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
 * The selectable layout strategies for an image mosaic.
 */
public enum LayoutType {

    /**
     * The historic fixed matrix: a {@code columns x rows} grid of identical
     * cells, stretching every image to its cell and thus ignoring the aspect
     * ratio.
     */
    MATRIX,

    /**
     * Justified rows (Flickr style): images are packed into horizontal bands
     * of varying height, each band stretched to fill the width exactly. Every
     * image keeps its exact aspect ratio; no image is cropped. Bands mix
     * portrait and landscape images freely.
     */
    JUSTIFIED_ROWS,

    /**
     * The transposed variant of {@link #JUSTIFIED_ROWS}: vertical bands
     * stretched to fill the height exactly. Better suited for portrait heavy
     * image sets.
     */
    JUSTIFIED_COLUMNS,

    /**
     * Gapless banded tiling: like {@link #JUSTIFIED_ROWS} but every band is
     * composed of a single orientation (all landscape or all portrait),
     * producing clean rows of consistently shaped tiles. Images are drawn
     * from the source in random order; when a band needs a matching
     * orientation the source is asked for another image. Cells always match
     * the image aspect ratio, so nothing is cropped or stretched.
     */
    COVER_GRID
}
