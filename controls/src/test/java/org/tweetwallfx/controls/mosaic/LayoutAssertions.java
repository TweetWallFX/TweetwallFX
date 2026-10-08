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

import java.util.ArrayList;
import java.util.List;

/**
 * Shared invariant assertions for {@link MosaicLayout} implementations.
 */
final class LayoutAssertions {

    static final double TOLERANCE = 1e-6;

    private LayoutAssertions() {
        // prevent instantiation
    }

    static void assertWithinContentBox(final List<MosaicTile> tiles, final MosaicArea area) {
        final double minX = area.layoutX() + area.gapX() / 2 - TOLERANCE;
        final double maxX = area.layoutX() + area.width() - area.gapX() / 2 + TOLERANCE;
        final double minY = area.layoutY() + area.gapY() / 2 - TOLERANCE;
        final double maxY = area.layoutY() + area.height() - area.gapY() / 2 + TOLERANCE;

        for (final MosaicTile tile : tiles) {
            assertThat(tile.x()).as("tile x").isGreaterThanOrEqualTo(minX);
            assertThat(tile.y()).as("tile y").isGreaterThanOrEqualTo(minY);
            assertThat(tile.x() + tile.width()).as("tile right edge").isLessThanOrEqualTo(maxX);
            assertThat(tile.y() + tile.height()).as("tile bottom edge").isLessThanOrEqualTo(maxY);
        }
    }

    static void assertNoOverlap(final List<MosaicTile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = i + 1; j < tiles.size(); j++) {
                final MosaicTile a = tiles.get(i);
                final MosaicTile b = tiles.get(j);
                final boolean separated = a.x() + a.width() <= b.x() + TOLERANCE
                        || b.x() + b.width() <= a.x() + TOLERANCE
                        || a.y() + a.height() <= b.y() + TOLERANCE
                        || b.y() + b.height() <= a.y() + TOLERANCE;
                assertThat(separated).as("tiles %d and %d must not overlap", i, j).isTrue();
            }
        }
    }

    static void assertHonorsAspect(final List<MosaicTile> tiles) {
        for (final MosaicTile tile : tiles) {
            final double expected = tile.item().aspectRatio();
            final double actual = tile.width() / tile.height();
            assertThat(Math.abs(actual / expected - 1))
                    .as("tile aspect ratio for item %sx%s", tile.item().width(), tile.item().height())
                    .isLessThan(1e-9);
        }
    }

    static void assertAtMostMaxTiles(final List<MosaicTile> tiles, final MosaicArea area) {
        assertThat(tiles.size()).isLessThanOrEqualTo(area.maxTiles());
    }

    /**
     * Asserts that every horizontal band but the last one fills the content
     * width exactly (gaps included). The last band may be short when it was
     * clamped to fit the remaining vertical space.
     *
     * @param tiles the tiles in placement order
     * @param area the area
     */
    static void assertBandsFillWidth(final List<MosaicTile> tiles, final MosaicArea area) {
        final List<List<MosaicTile>> bands = bandsBy(tiles, true);
        assertBandsFillExtent(bands, area.width() - area.gapX(), area.gapX(), true);
    }

    /**
     * Asserts that every vertical band but the last one fills the content
     * height exactly (gaps included).
     *
     * @param tiles the tiles in placement order
     * @param area the area
     */
    static void assertBandsFillHeight(final List<MosaicTile> tiles, final MosaicArea area) {
        final List<List<MosaicTile>> bands = bandsBy(tiles, false);
        assertBandsFillExtent(bands, area.height() - area.gapY(), area.gapY(), false);
    }

    static void assertNoVerticalOverflow(final List<MosaicTile> tiles, final MosaicArea area) {
        final double extent = verticalExtent(tiles);
        assertThat(extent).isLessThanOrEqualTo(area.height() - area.gapY() + TOLERANCE);
    }

    static void assertNoHorizontalOverflow(final List<MosaicTile> tiles, final MosaicArea area) {
        final double extent = horizontalExtent(tiles);
        assertThat(extent).isLessThanOrEqualTo(area.width() - area.gapX() + TOLERANCE);
    }

    static double verticalExtent(final List<MosaicTile> tiles) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            min = Math.min(min, tile.y());
            max = Math.max(max, tile.y() + tile.height());
        }

        return max - min;
    }

    static double horizontalExtent(final List<MosaicTile> tiles) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            min = Math.min(min, tile.x());
            max = Math.max(max, tile.x() + tile.width());
        }

        return max - min;
    }

    private static void assertBandsFillExtent(final List<List<MosaicTile>> bands,
            final double contentExtent, final double gap, final boolean width) {
        for (int i = 0; i < bands.size(); i++) {
            final List<MosaicTile> band = bands.get(i);

            if (i == bands.size() - 1) {
                continue;
            }

            double sum = 0;

            for (final MosaicTile tile : band) {
                sum += width ? tile.width() : tile.height();
            }

            sum += (band.size() - 1) * gap;
            assertThat(Math.abs(sum - contentExtent))
                    .as("band %d must fill the content extent", i)
                    .isLessThan(TOLERANCE);
        }
    }

    /**
     * Groups tiles into bands by their shared across coordinate: consecutive
     * tiles with the same y (horizontal bands) or x (vertical bands) belong to
     * one band.
     *
     * @param tiles the tiles in placement order
     * @param horizontal {@code true} for horizontal bands (grouped by y),
     * {@code false} for vertical bands (grouped by x)
     *
     * @return the bands in placement order
     */
    static List<List<MosaicTile>> bandsBy(final List<MosaicTile> tiles,
            final boolean horizontal) {
        final List<List<MosaicTile>> bands = new ArrayList<>();

        for (final MosaicTile tile : tiles) {
            final double key = horizontal ? tile.y() : tile.x();

            if (bands.isEmpty() || Math.abs(bandKey(bands, horizontal) - key) > TOLERANCE) {
                bands.add(new ArrayList<>());
            }

            bands.get(bands.size() - 1).add(tile);
        }

        return bands;
    }

    private static double bandKey(final List<List<MosaicTile>> bands, final boolean horizontal) {
        final List<MosaicTile> band = bands.get(bands.size() - 1);
        final MosaicTile last = band.get(band.size() - 1);
        return horizontal ? last.y() : last.x();
    }

    static List<MosaicItem> twoRatioItems(final int landscapes, final int portraits) {
        final List<MosaicItem> items = new ArrayList<>();

        for (int i = 0; i < landscapes; i++) {
            items.add(MosaicItem.of(600, 400));
        }

        for (int i = 0; i < portraits; i++) {
            items.add(MosaicItem.of(400, 600));
        }

        return items;
    }

    static List<MosaicItem> mixedItems(final int count) {
        final double[][] dimensions = {
            { 600, 400 }, { 400, 600 }, { 500, 500 }, { 640, 360 },
            { 360, 640 }, { 800, 600 }, { 600, 800 }, { 1024, 576 },
            { 576, 1024 }, { 750, 500 }, { 450, 600 }, { 900, 600 }
        };
        final List<MosaicItem> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            final double[] dim = dimensions[i % dimensions.length];
            items.add(MosaicItem.of(dim[0], dim[1]));
        }

        return items;
    }
}
