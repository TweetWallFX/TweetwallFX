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
import java.util.Optional;

/**
 * The historic fixed matrix layout: a {@code columns x rows} grid of
 * identical cells. Cell size and position are computed exactly like the
 * original mosaic steps computed them, hence images are stretched to their
 * cell and the aspect ratio is ignored.
 */
final class MatrixLayout implements MosaicLayout {

    @Override
    public List<MosaicTile> layout(final MosaicItemSource source, final MosaicArea area) {
        final double cellWidth = area.width() / area.columns() - area.gapX();
        final double cellHeight = area.height() / area.rows() - area.gapY();
        final List<MosaicTile> tiles = new ArrayList<>(area.maxTiles());

        for (int column = 0; column < area.columns(); column++) {
            for (int row = 0; row < area.rows(); row++) {
                final Optional<MosaicItem> item = source.next();

                if (item.isEmpty()) {
                    return tiles;
                }

                tiles.add(new MosaicTile(
                        item.get(),
                        area.layoutX() + column * (cellWidth + area.gapX()) + area.gapX() / 2,
                        area.layoutY() + row * (cellHeight + area.gapY()) + area.gapY() / 2,
                        cellWidth,
                        cellHeight));
            }
        }

        return tiles;
    }
}
