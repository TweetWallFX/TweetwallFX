/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwallfx.controls.stepengine;

import java.time.Duration;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;

/**
 * @author Jörg Michelberger
 */
public interface Step {

    default void initStep(final MachineContext context) {
    }

    default boolean shouldSkip(final MachineContext context) {
        return false;
    }

    default void prepareStep(final MachineContext context) {
    }

    void doStep(final MachineContext context);

    default void leaveStep(final MachineContext context) {
    }

    Duration preferredStepDuration(final MachineContext context);

    default String getName() {
        return getClass().getName();
    }

    default boolean requiresPlatformThread() {
        return true;
    }

    /**
     * A Factory creating a {@link DataProvider}.
     */
    interface Factory {

        /**
         * Returns the class of the Provider this factory will create via
         * {@link #create(org.tweetwallfx.tweet.api.TweetStream)}.
         *
         * @return the class of the Provider this factory will create
         */
        Class<? extends Step> getStepClass();

        /**
         * Creates a Step.
         *
         * @return the created Step
         */
        Step create();
    }
}
