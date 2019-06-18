/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 TweetWallFX
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
package org.tweetwallfx.controls;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WordleLayout {

    private static final Logger LOG = LogManager.getLogger(WordleLayout.class);
    private static final double RADIUS = 5.0;
    private static final int DEG = 10;

    private final Random rand = new SecureRandom();
    private final Map<Word, Bounds> wordLayoutMap;
    private final Configuration configuration;
    private final CloudWordNodeFactory wordNodeFactory;
    private final WordleLayout initialLayoutSolution;

    private WordleLayout(Configuration configuration, WordleLayout initialLayoutSolution) {
        this.configuration = configuration;
        this.wordNodeFactory = CloudWordNodeFactory.createFactory(new CloudWordNodeFactory.Config(configuration.font, configuration.maxFontSize));
        this.initialLayoutSolution = initialLayoutSolution;
        this.wordLayoutMap = calcTagLayout();
    }

    public static WordleLayout createWordleLayout(Configuration configuration) {
        return new WordleLayout(configuration, null);
    }

    public static WordleLayout createWordleLayout(Configuration configuration, WordleLayout initialLayoutSolution) {
        return new WordleLayout(configuration, initialLayoutSolution);
    }

    public Map<Word, Bounds> getWordLayoutInfo() {
        return wordLayoutMap;
    }

    public double getFontSizeForWeight(double weight) {
        return wordNodeFactory.getFontSize(weight, configuration.minWeight, configuration.maxWeight);
    }

    public void fontSizeAdaption(Text text, double weight) {
        wordNodeFactory.fontSizeAdaption(text, wordNodeFactory.getFontSize(weight, configuration.minWeight, configuration.maxWeight));
    }

    public Text createTextNode(Word word) {
        Text textNode = wordNodeFactory.createTextNode(word.getText());
        fontSizeAdaption(textNode, word.getWeight());
        return textNode;
    }

    private Map<Word, Bounds> calcTagLayout() {
        List<Bounds> boundsList = new ArrayList<>(configuration.words.size());
        for (Word word : configuration.words) {
            if (null != initialLayoutSolution) {
                Bounds bounds = initialLayoutSolution.getWordLayoutInfo().get(word);
                boundsList.add(bounds);
            } else {
                boundsList.add(null);
            }
        }

        boolean doFinish = false;

        Text firstNode = createTextNode(configuration.words.get(0));
        double firstWidth = firstNode.getLayoutBounds().getWidth();
        double firstHeight = firstNode.getLayoutBounds().getHeight();

        if (null == boundsList.get(0)) {
            boundsList.set(0, new BoundingBox(-firstWidth / 2d,
                    -firstHeight / 2d, firstWidth, firstHeight));
        }

        for (int i = 1; i < configuration.words.size(); ++i) {
            if (null != boundsList.get(i)) {
                continue;
            }
            Word word = configuration.words.get(i);
            Text textNode = createTextNode(word);
            double width = textNode.getLayoutBounds().getWidth();
            double height = textNode.getLayoutBounds().getHeight();

            Point2D center = new Point2D(0, 0);
            double totalWeight = 0.0;
            for (int prev = 0; prev < i; ++prev) {
                Bounds prevBounds = boundsList.get(prev);
                double weight = configuration.words.get(prev).getWeight();
                if (0 == i % 2) {
                    center = center.add((prevBounds.getWidth() / 2d) * weight, (prevBounds.getHeight() / 2d) * weight);
                } else {
                    center = center.subtract((prevBounds.getWidth() / 2d) * weight, (prevBounds.getHeight() / 2d) * weight);
                }
                totalWeight += weight;
            }
            center = center.multiply(1d / totalWeight);
            boolean done = false;
            double radius = 0.1 * Math.min(boundsList.get(0).getWidth(), boundsList.get(0).getHeight());
            while (!done) {
                if (radius > Math.max(configuration.layoutBounds.getHeight(), configuration.layoutBounds.getWidth())) {
                    doFinish = true;
                }
                int startDeg = rand.nextInt(360);
                double prevX = -1;
                double prevY = -1;
                for (int deg = startDeg; deg < startDeg + 360; deg += DEG) {
                    double rad = ((double) deg / Math.PI) * 180.0;
                    if (0 == i % 2) {
                        center = center.add(radius * Math.cos(rad), radius * Math.sin(rad));
                    } else {
                        center = center.subtract(radius * Math.cos(rad), radius * Math.sin(rad));
                    }
                    if (prevX == center.getX() && prevY == center.getY()) {
                        continue;
                    }
                    prevX = center.getX();
                    prevY = center.getY();
                    Bounds mayBe = new BoundingBox(center.getX() - width / 2d,
                            center.getY() - height / 2d, width, height);
                    boolean useable = true;
                    //check if bounds are full on screen:
                    if (configuration.layoutBounds.getWidth() > 0 && configuration.layoutBounds.getHeight() > 0 && (mayBe.getMinX() + configuration.layoutBounds.getWidth() / 2d < 0
                            || mayBe.getMinY() + configuration.layoutBounds.getHeight() / 2d < 0
                            || mayBe.getMaxX() + configuration.layoutBounds.getWidth() / 2d > configuration.layoutBounds.getMaxX()
                            || mayBe.getMaxY() + configuration.layoutBounds.getHeight() / 2d > configuration.layoutBounds.getMaxY())) {
                        useable = false;
                    }
                    if (useable) {
                        useable = Arrays.stream(configuration.blockedAreaBounds).noneMatch(mayBe::intersects);
                    }
                    if (useable) {
                        useable = boundsList.stream().filter(Objects::nonNull).noneMatch(mayBe::intersects);
                    }
                    if (useable) {
                        done = true;
                        boundsList.set(i, new BoundingBox(center.getX() - width / 2d,
                                center.getY() - height / 2d, width, height));
                        break;
                    }
                    if (doFinish) {
                        done = true;
                        break;
                    }
                }
                radius += RADIUS;
            }
        }

        Map<Word, Bounds> boundsMap = new HashMap<>();

        for (int k = 0; k < configuration.words.size(); k++) {
            boundsMap.put(configuration.words.get(k), boundsList.get(k));
        }
        return boundsMap;
    }

    public static class Configuration {

        private final List<Word> words;
        private final Bounds layoutBounds;
        private final Font font;
        private final double minWeight;
        private final double maxWeight;
        private final double maxFontSize;
        private Bounds[] blockedAreaBounds = new Bounds[0];

        public Configuration(final List<Word> words, final Font font, final double maxFontSize, final Bounds layoutBounds) {
            this.words = words;
            this.font = font;
            this.maxFontSize = maxFontSize;
            this.layoutBounds = layoutBounds;

            final DoubleSummaryStatistics weightSummary = words.stream().mapToDouble(Word::getWeight).summaryStatistics();
            this.minWeight = weightSummary.getMin();
            this.maxWeight = weightSummary.getMax();
            LOG.info("MaxWeight: " + maxWeight);
            LOG.info("MiWeight: " + minWeight);
        }

        public void setBlockedAreaBounds(final Bounds... blockedAreaBounds) {
            this.blockedAreaBounds = blockedAreaBounds;
        }
    }
}
