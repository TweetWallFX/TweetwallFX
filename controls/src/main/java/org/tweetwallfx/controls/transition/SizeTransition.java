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
package org.tweetwallfx.controls.transition;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

/**
 *
 * @author sven
 */
public final class SizeTransition extends Transition {
    
    private final DoubleProperty widthProperty;
    private final DoubleProperty heightProperty;
    private double startWidth;
    private double startHeight;
    private double targetHeight;
    private double targetWidth;

    public SizeTransition(Duration duration, DoubleProperty widthProperty, DoubleProperty heightProperty) {
        setCycleDuration(duration);
        this.widthProperty = widthProperty;
        this.heightProperty = heightProperty;
    }

    public void setFromWidth(double startWidth) {
        this.startWidth = startWidth;
    }

    public void setFromHeight(double startHeight) {
        this.startHeight = startHeight;
    }

    public void setToWidth(double targetWidth) {
        this.targetWidth = targetWidth;
    }

    public void setToHeight(double targetHeight) {
        this.targetHeight = targetHeight;
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
