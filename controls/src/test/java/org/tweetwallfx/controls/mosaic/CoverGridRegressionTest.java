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
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.TOLERANCE;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertBandsFillWidth;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertHonorsAspect;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoHorizontalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoOverlap;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoVerticalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertWithinContentBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Regression guards for the cinema COVER_GRID layout: nearly every available
 * tile must be placed and the content must stay vertically centered for all
 * orientation mixes (the layout used to leave the lower right corner almost
 * empty while discarding most of the items). Exact bottom fill is not required
 * because a band is never shrunk by more than five percent; the remainder is
 * split evenly above and below the centered content.
 */
class CoverGridRegressionTest {

    private static final int CINEMA_WIDTH = 1860;
    private static final int CINEMA_HEIGHT = 555;
    private static final int CINEMA_LAYOUT_X = 35;
    private static final int CINEMA_LAYOUT_Y = 305;
    private static final int GAP_X = 10;
    private static final int GAP_Y = 8;
    private static final double MIN_TILE_RATIO = 0.75;
    private static final double MAX_BOTTOM_SLACK_RATIO = 0.2;

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3 })
    void fullWidthCoverGridPlacesNearlyEveryTile(final int mixIndex) {
        final MosaicArea area = new MosaicArea(CINEMA_WIDTH, CINEMA_HEIGHT, CINEMA_LAYOUT_X,
                CINEMA_LAYOUT_Y, GAP_X, GAP_Y, 9, 4);
        final int[][] mixes = { { 30, 6 }, { 24, 12 }, { 18, 18 }, { 36, 0 } };
        final int[] mix = mixes[mixIndex];

        for (final long seed : new long[] { 1, 42, 7 }) {
            assertCinemaLayout(area, mixed(mix[0], mix[1], seed));
        }
    }

    @ParameterizedTest
    @CsvSource({ "30, 6", "24, 12", "18, 18", "36, 0" })
    void denseCoverGridPlacesNearlyEveryTile(final int landscapes, final int portraits) {
        final MosaicArea area = new MosaicArea(CINEMA_WIDTH, CINEMA_HEIGHT, CINEMA_LAYOUT_X,
                CINEMA_LAYOUT_Y, GAP_X, GAP_Y, 12, 5);

        for (final long seed : new long[] { 1, 42, 7 }) {
            assertCinemaLayout(area, mixed(landscapes * 5 / 3, portraits * 5 / 3, seed));
        }
    }

    @ParameterizedTest
    @CsvSource({ "30, 6", "24, 12", "18, 18", "36, 0" })
    void sideCoverGridPlacesNearlyEveryTile(final int landscapes, final int portraits) {
        final MosaicArea area = new MosaicArea(1000, 650, 900, 210, GAP_X, GAP_Y, 6, 5);

        for (final long seed : new long[] { 1, 42, 7 }) {
            assertCinemaLayout(area, mixed(landscapes, portraits, seed));
        }
    }

    private static void assertCinemaLayout(final MosaicArea area, final List<MosaicItem> items) {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.COVER_GRID)
                .layout(new ListMosaicItemSource(new ArrayList<>(items)), area);

        assertThat(tiles.size())
                .as("layout must use nearly all available cells")
                .isGreaterThanOrEqualTo((int) Math.ceil(area.maxTiles() * MIN_TILE_RATIO));
        assertWithinContentBox(tiles, area);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertNoVerticalOverflow(tiles, area);
        assertNoHorizontalOverflow(tiles, area);
        assertBandsFillWidth(tiles, area);

        final double contentTop = area.layoutY() + area.gapY() / 2.0;
        final double contentBottom = area.layoutY() + area.height() - area.gapY() / 2.0;
        double minTop = Double.POSITIVE_INFINITY;
        double maxBottom = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            minTop = Math.min(minTop, tile.y());
            maxBottom = Math.max(maxBottom, tile.y() + tile.height());
        }

        assertThat(Math.abs((minTop - contentTop) - (contentBottom - maxBottom)))
                .as("content must be vertically centered")
                .isLessThanOrEqualTo(TOLERANCE);
        assertThat(contentBottom - maxBottom)
                .as("centered content must not leave more than the allowed slack")
                .isLessThanOrEqualTo((contentBottom - contentTop) * MAX_BOTTOM_SLACK_RATIO);
    }

    private static List<MosaicItem> mixed(final int landscapes, final int portraits,
            final long seed) {
        final List<MosaicItem> items = new ArrayList<>();
        final Random random = new Random(seed);

        for (int i = 0; i < landscapes + portraits; i++) {
            if (i < landscapes) {
                items.add(MosaicItem.of(600 + random.nextInt(4), 400 + random.nextInt(4)));
            } else {
                items.add(MosaicItem.of(400 + random.nextInt(4), 600 + random.nextInt(4)));
            }
        }

        Collections.shuffle(items, random);
        return items;
    }
}
