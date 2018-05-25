/*
 * The MIT License
 *
 * Copyright 2017 TweetWallFX
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
package org.tweetwallfx.devoxx17be.exhibition;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.value.ObservableValue;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Flip Out around X-axis animation.
 * 
 * Heavily inspired by canned animations from Jasper Potts.
 * 
 * Port of FlipOutX from Animate.css http://daneden.me/animate by Dan Eden
 * 
 * @author Sven Reimers
 */
public class FlipOutXTransition extends Transition {
    private final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    private Camera oldCamera;
    private final Node node;
    private final Timeline timeline;
    
    /**
     * Create new FlipOutXTransition
     * 
     * @param node The node to affect
     */
    public FlipOutXTransition(final Node node) {
        this.node = node;
        timeline = new Timeline(
                    new KeyFrame(Duration.millis(0), 
                        new KeyValue(node.rotateProperty(), 0, EASE),
                        new KeyValue(node.opacityProperty(), 1, EASE)
                    ),
                    new KeyFrame(Duration.millis(1000), 
                        new KeyValue(node.rotateProperty(), -90, EASE),
                        new KeyValue(node.opacityProperty(), 0, EASE)
                    )
            );
        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0.2));
        statusProperty().addListener((ObservableValue<? extends Status> ov, Status t, Status newStatus) -> {
            switch(newStatus) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
        
    }

    protected void starting() {
        node.setRotationAxis(Rotate.X_AXIS);
        oldCamera = node.getScene().getCamera();
        node.getScene().setCamera(new PerspectiveCamera());
    }

    protected void stopping() {
        node.setRotate(0);
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getScene().setCamera(oldCamera);
    }

    @Override
    protected void interpolate(double frac) {
        timeline.playFrom(Duration.seconds(frac));
        timeline.stop();    
    }
}
