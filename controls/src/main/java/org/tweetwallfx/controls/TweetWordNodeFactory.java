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
import javafx.scene.text.Text;

/**
 * Factory creating text nodes for the words of a tweet.
 */
public class TweetWordNodeFactory extends AbstractWordNodeFactory {

    private final Config configuration;

    private TweetWordNodeFactory(Config configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    /**
     * Creates a new {@link TweetWordNodeFactory} for the given configuration.
     *
     * @param configuration the configuration to use
     * @return the created factory
     */
    public static TweetWordNodeFactory createFactory(Config configuration) {
        return new TweetWordNodeFactory(configuration);
    }

    /**
     * Adapts the font size of the given text node to the configured tweet font
     * size.
     *
     * @param textNode the text node to adapt
     */
    public void fontSizeAdaption(Text textNode) {
        fontSizeAdaption(textNode, configuration.tweetFontSize);
    }

    /**
     * Configuration of a {@link TweetWordNodeFactory}.
     */
    public static class Config extends AbstractWordNodeFactory.Configuration {

        private final double tweetFontSize;

        /**
         * Creates a new instance.
         *
         * @param font the font to use
         * @param tweetFontSize the font size to use for tweet words
         */
        public Config(Font font, double tweetFontSize) {
            super(font);
            this.tweetFontSize = tweetFontSize;
        }
    }
}
