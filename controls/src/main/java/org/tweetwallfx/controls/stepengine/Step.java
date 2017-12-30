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
import java.util.Collection;
import java.util.Collections;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.stepengine.config.StepEngineSettings;

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

    default Duration preferredStepDuration(final MachineContext context) {
        return Duration.ZERO;
    }

    default String getName() {
        return getClass().getName();
    }

    default boolean requiresPlatformThread() {
        return true;
    }

    /**
     * A Factory creating a {@link Step}.
     */
    interface Factory {

        /**
         * Returns the class of the Step this factory will create.
         *
         * @return the class of the Step this factory will create
         */
        Class<? extends Step> getStepClass();

        /**
         * Creates a Step.
         *
         * @param stepDefinition the settings object for which the Step is to be
         * created
         *
         * @return the created Step
         */
        Step create(final StepEngineSettings.StepDefinition stepDefinition);

        /**
         * Retrieves the classes of {@link DataProvider DataProviders} required
         * by the created {@link Step}.
         * <p>
         *
         * <b>Important: Any {@link DataProvider} that is used must be contained
         * within the returned Collection in order to be certain that the
         * DataProvider is instantiated. If it is not declared to be required it
         * is highly likely that it will not be instantiated and thus produce a
         * {@link NullPointerException} downstream.</b>
         *
         * @param stepDefinition the settings object for which the Step is to be
         * created
         *
         * @return a Collection containing the classes of
         * {@link DataProvider DataProviders} required by the created
         * {@link Step}
         */
        default Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepDefinition) {
            return Collections.emptyList();
        }
    }
}
