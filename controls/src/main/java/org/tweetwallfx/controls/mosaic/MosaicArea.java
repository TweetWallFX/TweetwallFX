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
 * The area a {@link MosaicLayout} has to fill.
 *
 * <p>
 * The content box starts half a gap in from each edge, mirroring the historic
 * matrix layout: content starts at {@code layoutX + gapX / 2} and spans
 * {@code width - gapX}.
 *
 * @param width the total width of the area
 * @param height the total height of the area
 * @param layoutX absolute x offset of the area
 * @param layoutY absolute y offset of the area
 * @param gapX horizontal gap between tiles and to the left/right edge
 * @param gapY vertical gap between tiles and to the top/bottom edge
 * @param columns the number of columns: matrix layout grid width, strip
 * layouts preferred items per band (horizontal) or preferred band count
 * (vertical), and tile count hint
 * @param rows the number of rows: matrix layout grid height, strip layouts
 * preferred band count (horizontal) or preferred items per band (vertical), and
 * tile count hint
 */
public record MosaicArea(
        double width,
        double height,
        double layoutX,
        double layoutY,
        double gapX,
        double gapY,
        int columns,
        int rows) {

    public MosaicArea {
        if (Double.isNaN(width) || width <= 0) {
            throw new IllegalArgumentException("width must be > 0 but was " + width);
        }
        if (Double.isNaN(height) || height <= 0) {
            throw new IllegalArgumentException("height must be > 0 but was " + height);
        }
        if (Double.isNaN(gapX) || gapX < 0 || gapX >= width) {
            throw new IllegalArgumentException("gapX must be >= 0 and < width but was " + gapX);
        }
        if (Double.isNaN(gapY) || gapY < 0 || gapY >= height) {
            throw new IllegalArgumentException("gapY must be >= 0 and < height but was " + gapY);
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("columns must be > 0 but was " + columns);
        }
        if (rows <= 0) {
            throw new IllegalArgumentException("rows must be > 0 but was " + rows);
        }
    }

    /**
     * The maximum number of tiles any layout may place.
     *
     * @return columns * rows
     */
    public int maxTiles() {
        return columns * rows;
    }
}
