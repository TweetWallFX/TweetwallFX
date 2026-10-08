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

import static org.assertj.core.api.Assertions.assertThat;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.mixedItems;

import java.util.List;

import org.junit.jupiter.api.Test;

class MatrixLayoutTest {

    private static final MosaicArea AREA = new MosaicArea(900, 700, 100, 50, 10, 8, 5, 4);
    private static final double CELL_WIDTH = 900.0 / 5 - 10;
    private static final double CELL_HEIGHT = 700.0 / 4 - 8;

    @Test
    void replicatesHistoricCellGeometry() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.MATRIX)
                .layout(new ListMosaicItemSource(mixedItems(30)), AREA);

        assertThat(tiles).hasSize(20);

        for (int column = 0; column < 5; column++) {
            for (int row = 0; row < 4; row++) {
                final MosaicTile tile = tiles.get(column * 4 + row);
                assertThat(tile.width()).isEqualTo(CELL_WIDTH);
                assertThat(tile.height()).isEqualTo(CELL_HEIGHT);
                assertThat(tile.x()).isEqualTo(100 + column * (CELL_WIDTH + 10) + 5);
                assertThat(tile.y()).isEqualTo(50 + row * (CELL_HEIGHT + 8) + 4);
            }
        }
    }

    @Test
    void stopsWhenSourceIsExhausted() {
        final List<MosaicItem> items = mixedItems(7);
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.MATRIX)
                .layout(new ListMosaicItemSource(items), AREA);

        assertThat(tiles).hasSize(7);
        assertThat(tiles.get(0).item()).isEqualTo(items.get(0));
        // column-major: index 4 is column 1, row 0
        assertThat(tiles.get(4).item()).isEqualTo(items.get(4));
        assertThat(tiles.get(4).x()).isEqualTo(100 + (CELL_WIDTH + 10) + 5);
        assertThat(tiles.get(4).y()).isEqualTo(50 + 8 / 2.0);
    }

    @Test
    void emptySourceYieldsNoTiles() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.MATRIX)
                .layout(new ListMosaicItemSource(List.of()), AREA);
        assertThat(tiles).isEmpty();
    }

    @Test
    void stretchesImagesToIdenticalCells() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.MATRIX)
                .layout(new ListMosaicItemSource(List.of(
                        MosaicItem.of(600, 400),
                        MosaicItem.of(400, 600))), AREA);

        assertThat(tiles).hasSize(2);
        // both tiles are identical cells regardless of the item orientation
        assertThat(tiles.get(0).width()).isEqualTo(tiles.get(1).width());
        assertThat(tiles.get(0).height()).isEqualTo(tiles.get(1).height());
        assertThat(tiles.get(0).width() / tiles.get(0).height())
                .isNotEqualTo(tiles.get(0).item().aspectRatio());
    }
}
