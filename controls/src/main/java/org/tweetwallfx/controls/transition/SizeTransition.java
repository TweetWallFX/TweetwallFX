/*
 * The MIT License
 *
 * Copyright 2014-2018 TweetWallFX
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
package org.tweetwallfx.controls.transition;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

public final class SizeTransition extends Transition {

    private final DoubleProperty widthProperty;
    private final DoubleProperty heightProperty;
    private final double startHeight;
    private final double startWidth;
    private final double targetHeight;
    private final double targetWidth;

    private SizeTransition(
            final Duration duration, final DoubleProperty widthProperty, final DoubleProperty heightProperty,
            final double startHeight, final double startWidth,
            final double targetHeight, final double targetWidth) {
        setCycleDuration(duration);
        this.widthProperty = widthProperty;
        this.heightProperty = heightProperty;
        this.startHeight = startHeight;
        this.startWidth = startWidth;
        this.targetHeight = targetHeight;
        this.targetWidth = targetWidth;
    }

    public SizeTransition(
            final Duration duration, final DoubleProperty widthProperty, final DoubleProperty heightProperty) {
        this(duration, widthProperty, heightProperty,
                Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public SizeTransition withWidth(final double startWidth, final double targetWidth) {
        return new SizeTransition(getCycleDuration(), this.widthProperty, this.heightProperty,
                this.startHeight, startWidth, this.targetHeight, targetWidth);
    }

    public SizeTransition withHeight(final double startHeight, final double targetHeight) {
        return new SizeTransition(getCycleDuration(), this.widthProperty, this.heightProperty,
                startHeight, this.startWidth, targetHeight, this.targetWidth);
    }

    @Override
    protected void interpolate(final double frac) {
        if (!Double.isNaN(startWidth)) {
            widthProperty.setValue(startWidth + frac * (targetWidth - startWidth));
        }

        if (!Double.isNaN(startHeight)) {
            heightProperty.setValue(startHeight + frac * (targetHeight - startHeight));
        }
    }
}
