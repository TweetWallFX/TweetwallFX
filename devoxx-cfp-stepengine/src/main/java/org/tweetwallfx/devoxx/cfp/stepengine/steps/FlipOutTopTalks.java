/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.devoxx.cfp.stepengine.steps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import javafx.scene.Node;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.devoxx.cfp.stepengine.dataprovider.TopTalksTodayDataProvider;
import org.tweetwallfx.devoxx.cfp.stepengine.dataprovider.TopTalksWeekDataProvider;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.transitions.FlipOutXTransition;

/**
 * Schedule Flip Out Animation Step
 */
public class FlipOutTopTalks implements Step {

    private final Predicate<MachineContext> skipPredicate;

    private FlipOutTopTalks(final Predicate<MachineContext> skipPredicate) {
        // prevent external instantiation
        this.skipPredicate = Objects.requireNonNull(skipPredicate, "skipPredicate must not be null");
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        Node node = wordleSkin.getNode().lookup("#scheduleNode");
        FlipOutXTransition flipOutXTransition = new FlipOutXTransition(node);
        flipOutXTransition.setOnFinished(e -> {
            wordleSkin.getPane().getChildren().remove(node);
            context.proceed();
        });
        flipOutXTransition.play();
    }

    @Override
    public boolean shouldSkip(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        return null == wordleSkin.getNode().lookup("#scheduleNode")
                || skipPredicate.test(context);
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link FlipOutTopTalks}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public FlipOutTopTalks create(final StepEngineSettings.StepDefinition stepDefinition) {
            final TopVotedType topVotedType = stepDefinition.getConfig(Config.class).getTopVotedType();

            switch (topVotedType) {
                case TODAY:
                    return new FlipOutTopTalks(mc -> mc.getDataProvider(TopTalksTodayDataProvider.class).getFilteredSessionData().isEmpty());

                case WEEK:
                    return new FlipOutTopTalks(mc -> mc.getDataProvider(TopTalksWeekDataProvider.class).getFilteredSessionData().isEmpty());

                default:
                    throw new IllegalArgumentException("TopVotedType " + topVotedType + " is not supported");
            }
        }

        @Override
        public Class<FlipOutTopTalks> getStepClass() {
            return FlipOutTopTalks.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepDefinition) {
            final TopVotedType topVotedType = stepDefinition.getConfig(Config.class).getTopVotedType();

            switch (topVotedType) {
                case TODAY:
                    return Arrays.asList(TopTalksTodayDataProvider.class);

                case WEEK:
                    return Arrays.asList(TopTalksWeekDataProvider.class);

                default:
                    throw new IllegalArgumentException("TopVotedType " + topVotedType + " is not supported");
            }
        }
    }

    /**
     * Configures the type of the Top Voted display to flip out.
     */
    public static class Config {

        private TopVotedType topVotedType = null;

        /**
         * Provides the type of the Top Voted display to flip out.
         *
         * @return the type to flip out
         */
        public TopVotedType getTopVotedType() {
            return Objects.requireNonNull(topVotedType, "topVotedType must not be null");
        }

        /**
         * Sets the type of the Top Voted display to flip out.
         *
         * @param topVotedType the type to flip out
         */
        public void setTopVotedType(final TopVotedType topVotedType) {
            this.topVotedType = Objects.requireNonNull(topVotedType, "topVotedType must not be null");
        }
    }

    /**
     * The type of top voted display to flip out.
     */
    public static enum TopVotedType {

        /**
         * The Top Voted Session of Today.
         */
        TODAY,
        /**
         * The Top Voted Session of the Week.
         */
        WEEK;
    }
}
