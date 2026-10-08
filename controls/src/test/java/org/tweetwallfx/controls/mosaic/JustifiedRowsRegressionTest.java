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
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertHonorsAspect;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoHorizontalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoOverlap;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertNoVerticalOverflow;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.assertWithinContentBox;
import static org.tweetwallfx.controls.mosaic.LayoutAssertions.bandsBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Regression guards for the cinema JUSTIFIED_ROWS layout: every band,
 * including the last one, must span the full content width (the layout used to
 * clamp the last band, leaving a gap of up to several hundred pixels on the
 * right edge) while honouring aspect ratios and staying inside the area.
 */
class JustifiedRowsRegressionTest {

    private static final int CINEMA_WIDTH = 1860;
    private static final int CINEMA_HEIGHT = 555;
    private static final int CINEMA_LAYOUT_X = 35;
    private static final int CINEMA_LAYOUT_Y = 305;
    private static final int GAP_X = 10;
    private static final int GAP_Y = 8;

    /**
     * The last band may be clamped (and thus narrowed) by up to five percent,
     * matching the placement rule in {@code StripLayout}; the historic defect
     * produced gaps of 100 to 750 pixels.
     */
    private static final double MAX_LAST_BAND_GAP_RATIO = 0.05;

    @ParameterizedTest
    @CsvSource({ "30, 6", "24, 12", "18, 18", "36, 0", "50, 10", "40, 20", "30, 30" })
    void everyBandSpansTheFullContentWidth(final int landscapes, final int portraits) {
        final MosaicArea area = new MosaicArea(CINEMA_WIDTH, CINEMA_HEIGHT, CINEMA_LAYOUT_X,
                CINEMA_LAYOUT_Y, GAP_X, GAP_Y, 12, 5);

        for (final long seed : new long[] { 1, 42, 7 }) {
            assertRowsLayout(area, mixed(landscapes, portraits, seed));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3 })
    void sideAreaBandsSpanTheFullContentWidth(final int mixIndex) {
        final MosaicArea area = new MosaicArea(1000, 650, 900, 210, GAP_X, GAP_Y, 6, 5);
        final int[][] mixes = { { 30, 6 }, { 24, 12 }, { 18, 18 }, { 36, 0 } };
        final int[] mix = mixes[mixIndex];

        for (final long seed : new long[] { 1, 42, 7 }) {
            assertRowsLayout(area, mixed(mix[0], mix[1], seed));
        }
    }

    private static void assertRowsLayout(final MosaicArea area, final List<MosaicItem> items) {
        final List<MosaicTile> tiles = MosaicLayouts.create(LayoutType.JUSTIFIED_ROWS)
                .layout(new ListMosaicItemSource(new ArrayList<>(items)), area);

        assertThat(tiles).isNotEmpty();
        assertWithinContentBox(tiles, area);
        assertNoOverlap(tiles);
        assertHonorsAspect(tiles);
        assertNoVerticalOverflow(tiles, area);
        assertNoHorizontalOverflow(tiles, area);

        final double contentRight = area.layoutX() + area.width() - area.gapX() / 2.0;
        final double maxGap = area.width() * MAX_LAST_BAND_GAP_RATIO;

        for (final List<MosaicTile> band : bandsBy(tiles, true)) {
            double maxBandRight = 0;

            for (final MosaicTile tile : band) {
                maxBandRight = Math.max(maxBandRight, tile.x() + tile.width());
            }

            assertThat(contentRight - maxBandRight)
                    .as("every band must span the full content width")
                    .isLessThanOrEqualTo(maxGap);
        }
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
