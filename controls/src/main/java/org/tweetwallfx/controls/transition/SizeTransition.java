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
    private double startWidth = Double.NaN;
    private double startHeight = Double.NaN;
    private double targetHeight = Double.NaN;
    private double targetWidth = Double.NaN;

    public SizeTransition(final Duration duration, final DoubleProperty widthProperty, final DoubleProperty heightProperty) {
        setCycleDuration(duration);
        this.widthProperty = widthProperty;
        this.heightProperty = heightProperty;
    }

    public SizeTransition fromWidth(final double startWidth) {
        this.startWidth = startWidth;
        return this;
    }

    public SizeTransition fromHeight(final double startHeight) {
        this.startHeight = startHeight;
        return this;
    }

    public SizeTransition toWidth(final double targetWidth) {
        this.targetWidth = targetWidth;
        return this;
    }

    public SizeTransition toHeight(final double targetHeight) {
        this.targetHeight = targetHeight;
        return this;
    }

    @Override
    protected void interpolate(double frac) {
        if (!Double.isNaN(startWidth)) {
            widthProperty.setValue(startWidth + frac * (targetWidth - startWidth));
        }

        if (!Double.isNaN(startHeight)) {
            heightProperty.setValue(startHeight + frac * (targetHeight - startHeight));
        }
    }
}
