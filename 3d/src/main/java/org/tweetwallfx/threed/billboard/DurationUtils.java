/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.threed.billboard;

import javafx.util.Duration;

/**
 * Some Duration conversions
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class DurationUtils {

    public static Duration toNanos(double val) {
        return Duration.millis(
                (val / 1000000) * 1000
        );
    }

    public static Duration toNanos(Duration dur) {
        double milVal = dur.toMillis();
        double nanos = milVal / 1000000;
        return Duration.millis(nanos);
    }

    public static Duration fpsToMillis(long fps) {
        return Duration.millis(((1.0 / fps) * 1000));
    }

    public static Duration fpsToNanos(long fps) {
        return Duration.millis((((1.0 / fps) * 1000) / 1000000));
    }
}
