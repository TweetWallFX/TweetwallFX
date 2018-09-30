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
 */package org.tweetwallfx.controls.transition;

import javafx.animation.Transition;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public final class BlurTransition extends Transition {

    private final GaussianBlur blur;
    private double startRadius = Double.NaN;
    private double targetRadius = Double.NaN;

    public BlurTransition(final Duration duration, final GaussianBlur blur) {
        setCycleDuration(duration);
        this.blur = blur;
    }

    public BlurTransition fromRadius(final double startRadius) {
        this.startRadius = startRadius;
        return this;
    }

    public BlurTransition toRadius(final double targetRadius) {
        this.targetRadius = targetRadius;
        return this;
    }

    @Override
    protected void interpolate(final double frac) {
        if (!Double.isNaN(startRadius)) {
            blur.setRadius(startRadius + frac * (targetRadius - startRadius));
        }
    }
}
