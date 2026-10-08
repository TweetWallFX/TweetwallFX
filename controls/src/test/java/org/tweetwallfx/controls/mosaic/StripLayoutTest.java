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
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertAtMostMaxTiles;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertBandsFillHeight;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertBandsFillWidth;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertHonorsAspect;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoHorizontalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoOverlap;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoVerticalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertWithinContentBox;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.bandsBy;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.mixedItems;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.twoRatioItems;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StripLayoutTest {

    private static final MosaicArea AREA = new MosaicArea(900, 700, 100, 50, 10, 8, 5, 4);
    private static final MosaicArea SMALL_AREA = new MosaicArea(900, 700, 0, 0, 10, 8, 4, 2);

    @ParameterizedTest
    @EnumSource(value = LayoutType.class, names = "MATRIX", mode = EnumSource.Mode.EXCLUDE)
    void honorsAspectStaysWithinAreaAndDoesNotOverlap(final LayoutType layoutType) {
        final List<MosaicTile> tiles = MosaicLayouts.create(layoutType)
                .layout(new ListMosaicItemSource(mixedItems(40)), AREA);

        assertThat(tiles).isNotEmpty();
        assertAtMostMaxTiles(tiles, AREA);
        assertWithinContentBox(tiles, AREA);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertNoVerticalOverflow(tiles, AREA);
        assertNoHorizontalOverflow(tiles, AREA);
    }

    @Test
    void justifiedRowsFillEveryBandButTheLast() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(mixedItems(40)), AREA);

        assertBandsFillWidth(tiles, AREA);
    }

    @Test
    void justifiedColumnsFillEveryBandButTheLast() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_COLUMNS)
                .layout(new ListMosaicItemSource(mixedItems(40)), AREA);

        assertBandsFillHeight(tiles, AREA);
    }

    @Test
    void justifiedRowsWorkForTwoRatioInput() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(twoRatioItems(12, 8)), AREA);

        assertThat(tiles).isNotEmpty();
        assertWithinContentBox(tiles, AREA);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertBandsFillWidth(tiles, AREA);
    }

    @Test
    void justifiedRowsCentersContentVertically() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(List.of(MosaicItem.of(4000, 400),
                        MosaicItem.of(4000, 400))), AREA);

        assertThat(tiles).isNotEmpty();

        final double contentTop = AREA.layoutY() + AREA.gapY() / 2.0;
        final double contentBottom = AREA.layoutY() + AREA.height() - AREA.gapY() / 2.0;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            minY = Math.min(minY, tile.y());
            maxY = Math.max(maxY, tile.y() + tile.height());
        }

        assertThat(Math.abs((minY - contentTop) - (contentBottom - maxY)))
                .as("top and bottom slack must be equal")
                .isLessThan(1e-6);
    }

    @Test
    void justifiedColumnsCentersContentHorizontally() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_COLUMNS)
                .layout(new ListMosaicItemSource(List.of(MosaicItem.of(400, 4000),
                        MosaicItem.of(400, 4000))), AREA);

        assertThat(tiles).isNotEmpty();

        final double contentLeft = AREA.layoutX() + AREA.gapX() / 2.0;
        final double contentRight = AREA.layoutX() + AREA.width() - AREA.gapX() / 2.0;
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            minX = Math.min(minX, tile.x());
            maxX = Math.max(maxX, tile.x() + tile.width());
        }

        assertThat(Math.abs((minX - contentLeft) - (contentRight - maxX)))
                .as("left and right slack must be equal")
                .isLessThan(1e-6);
    }

    @Test
    void pullMatchingRespectsTheCap() {
        final ListMosaicItemSource source = new ListMosaicItemSource(List.of(
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400)));
        final List<MosaicItem> drawn = new ArrayList<>();

        final int added = StripLayout.pullMatching(source, drawn,
                item -> item.aspectRatio() >= 1.0, 2);

        assertThat(added).isEqualTo(2);
        assertThat(drawn).hasSize(2);
        assertThat(source.remaining()).isEqualTo(3);
    }

    @Test
    void pullMatchingWithNonPositiveCapConsumesNothing() {
        final ListMosaicItemSource source = new ListMosaicItemSource(
                List.of(MosaicItem.of(600, 400)));
        final List<MosaicItem> drawn = new ArrayList<>();

        assertThat(StripLayout.pullMatching(source, drawn, item -> true, 0)).isZero();
        assertThat(drawn).isEmpty();
        assertThat(source.remaining()).isEqualTo(1);
    }

    @Test
    void pullMatchingDrainsWhenCapExceedsAvailable() {
        final ListMosaicItemSource source = new ListMosaicItemSource(List.of(
                MosaicItem.of(600, 400), MosaicItem.of(600, 400)));
        final List<MosaicItem> drawn = new ArrayList<>();

        assertThat(StripLayout.pullMatching(source, drawn, item -> true, 10)).isEqualTo(2);
        assertThat(drawn).hasSize(2);
        assertThat(source.remaining()).isZero();
    }

    @Test
    void coverGridBandsAreOrientationPureForTwoRatioInput() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.COVER_GRID)
                .layout(new ListMosaicItemSource(twoRatioItems(12, 8)), AREA);

        assertThat(tiles).isNotEmpty();
        assertAtMostMaxTiles(tiles, AREA);
        assertWithinContentBox(tiles, AREA);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertBandsFillWidth(tiles, AREA);

        for (final List<MosaicTile> band : bandsBy(tiles, true)) {
            final boolean firstIsLandscape = band.get(0).item().aspectRatio() >= 1;

            for (final MosaicTile tile : band) {
                assertThat(tile.item().aspectRatio() >= 1)
                        .as("band must contain a single orientation only")
                        .isEqualTo(firstIsLandscape);
            }
        }
    }

    @Test
    void coverGridRequestsMoreImagesWhenABandStarves() {
        final List<MosaicItem> drawn = new ArrayList<>(List.of(
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(400, 600),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400),
                MosaicItem.of(600, 400)));
        final List<MosaicItem> reserve = twoRatioItems(0, 2);
        final List<MosaicItem> all = new ArrayList<>(drawn);
        all.addAll(reserve);
        final ListMosaicItemSource source = new ListMosaicItemSource(all);

        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.COVER_GRID)
                .layout(source, SMALL_AREA);

        // the two reserved portraits were requested from the source to
        // complete the starving portrait band
        assertThat(source.remaining()).isZero();
        // the reserved portraits are not necessarily placed: two portraits
        // cannot form a full width band and a shrunk stub band is rejected,
        // so the winning composition may consist of landscape bands only
        assertWithinContentBox(tiles, SMALL_AREA);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertBandsFillWidth(tiles, SMALL_AREA);
    }

    @Test
    void exhaustedSourceDrainsAndStillYieldsAValidLayout() {
        final List<MosaicItem> items = mixedItems(5);
        final ListMosaicItemSource source = new ListMosaicItemSource(items);

        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(source, AREA);

        // all items were requested, but geometry may leave one unplaced when
        // an exactly filling packing with fewer tiles exists
        assertThat(source.remaining()).isZero();
        assertThat(tiles.size()).isBetween(1, items.size());
        assertWithinContentBox(tiles, AREA);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
    }

    @Test
    void extremeAspectItemStaysWithinArea() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(List.of(MosaicItem.of(4000, 400))), AREA);

        assertThat(tiles).hasSize(1);
        assertWithinContentBox(tiles, AREA);
        assertHonorsAspect(tiles);
    }

    @Test
    void emptySourceYieldsNoTiles() {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(List.of()), AREA);
        assertThat(tiles).isEmpty();
    }

    @Test
    void justifiedRowsNeverDrawMoreThanMaxTiles() {
        final ListMosaicItemSource source = new ListMosaicItemSource(mixedItems(100));

        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(source, AREA);

        assertThat(tiles.size()).isLessThanOrEqualTo(AREA.maxTiles());
        assertThat(source.remaining()).isEqualTo(100 - AREA.maxTiles());
    }
}
