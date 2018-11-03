/*
 * The MIT License
 *
 * Copyright 2015-2018 TweetWallFX
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
package org.tweetwallfx.transitions;

import javafx.animation.Transition;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class FontSizeTransition extends Transition {

    private final Text node;
    private final double startSize;
    private final double toSize;

    public FontSizeTransition(
            final Duration duration, final Text node,
            final double startSize, final double toSize) {
        setCycleDuration(duration);
        this.node = node;
        this.startSize = startSize;
        this.toSize = toSize;
    }

    public FontSizeTransition(
            final Duration duration, final Text node) {
        this(duration, node, Double.NaN, Double.NaN);
    }

    public FontSizeTransition withSize(final double startSize, final double toSize) {
        return new FontSizeTransition(
                getCycleDuration(), node,
                startSize, toSize);
    }

    @Override
    protected void interpolate(final double frac) {
        if (!Double.isNaN(startSize)) {
            node.setFont(Font.font(node.getFont().getFamily(), startSize + frac * (toSize - startSize)));
        }
    }
}
