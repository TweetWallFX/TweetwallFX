/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

import javafx.scene.text.Font;

/**
 * Factory creating and adapting the text nodes used to render the words of a
 * tag cloud.
 */
public class CloudWordNodeFactory extends AbstractWordNodeFactory {

    private final Config configuration;

    private CloudWordNodeFactory(Config configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    /**
     * {@return a newly created factory for the given configuration}
     *
     * @param configuration the configuration of the factory
     */
    static CloudWordNodeFactory createFactory(Config configuration) {
        return new CloudWordNodeFactory(configuration);
    }

    /**
     * {@return the font size to use for the word with the given weight}
     *
     * @param weight the weight of the word
     * @param minWeight the minimum weight of all words
     * @param maxWeight the maximum weight of all words
     */
    double getFontSize(double weight, double minWeight, double maxWeight) {
        double a = (configuration.font.getSize() - configuration.maxFontSize) / Math.log(minWeight / maxWeight);
        double b = configuration.font.getSize() - a * Math.log(minWeight);
        return a * Math.log(weight) + b;
    }

    /**
     * Configuration of a {@link CloudWordNodeFactory}.
     */
    static class Config extends AbstractWordNodeFactory.Configuration {

        private final double maxFontSize;

        /**
         * Creates a new instance.
         *
         * @param font the font to use
         * @param maxFontSize the maximum font size
         */
        Config(final Font font, final double maxFontSize) {
            super(font);
            this.maxFontSize = maxFontSize;
        }
    }
}
