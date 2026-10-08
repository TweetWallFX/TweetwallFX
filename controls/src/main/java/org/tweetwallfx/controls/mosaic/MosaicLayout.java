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

import java.util.List;

/**
 * A layout strategy placing items from a {@link MosaicItemSource} into a
 * {@link MosaicArea}.
 *
 * <p>
 * Implementations must guarantee for every returned tile:
 * <ul>
 * <li>it lies within the area's content box,</li>
 * <li>it does not overlap any other tile (up to floating point rounding),</li>
 * <li>it honors the item's aspect ratio unless
 * {@link LayoutType#MATRIX} is used,</li>
 * <li>it places at most {@link MosaicArea#maxTiles()} tiles and always
 * terminates.</li>
 * </ul>
 */
public sealed interface MosaicLayout permits MatrixLayout, StripLayout {

    /**
     * Lays out items drawn from the given {@code source} within the given
     * {@code area}.
     *
     * @param source the source to draw items from
     * @param area the area to fill
     *
     * @return the placed tiles in placement order, possibly empty
     */
    List<MosaicTile> layout(MosaicItemSource source, MosaicArea area);
}
