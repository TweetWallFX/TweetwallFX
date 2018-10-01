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
import javafx.scene.Node;
import javafx.util.Duration;

public final class LocationTransition extends Transition {

    private final Node node;
    private final double startX;
    private final double startY;
    private final double targetX;
    private final double targetY;

    public LocationTransition(
            final Duration duration, final Node node,
            final double startX, final double startY,
            final double targetX, final double targetY) {
        setCycleDuration(duration);
        this.node = node;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public LocationTransition(
            final Duration duration, final Node node) {
        this(duration, node,
                Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public LocationTransition withX(final double startX, final double targetX) {
        return new LocationTransition(
                getCycleDuration(), this.node,
                startX, this.startY, targetX, this.targetY);
    }

    public LocationTransition withY(final double startY, final double targetY) {
        return new LocationTransition(
                getCycleDuration(), this.node,
                this.startX, startY, this.targetX, targetY);
    }

    @Override
    protected void interpolate(final double frac) {
        if (!Double.isNaN(startX)) {
            node.setLayoutX(startX + frac * (targetX - startX));
        }

        if (!Double.isNaN(startY)) {
            node.setLayoutY(startY + frac * (targetY - startY));
        }
    }
}
