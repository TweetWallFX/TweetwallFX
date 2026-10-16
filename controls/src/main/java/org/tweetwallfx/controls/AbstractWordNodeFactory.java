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
 * Base factory creating and adapting the text nodes used to render the words of
 * a tag cloud.
 */
class AbstractWordNodeFactory {

    private final Configuration configuration;

    /**
     * Creates a new instance.
     *
     * @param configuration the configuration of this factory
     */
    protected AbstractWordNodeFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * {@return a newly created factory for the given configuration}
     *
     * @param configuration the configuration of the factory
     */
    public static AbstractWordNodeFactory createFactory(Configuration configuration) {
        return new AbstractWordNodeFactory(configuration);
    }

    /**
     * Adapts the font size of the given text node.
     *
     * @param text the text node to adapt
     * @param fontSize the font size to apply
     */
    public void fontSizeAdaption(Text text, double fontSize) {
        text.setFont(Font.font(text.getFont().getFamily(), fontSize));
    }

    /**
     * {@return a newly created text node displaying the given word}
     *
     * @param word the word to display
     */
    public Text createTextNode(String word) {
        Text textNode = new Text(word);
        textNode.getStyleClass().setAll("tag");
        textNode.setStyle("-fx-padding: 10px");
        textNode.applyCss();
        textNode.setFont(configuration.font);
        textNode.setCache(true);
//        textNode.setCacheHint(CacheHint.SPEED);
        return textNode;
    }

    /**
     * Configuration of an {@link AbstractWordNodeFactory}.
     */
    protected static class Configuration {

        /**
         * The font used to render words.
         */
        final Font font;

        /**
         * Creates a new instance.
         *
         * @param font the font to use
         */
        Configuration(Font font) {
            this.font = font;
        }
    }
}
