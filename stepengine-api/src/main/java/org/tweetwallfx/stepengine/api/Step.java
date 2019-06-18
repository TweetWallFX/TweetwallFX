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
package org.tweetwallfx.stepengine.api;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * A Step in the {@link StepEngine}. It represents an amount of work being done
 * that may optionally take a very specific amount of time.
 */
public interface Step {

    /**
     * Initiates this {@link Step} prior to being included in the
     * {@link StepEngine}. This method called only after instantiation by the
     * {@link StepEngine}.
     *
     * @param context the MachineContext
     */
    default void initStep(final MachineContext context) {
        // by default do nothing
    }

    /**
     * Determines if this {@link Step} should be skipped in order to not be
     * processed in the current iteration of the {@link StepEngine}.
     *
     * By Default this method returns {@code false}.
     *
     * @param context the MachineContext
     *
     * @return a boolean flag indicating whether this {@link Step} should be
     * skipped in the current {@link StepEngine} iteration
     */
    default boolean shouldSkip(final MachineContext context) {
        return false;
    }

    /**
     * Performs this {@link Step}s action.
     *
     * @param context the MachineContext
     */
    void doStep(final MachineContext context);

    /**
     * Determines the preffered duration this Step is to run.
     *
     * No minimal step duration is signaled by {@link Duration#ZERO}. Upon
     * returning from
     * {@link #doStep(org.tweetwallfx.stepengine.api.StepEngine.MachineContext)}
     * the {@link Step} is processed immidiatly.
     *
     * In case of a positive {@link Duration} the {@link StepEngine} ensures
     * that the next {@link Step} is processed only after at least the returned
     * {@link Duration} has expired and the
     * {@link #doStep(org.tweetwallfx.stepengine.api.StepEngine.MachineContext)}
     * has returned.
     *
     * By Default this method returns {@link Duration#ZERO}.
     *
     * @param context the MachineContext
     *
     * @return the minimum {@link Duration} this step is to be processed
     */
    default Duration preferredStepDuration(final MachineContext context) {
        return Duration.ZERO;
    }

    /**
     * Declares that the
     * {@link #doStep(org.tweetwallfx.stepengine.api.StepEngine.MachineContext)}
     * call is to be performed on the FX Platform thread.
     *
     * By Default this method returns {@code true}.
     *
     * @return the flag indicating if
     * {@link #doStep(org.tweetwallfx.stepengine.api.StepEngine.MachineContext)}
     * is to be called on the FX Platform thread
     */
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
