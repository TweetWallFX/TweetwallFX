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

import java.util.Objects;

/**
 * Factory for the {@link MosaicLayout} implementations selectable via
 * {@link LayoutType}.
 */
public final class MosaicLayouts {

    private MosaicLayouts() {
        // prevent instantiation
    }

    /**
     * Creates the {@link MosaicLayout} for the given {@code layoutType}.
     *
     * @param layoutType the layout type to create the layout for
     *
     * @return the created layout
     */
    public static MosaicLayout create(final LayoutType layoutType) {
        return switch (Objects.requireNonNull(layoutType, "layoutType")) {
            case MATRIX -> new MatrixLayout();
            case JUSTIFIED_ROWS -> new StripLayout(false, false);
            case JUSTIFIED_COLUMNS -> new StripLayout(true, false);
            case COVER_GRID -> new StripLayout(false, true);
        };
    }
}
