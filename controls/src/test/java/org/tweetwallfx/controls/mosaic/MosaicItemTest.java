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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MosaicItemTest {

    @Test
    void computesAspectRatio() {
        assertThat(MosaicItem.of(600, 400).aspectRatio()).isEqualTo(1.5);
        assertThat(MosaicItem.of(400, 600).aspectRatio()).isEqualTo(400.0 / 600.0);
    }

    @Test
    void rejectsInvalidDimensions() {
        assertThatThrownBy(() -> MosaicItem.of(0, 400)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MosaicItem.of(600, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MosaicItem.of(Double.NaN, 400))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MosaicItem.of(1e308, 1e-308))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void carriesPayload() {
        final Object payload = new Object();
        final MosaicItem item = new MosaicItem(600, 400, payload);
        assertThat(item.payload()).isSameAs(payload);
        assertThat(MosaicItem.of(600, 400).payload()).isNull();
    }
}
