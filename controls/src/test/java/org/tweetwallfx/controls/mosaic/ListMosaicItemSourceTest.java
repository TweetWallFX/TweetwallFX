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

class ListMosaicItemSourceTest {

    @Test
    void servesItemsInOrder() {
        final List<MosaicItem> items = mixedItems(3);
        final ListMosaicItemSource source = new ListMosaicItemSource(items);

        assertThat(source.remaining()).isEqualTo(3);
        assertThat(source.next()).contains(items.get(0));
        assertThat(source.next()).contains(items.get(1));
        assertThat(source.next()).contains(items.get(2));
        assertThat(source.next()).isEmpty();
        assertThat(source.remaining()).isZero();
    }

    @Test
    void nextMatchingSkipsButKeepsNonMatches() {
        final MosaicItem landscape = MosaicItem.of(600, 400);
        final MosaicItem portrait = MosaicItem.of(400, 600);
        final MosaicItem otherLandscape = MosaicItem.of(640, 360);
        final ListMosaicItemSource source = new ListMosaicItemSource(
                List.of(landscape, portrait, otherLandscape));

        assertThat(source.nextMatching(item -> item.aspectRatio() < 1)).contains(portrait);
        assertThat(source.remaining()).isEqualTo(2);
        assertThat(source.next()).contains(landscape);
        assertThat(source.nextMatching(item -> item.aspectRatio() < 1)).isEmpty();
        assertThat(source.next()).contains(otherLandscape);
        assertThat(source.remaining()).isZero();
    }
}
