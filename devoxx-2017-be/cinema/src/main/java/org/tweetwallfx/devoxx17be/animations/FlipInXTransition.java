package org.tweetwallfx.devoxx17be.animations;

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
 * Animate a flip out x effect on the given node
 * 
 * Port of FlipOutX from Animate.css http://daneden.me/animate by Dan Eden
 * 
 * {@literal @}keyframes flipOutX {
 *     0% {
 *         transform: perspective(400px) rotateX(0deg);
 *         opacity: 1;
 *     }
 * 	100% {
 *         transform: perspective(400px) rotateX(90deg);
 *         opacity: 0;
 *     }
 * }
 *
 * @author Jasper Potts
 */
public class FlipInXTransition extends Transition {
    private final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    private Camera oldCamera;
    private final Node node;
    private final Timeline timeline;
    
    /**
     * Create new FlipOutXTransition
     * 
     * @param node The node to affect
     */
    public FlipInXTransition(final Node node) {
        this.node = node;
        this.node.setOpacity(0);
        timeline = new Timeline(
                    new KeyFrame(Duration.millis(0), 
                        new KeyValue(node.rotateProperty(), -90, EASE),
                        new KeyValue(node.opacityProperty(), 0, EASE)
                    ),
                    new KeyFrame(Duration.millis(400), 
                        new KeyValue(node.rotateProperty(), 10, EASE)
                    ),
                    new KeyFrame(Duration.millis(700), 
                        new KeyValue(node.rotateProperty(), -10, EASE)
                    ),
                    new KeyFrame(Duration.millis(1000), 
                        new KeyValue(node.rotateProperty(), 0, EASE),
                        new KeyValue(node.opacityProperty(), 1, EASE)
                    )
            );
        setCycleDuration(Duration.seconds(2));
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
        timeline.stop();    }
}
