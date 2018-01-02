/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sven
 */
public final class WordleLayout {
    
    private static final Logger log = LogManager.getLogger(WordleLayout.class);
    
    private static final double dRadius = 5.0;    
    private static final int dDeg = 10;
    
    private final Random rand = new Random();    
    
    private final Map<Word, Bounds> wordLayoutMap;
    private final Configuration configuration;
    private final CloudWordNodeFactory wordNodeFactory;
    private WordleLayout initialLayoutSolution;
    
    private WordleLayout(Configuration configuration) {
        this.configuration = configuration;
        this.wordNodeFactory = CloudWordNodeFactory.createFactory(new CloudWordNodeFactory.Configuration(configuration.font, configuration.minFontSize, configuration.maxFontSize));
        this.wordLayoutMap = calcTagLayout();                
    }
    
    private WordleLayout(Configuration configuration, WordleLayout initialLayoutSolution) {
        this.configuration = configuration;
        this.wordNodeFactory = CloudWordNodeFactory.createFactory(new CloudWordNodeFactory.Configuration(configuration.font, configuration.minFontSize, configuration.maxFontSize));
        this.initialLayoutSolution = initialLayoutSolution;
        this.wordLayoutMap = calcTagLayout();                
    }
    
    public static WordleLayout createWordleLayout(Configuration configuration) {
        return new WordleLayout(configuration);        
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
    
    private Map<Word, Bounds> calcTagLayout(){
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
                center = center.add((prevBounds.getWidth() / 2d) * weight, (prevBounds.getHeight() / 2d) * weight);
                totalWeight += weight;
            }
            center = center.multiply(1d / totalWeight);
            boolean done = false;
            double radius = 0.5 * Math.min(boundsList.get(0).getWidth(), boundsList.get(0).getHeight());
            while (!done) {
                if (radius > Math.max(configuration.layoutBounds.getHeight(), configuration.layoutBounds.getWidth())) {
                    doFinish = true;
                }
                int startDeg = rand.nextInt(360);
                double prev_x = -1;
                double prev_y = -1;
                for (int deg = startDeg; deg < startDeg + 360; deg += dDeg) {
                    double rad = ((double) deg / Math.PI) * 180.0;
                    center = center.add(radius * Math.cos(rad), radius * Math.sin(rad));
                    if (prev_x == center.getX() && prev_y == center.getY()) {
                        continue;
                    }
                    prev_x = center.getX();
                    prev_y = center.getY();
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
                        useable = !Arrays.stream(configuration.blockedAreaBounds).filter(bb -> mayBe.intersects(bb)).findAny().isPresent();
                    }
                    if (useable) {
                        for (int prev = 0; prev < boundsList.size(); ++prev) {
                            if (null != boundsList.get(prev)) {
                                if (mayBe.intersects(boundsList.get(prev))) {
                                    useable = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (useable || doFinish) {
                        done = true;
                        boundsList.set(i, new BoundingBox(center.getX() - width / 2d,
                                center.getY() - height / 2d, width, height));
                        break;
                    }
                }
                radius += dRadius;
            }
        }

        Map<Word, Bounds> boundsMap = new HashMap<>();

        for (int k = 0; k < configuration.words.size(); k++) {
            boundsMap.put(configuration.words.get(k), boundsList.get(k));
        }
        return boundsMap;
    }
    
    public static class Configuration {

        final List<Word> words;
        final Bounds layoutBounds;
        final Font font;
        final double minWeight;
        final double maxWeight;
        final double minFontSize;
        final double maxFontSize;
        Bounds[] blockedAreaBounds = new Bounds[]{};
        
        public Configuration(List<Word> words, Font font, double minFontSize, double maxFontSize, Bounds layoutBounds) {
            this.words = words;
            this.font = font;
            this.minFontSize = minFontSize;
            this.maxFontSize = maxFontSize;                    
            this.layoutBounds = layoutBounds;
            this.minWeight = words.stream().map(Word::getWeight).min(Comparator.naturalOrder()).get();
            this.maxWeight = words.stream().map(Word::getWeight).max(Comparator.naturalOrder()).get();
            log.info("MaxWeight: " + maxWeight);
            log.info("MiWeight: " + minWeight);
        }
        
        public void setBlockedAreaBounds(Bounds ... blockedAreaBounds) {
            this.blockedAreaBounds = blockedAreaBounds;
        }
        
    }
    
    
}
