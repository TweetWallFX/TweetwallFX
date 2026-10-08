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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Strip based layout packing items into bands that are stretched to fill the
 * area in one direction, while every tile keeps its item's exact aspect ratio.
 *
 * <p>
 * The target band size is determined by a binary search augmented by a
 * logarithmic grid of probe values so that all band size scales are covered.
 * The last band additionally grows towards the remaining free extent so that
 * it spans the full width whenever the remaining items allow it; a band that
 * cannot be placed without shrinking by more than five percent is dropped
 * rather than rendered as a short stub.
 * Candidate compositions are ranked by {@code tiles * fillRatio} where
 * {@code fillRatio} is the demanded extent relative to the area extent capped
 * at one. For the mixed band policies the score is additionally damped by a
 * structure factor that favours compositions whose band count matches the
 * preferred number of bands ({@link MosaicArea#rows()} for horizontal strips,
 * the column count for vertical ones) and whose average items per band matches
 * the preferred items per band (the column count for horizontal strips, again
 * swapped for vertical ones); the items-per-band deviation is two sided so
 * both rows crammed with tiny images and rows holding only a few oversized
 * ones are pushed towards the configured shape. The closeness of the total
 * <em>unclamped demand</em> (the extent the bands would need without the final
 * fitting clamp) serves as tie breaker. A sparse composition that happens to
 * match the area extent exactly therefore never beats a composition using far
 * more items, while a dense composition that leaves half the area empty never
 * beats a well filling one, and compositions deviating strongly from the
 * configured rows x columns shape are pushed back towards it without being
 * excluded outright. The orientation grouped policy ignores the structure
 * factor and keeps maximising the number of placed tiles.
 *
 * <p>
 * The packed content is centered within the content box along the band
 * stacking direction, so the remainder left when the items cannot fill the
 * whole extent is split evenly between both ends instead of accumulating at
 * the far end (bottom for horizontal strips, right edge for vertical ones).
 *
 * <p>
 * Two composition policies are supported:
 * <ul>
 * <li>mixed bands ({@code orientationGrouped == false}, used by
 * {@link LayoutType#JUSTIFIED_ROWS} and {@code JUSTIFIED_COLUMNS}): items are
 * taken strictly in source order,</li>
 * <li>orientation grouped bands ({@code orientationGrouped == true}, used by
 * {@link LayoutType#COVER_GRID}): each band only collects items of the same
 * orientation as its seed. If during the search a composition starves because
 * a band cannot find company of its orientation although items remain, the
 * source is asked for matching images (bounded top-up).</li>
 * </ul>
 */
final class StripLayout implements MosaicLayout {

    private static final int BINARY_SEARCH_STEPS = 64;
    private static final int GRID_SEARCH_STEPS = 128;
    private static final int MAX_TOP_UP_ROUNDS = 3;
    private static final double EPSILON = 1e-6;
    private static final double MIN_PLACED_BAND_FILL = 0.95;
    private static final double STRUCTURE_BAND_WEIGHT = 1.0;
    private static final double STRUCTURE_ITEM_WEIGHT = 2.0;

    private final boolean vertical;
    private final boolean orientationGrouped;

    /**
     * Creates a strip layout.
     *
     * @param vertical if {@code true} bands run vertically (columns layout),
     * otherwise horizontally (rows layout)
     * @param orientationGrouped if {@code true} each band only contains items
     * of a single orientation
     */
    StripLayout(final boolean vertical, final boolean orientationGrouped) {
        this.vertical = vertical;
        this.orientationGrouped = orientationGrouped;
    }

    @Override
    public List<MosaicTile> layout(final MosaicItemSource source, final MosaicArea area) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(area, "area");

        final List<MosaicItem> drawn = new ArrayList<>(area.maxTiles());
        while (drawn.size() < area.maxTiles()) {
            final Optional<MosaicItem> next = source.next();

            if (next.isEmpty()) {
                break;
            }

            drawn.add(next.get());
        }

        if (drawn.isEmpty()) {
            return List.of();
        }

        Packing best = search(drawn, area);

        if (orientationGrouped) {
            best = topUp(source, drawn, area, best);
        }

        return centerAlongDown(best.tiles(), area);
    }

    /**
     * Centers the packed content within the content box along the band stacking
     * direction, so the empty remainder left when the items cannot fill the
     * whole extent is split evenly instead of accumulating at the end of the
     * area.
     *
     * @param tiles the packed tiles
     * @param area the area
     *
     * @return the tiles shifted so the content is centered along the stacking
     * direction
     */
    private List<MosaicTile> centerAlongDown(final List<MosaicTile> tiles, final MosaicArea area) {
        if (tiles.isEmpty()) {
            return tiles;
        }

        final double contentDown = downExtent(area) - downGap(area);
        double minDown = Double.POSITIVE_INFINITY;
        double maxDown = Double.NEGATIVE_INFINITY;

        for (final MosaicTile tile : tiles) {
            final double down = vertical ? tile.x() : tile.y();
            final double size = vertical ? tile.width() : tile.height();
            minDown = Math.min(minDown, down);
            maxDown = Math.max(maxDown, down + size);
        }

        final double shift = downStart(area) - minDown
                + (contentDown - (maxDown - minDown)) / 2.0;

        if (Math.abs(shift) <= EPSILON) {
            return tiles;
        }

        final List<MosaicTile> centered = new ArrayList<>(tiles.size());

        for (final MosaicTile tile : tiles) {
            centered.add(vertical
                    ? new MosaicTile(tile.item(), tile.x() + shift, tile.y(), tile.width(),
                            tile.height())
                    : new MosaicTile(tile.item(), tile.x(), tile.y() + shift, tile.width(),
                            tile.height()));
        }

        return centered;
    }

    private Packing topUp(final MosaicItemSource source, final List<MosaicItem> drawn,
            final MosaicArea area, final Packing best) {
        final int topUpCap = Math.max(1, area.maxTiles() / 2);
        int added = 0;
        int rounds = 0;
        Packing current = best;

        while (current.shortage() && rounds < MAX_TOP_UP_ROUNDS && added < topUpCap
                && source.remaining() > 0) {
            int roundAdded = 0;

            if (current.shortageLandscape()) {
                roundAdded += pullMatching(source, drawn, item -> item.aspectRatio() >= 1.0,
                        topUpCap - added);
            }

            if (current.shortagePortrait()) {
                roundAdded += pullMatching(source, drawn, item -> item.aspectRatio() < 1.0,
                        topUpCap - added);
            }

            if (roundAdded == 0) {
                break;
            }

            added += roundAdded;
            current = search(drawn, area);
            rounds++;
        }

        return current;
    }

    static int pullMatching(final MosaicItemSource source, final List<MosaicItem> drawn,
            final Predicate<MosaicItem> wanted, final int cap) {
        int count = 0;

        while (count < cap) {
            final Optional<MosaicItem> match = source.nextMatching(wanted);

            if (match.isEmpty()) {
                break;
            }

            drawn.add(match.get());
            count++;
        }

        return count;
    }

    private Packing search(final List<MosaicItem> drawn, final MosaicArea area) {
        final double target = downExtent(area) - downGap(area);
        Packing best = pack(drawn, target, area);
        boolean shortage = best.shortage();
        boolean shortageLandscape = best.shortageLandscape();
        boolean shortagePortrait = best.shortagePortrait();
        double lo = EPSILON;
        double hi = target;

        for (int i = 0; i < BINARY_SEARCH_STEPS; i++) {
            final double mid = (lo + hi) / 2;
            final Packing candidate = pack(drawn, mid, area);

            if (isBetter(candidate, best, target, area)) {
                best = candidate;
            }

            if (candidate.shortage()) {
                shortage = true;
                shortageLandscape |= candidate.shortageLandscape();
                shortagePortrait |= candidate.shortagePortrait();
            }

            if (candidate.demandDown() <= target) {
                lo = mid;
            } else {
                hi = mid;
            }
        }

        final double gridRatio = Math.pow(EPSILON / target, 1.0 / GRID_SEARCH_STEPS);

        for (int i = 0; i <= GRID_SEARCH_STEPS; i++) {
            final double mid = target * Math.pow(gridRatio, i);
            final Packing candidate = pack(drawn, mid, area);

            if (isBetter(candidate, best, target, area)) {
                best = candidate;
            }

            if (candidate.shortage()) {
                shortage = true;
                shortageLandscape |= candidate.shortageLandscape();
                shortagePortrait |= candidate.shortagePortrait();
            }
        }

        final Packing atLow = pack(drawn, lo, area);

        if (isBetter(atLow, best, target, area)) {
            best = atLow;
        }

        if (atLow.shortage()) {
            shortage = true;
            shortageLandscape |= atLow.shortageLandscape();
            shortagePortrait |= atLow.shortagePortrait();
        }

        return new Packing(best.tiles(), best.demandDown(), best.bands(),
                best.itemDeviationSum(), shortage, shortageLandscape, shortagePortrait);
    }

    private boolean isBetter(final Packing candidate, final Packing current,
            final double target, final MosaicArea area) {
        final double candidateScore = score(candidate, target, area);
        final double currentScore = score(current, target, area);

        if (candidateScore != currentScore) {
            return candidateScore > currentScore;
        }

        return Math.abs(candidate.demandDown() - target)
                < Math.abs(current.demandDown() - target);
    }

    private double score(final Packing packing, final double target, final MosaicArea area) {
        final double fill = Math.min(1.0, packing.demandDown() / target);
        final double structure = orientationGrouped
                ? 1.0
                : structureFactor(packing, area);

        return packing.tiles().size() * fill * structure;
    }

    /**
     * Damping factor that nudges justified (non grouped) compositions towards
     * the configured {@code rows} x {@code columns} shape: it penalises a band
     * count that deviates from the preferred number of bands and an average
     * items-per-band count that deviates from the preferred items per band.
     * The latter is two sided so that both rows crammed with tiny images and
     * rows holding only a few oversized ones are pushed towards the configured
     * shape.
     */
    private double structureFactor(final Packing packing, final MosaicArea area) {
        if (packing.bands() == 0) {
            return 1.0;
        }

        final double bandDeviation = Math.abs(packing.bands() - (double) preferredBands(area))
                / preferredBands(area);
        final double itemsDeviation = packing.itemDeviationSum()
                / (packing.bands() * (double) preferredItems(area));

        return 1.0 / (1.0 + STRUCTURE_BAND_WEIGHT * bandDeviation
                + STRUCTURE_ITEM_WEIGHT * itemsDeviation);
    }

    private int preferredBands(final MosaicArea area) {
        return vertical ? area.columns() : area.rows();
    }

    private int preferredItems(final MosaicArea area) {
        return vertical ? area.rows() : area.columns();
    }

    private Packing pack(final List<MosaicItem> items, final double target, final MosaicArea area) {
        final double across = acrossExtent(area);
        final double down = downExtent(area);
        final double acrossGap = acrossGap(area);
        final double downGap = downGap(area);
        final double contentAcross = across - acrossGap;
        final double contentDown = down - downGap;
        final double startAcross = acrossStart(area);
        final double startDown = downStart(area);
        final int maxTiles = area.maxTiles();

        final boolean[] used = new boolean[items.size()];
        final List<MosaicTile> tiles = new ArrayList<>(Math.min(items.size(), maxTiles));
        int bands = 0;
        double itemDeviationSum = 0;
        double downCursor = 0;
        double demandCursor = 0;
        boolean shortage = false;
        boolean shortageLandscape = false;
        boolean shortagePortrait = false;

        while (tiles.size() < maxTiles) {
            final double remainingDown = contentDown - downCursor;
            final double growthTarget = Math.min(target, remainingDown);
            final int seedIndex = firstUnused(used);

            if (seedIndex < 0) {
                break;
            }

            used[seedIndex] = true;
            final MosaicItem seed = items.get(seedIndex);
            final boolean seedLandscape = seed.aspectRatio() >= 1.0;
            final List<MosaicItem> band = new ArrayList<>();
            band.add(seed);
            double sumUnit = unit(seed);

            while (true) {
                final double bandDown = (contentAcross - (band.size() - 1) * acrossGap) / sumUnit;

                if (bandDown <= growthTarget) {
                    break;
                }

                final int candidateIndex = orientationGrouped
                        ? firstUnusedMatching(items, used, seedLandscape)
                        : firstUnused(used);
                if (candidateIndex < 0) {
                    if (orientationGrouped && anyUnused(used)) {
                        shortage = true;

                        if (seedLandscape) {
                            shortageLandscape = true;
                        } else {
                            shortagePortrait = true;
                        }
                    }

                    break;
                }

                used[candidateIndex] = true;
                final MosaicItem candidate = items.get(candidateIndex);
                band.add(candidate);
                sumUnit += unit(candidate);
            }

            final int room = maxTiles - tiles.size();

            if (band.size() > room) {
                while (band.size() > room) {
                    final MosaicItem removed = band.remove(band.size() - 1);
                    sumUnit -= unit(removed);
                }
            }

            double bandDown = (contentAcross - (band.size() - 1) * acrossGap) / sumUnit;

            while (band.size() > 1 && bandDown <= EPSILON) {
                final MosaicItem removed = band.remove(band.size() - 1);
                sumUnit -= unit(removed);
                bandDown = (contentAcross - (band.size() - 1) * acrossGap) / sumUnit;
            }

            if (remainingDown <= Math.max(downGap, EPSILON) || bandDown <= EPSILON) {
                break;
            }

            if (bandDown > remainingDown
                    && remainingDown < bandDown * MIN_PLACED_BAND_FILL) {
                break;
            }

            demandCursor += bandDown;
            itemDeviationSum += Math.abs(band.size() - preferredItems(area));

            if (bandDown > remainingDown) {
                bandDown = remainingDown;
            }

            double acrossCursor = 0;

            for (final MosaicItem item : band) {
                final double tileAcross = bandDown * unit(item);
                final double absoluteAcross = startAcross + acrossCursor;
                final double absoluteDown = startDown + downCursor;

                if (vertical) {
                    tiles.add(new MosaicTile(item, absoluteDown, absoluteAcross, bandDown, tileAcross));
                } else {
                    tiles.add(new MosaicTile(item, absoluteAcross, absoluteDown, tileAcross, bandDown));
                }

                acrossCursor += tileAcross + acrossGap;
            }

            downCursor += bandDown + downGap;
            demandCursor += downGap;
            bands++;
        }

        final double demandDown = bands > 0 ? demandCursor - downGap : 0;
        return new Packing(tiles, demandDown, bands, itemDeviationSum, shortage, shortageLandscape,
                shortagePortrait);
    }

    private static int firstUnused(final boolean[] used) {
        for (int i = 0; i < used.length; i++) {
            if (!used[i]) {
                return i;
            }
        }
        return -1;
    }

    private static int firstUnusedMatching(final List<MosaicItem> items, final boolean[] used,
            final boolean landscape) {
        for (int i = 0; i < items.size(); i++) {
            if (!used[i] && (items.get(i).aspectRatio() >= 1.0) == landscape) {
                return i;
            }
        }
        return -1;
    }

    private static boolean anyUnused(final boolean[] used) {
        return firstUnused(used) >= 0;
    }

    private double unit(final MosaicItem item) {
        return vertical ? 1.0 / item.aspectRatio() : item.aspectRatio();
    }

    private double acrossExtent(final MosaicArea area) {
        return vertical ? area.height() : area.width();
    }

    private double downExtent(final MosaicArea area) {
        return vertical ? area.width() : area.height();
    }

    private double acrossGap(final MosaicArea area) {
        return vertical ? area.gapY() : area.gapX();
    }

    private double downGap(final MosaicArea area) {
        return vertical ? area.gapX() : area.gapY();
    }

    private double acrossStart(final MosaicArea area) {
        return (vertical ? area.layoutY() : area.layoutX()) + acrossGap(area) / 2;
    }

    private double downStart(final MosaicArea area) {
        return (vertical ? area.layoutX() : area.layoutY()) + downGap(area) / 2;
    }

    /**
     * The result of packing once for a given target. {@code demandDown} is the
     * unclamped extent the placed bands would need; it is used to compare
     * compositions during the binary search so that clamped (overflowing)
     * compositions are not mistaken for perfectly fitting ones. The band
     * statistics back the structure factor of the score.
     *
     * @param tiles the placed tiles
     * @param demandDown the unclamped extent the placed bands demand
     * @param bands the number of bands placed
     * @param itemDeviationSum the summed absolute deviation of each band's item
     * count from the preferred items per band
     * @param shortage {@code true} if any searched composition starved for
     * items of a single orientation
     * @param shortageLandscape {@code true} if any searched composition
     * starved for landscape items
     * @param shortagePortrait {@code true} if any searched composition
     * starved for portrait items
     */
    private record Packing(List<MosaicTile> tiles, double demandDown, int bands,
            double itemDeviationSum, boolean shortage, boolean shortageLandscape,
            boolean shortagePortrait) {
    }
}
