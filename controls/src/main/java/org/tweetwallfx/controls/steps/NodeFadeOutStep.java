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
package org.tweetwallfx.controls.steps;

import java.util.Objects;
import java.util.Set;
import javafx.animation.ParallelTransition;
import javafx.scene.Node;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.transitions.FlipOutXTransition;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * Configrable step that fades out a {@link Node} lookup up via its
 * {@link Config#getNodeSelector() nodeSelector}.
 */
public class NodeFadeOutStep implements Step {

    private final Config config;

    private NodeFadeOutStep(final Config config) {
        this.config = config;
    }

    @Override
    public void doStep(final StepEngine.MachineContext context) {
        final WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        final Set<Node> nodes = wordleSkin.getNode().lookupAll(config.getNodeSelector());

        final ParallelTransition fadeOutAll = new ParallelTransition();
        nodes.forEach(node ->  {
            fadeOutAll.getChildren().add(new FlipOutXTransition(node));
        });
        fadeOutAll.setOnFinished(e -> {
            wordleSkin.getPane().getChildren().removeAll(nodes);
            context.proceed();
        });
        fadeOutAll.play();
    }

    @Override
    public boolean shouldSkip(final StepEngine.MachineContext context) {
        final WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        return null == wordleSkin.getNode().lookup(config.getNodeSelector());
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link NodeFadeOutStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public NodeFadeOutStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            final Config config = stepDefinition.getConfig(Config.class);
            return new NodeFadeOutStep(config);
        }

        @Override
        public Class<NodeFadeOutStep> getStepClass() {
            return NodeFadeOutStep.class;
        }
    }

    public static class Config {

        private String nodeSelector;

        public String getNodeSelector() {
            return nodeSelector;
        }

        public void setNodeSelector(final String nodeSelector) {
            this.nodeSelector = Objects.requireNonNull(nodeSelector, "nodeSelector must not be null!");
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "nodeSelector", getNodeSelector()
            )) + " extends " + super.toString();
        }
    }
}
