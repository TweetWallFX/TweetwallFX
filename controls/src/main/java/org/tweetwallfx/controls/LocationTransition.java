/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
 */package org.tweetwallfx.controls;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author sven
 */
class LocationTransition extends Transition {
    
    private final Node node;
    private double startX;
    private double startY;
    private double targetY;
    private double targetX;

    public LocationTransition(Duration duration, Node node) {
        setCycleDuration(duration);
        this.node = node;
    }

    public void setFromX(double startX) {
        this.startX = startX;
    }

    public void setFromY(double startY) {
        this.startY = startY;
    }

    public void setToX(double targetX) {
        this.targetX = targetX;
    }

    public void setToY(double targetY) {
        this.targetY = targetY;
    }

    @Override
    protected void interpolate(double frac) {
        if (!Double.isNaN(startX)) {
            node.setLayoutX(startX + frac * (targetX - startX));
        }
        if (!Double.isNaN(startY)) {
            node.setLayoutY(startY + frac * (targetY - startY));
        }
    }
    
}
