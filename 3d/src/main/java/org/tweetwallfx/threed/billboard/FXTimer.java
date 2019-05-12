/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
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
/**
 * Timer.java
 *
 * Copyright (c) 2011-2014, JFXtras All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the organization nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.tweetwallfx.threed.billboard;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

/**
 * A timer class in the spirit of java.swing.Timer but using JavaFX properties.
 *
 * @author Tom Eugelink
 *
 */
/**
 * Added more properties for keeping track of time Time is is nanoTime
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class FXTimer {

    private static final Logger LOG = Logger.getLogger(FXTimer.class.getName());

    static {
        LOG.setLevel(Level.ALL);
    }
    /*==========================================================================
     *    Variables
     *///========================================================================
    public static final long minFPS = 10;
    public static final long targetFPS = 60;
    public static final long maxFPS = 120;
    public static final long OPTIMAL_TIME = 1000000000L / targetFPS;
    private static long frame = 0;

    public long now = 0;
    public long updateLength = 0;
    public long lastLoopTime = 0;
    public long lastFpsTime = 0;
    public float delta;

    private Runnable runnable;
    final private boolean isDaemon = true;
    volatile private java.util.Timer timer;

    final private AtomicReference<TimerTask> timerTaskAtomicReference = new AtomicReference<>(null);

// ==================================================================================================================
// CONSTRUCTOR
    public FXTimer() {
        this(null);
    }

    public FXTimer(final Runnable rbl) {
        this.runnable = rbl;
    }

// ==================================================================================================================
// PROPERTIES
    /**
     * delay: initial delay
     *
     * @return ObjectProperty representing the delay
     */
    public ObjectProperty<Duration> delayProperty() {
        return this.delay;
    }
    final private ObjectProperty<Duration> delay = new SimpleObjectProperty<>(this, "delay", Duration.millis(0));

    public Duration getDelay() {
        return this.delay.getValue();
    }

    public void setDelay(Duration value) {
        this.delay.setValue(value);
    }

    /**
     * cycleDuration: time between fires
     *
     * @return ObjectProperty representing cycleDuration
     */
    public ObjectProperty<Duration> cycleDurationProperty() {
        return this.cycleDuration;
    }
    final private ObjectProperty<Duration> cycleDuration = new SimpleObjectProperty<>(this, "cycleDuration", DurationUtils.fpsToMillis(targetFPS));

    public Duration getCycleDuration() {
        return this.cycleDuration.getValue();
    }

    public void setCycleDuration(long fps) {
        this.cycleDuration.setValue(DurationUtils.fpsToMillis(fps));
    }
    //==========================================================================
    private final LongProperty updateDelay = new SimpleLongProperty(0);

    public long getUpdateDelay() {
        return updateDelay.get();
    }

    public void setUpdateDelay(long value) {
        updateDelay.set(value);
    }

    public LongProperty updateDelayProperty() {
        return updateDelay;
    }

    /**
     * repeats: If flag is false, instructs the Timer to send only one action
     * event to its listeners.
     *
     * @return
     */
    final private ObjectProperty<Boolean> repeats = new SimpleObjectProperty<>(this, "repeats", Boolean.TRUE);

    /*==========================================================================
     *   Added Properties
     *///========================================================================
    private final ReadOnlyLongWrapper fps = new ReadOnlyLongWrapper(this, "FPS");

    public final long getFPS() {
        return fps.get();
    }

    public ReadOnlyLongProperty fpsProperty() {
        return fps.getReadOnlyProperty();
    }
    //==========================================================================
    private final ReadOnlyLongWrapper currentTime = new ReadOnlyLongWrapper(this, "currentTime");

    public long getCurrentTime() {
        return currentTime.get();
    }

    public ReadOnlyLongProperty currentTimeProperty() {
        return currentTime.getReadOnlyProperty();
    }
    //==========================================================================
    private final ReadOnlyLongWrapper elapsedTime = new ReadOnlyLongWrapper(this, "elapsedTime");

    public long getElapsedTime() {
        return elapsedTime.get();
    }

    public ReadOnlyLongProperty elapsedTimeProperty() {
        return elapsedTime.getReadOnlyProperty();
    }
    //==========================================================================
    private final ReadOnlyFloatWrapper deltaTime = new ReadOnlyFloatWrapper(this, "deltaTime");

    public float getDeltaTime() {
        return deltaTime.get();
    }

    public ReadOnlyFloatProperty deltaTimeProperty() {
        return deltaTime.getReadOnlyProperty();
    }
    //==========================================================================
    private final ReadOnlyLongWrapper startTime = new ReadOnlyLongWrapper(this, "startTime");

    public long getStartTime() {
        return startTime.get();
    }
    //==========================================================================
    private final ReadOnlyLongWrapper lastFrameTime = new ReadOnlyLongWrapper(this, "lastFrameTime");

    public long getLastFrameTime() {
        return lastFrameTime.get();
    }

    public ReadOnlyLongProperty lastFrameTimeProperty() {
        return lastFrameTime.getReadOnlyProperty();
    }
// =============================================================================

    /**
     * Start the timer
     *
     */
    synchronized public void start() {
        // check if the timer is already running
        if (timerTaskAtomicReference.get() != null) {
            LOG.severe("Timer already started");
            throw new IllegalStateException("Timer already started");
        }
        //init startTime
        startTime.set(System.nanoTime());
        // create loop task and schedule it
        final TimerTask loop = new TimerTask() {
            @Override
            public void run() {
                //update times
                now = System.nanoTime();
                updateLength = now - lastLoopTime;
                lastLoopTime = now;
                delta = updateLength / ((float) OPTIMAL_TIME);

                // update the frame counter
                lastFpsTime += updateLength;
                frame++;

                // update our FPS counter if a second has passed since
                // we last recorded
                if (lastFpsTime >= 1000000000) {
                    fps.set(frame / 2);
                    lastFpsTime = 0;
                    frame = 0;
                }
                //calculate time on one frame, update on the next
                if (frame % 2 == 0) {
                    //update properties
                    currentTime.set(now);
                    lastFrameTime.set(lastLoopTime);
                    deltaTime.set(delta);
                    elapsedTime.set(now - getStartTime());
                    delay.set(Duration.millis((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000D));
                    cycleDuration.set(DurationUtils.fpsToMillis(getFPS()));
                } else {
                    if (runnable != null) {
                        Platform.runLater(runnable);
                    }
                }
            }
        };
        if (timer == null) {
            timer = new java.util.Timer(isDaemon);
        }
        timer.schedule(loop, (long) this.delay.getValue().toMillis(), (long) this.cycleDuration.getValue().toMillis());
        // remember for future reference
        timerTaskAtomicReference.set(loop);
    }

    /**
     * stop the timer if running
     *
     */
    public void stop() {
        TimerTask lTimerTask = timerTaskAtomicReference.getAndSet(null);
        if (lTimerTask != null) {
            lTimerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * restart the timer
     *
     */
    public void restart() {
        stop();
        start();
    }

    public void setRunnable(final Runnable runnable) {
        this.runnable = runnable;
    }
}
