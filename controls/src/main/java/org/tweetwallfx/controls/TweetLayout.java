/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2026 TweetWallFX
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

import java.util.List;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * Calculates the layout of the words of a {@link Tweet} within a text flow.
 */
public class TweetLayout {

    private final Pattern pattern = Pattern.compile("\\s+");

    private final Configuration configuration;
    private final TweetWordNodeFactory wordNodeFactory;
    private final List<TweetWord> tweetWords;

    private TweetLayout(Configuration configuration) {
        this.configuration = configuration;
        wordNodeFactory = TweetWordNodeFactory.createFactory(new TweetWordNodeFactory.Config(configuration.font, configuration.tweetFontSize));
        tweetWords = recalcTweetLayout();
    }

    /**
     * {@return the calculated layout information of the tweet words}
     */
    public List<TweetWord> getWordLayoutInfo() {
        return tweetWords;
    }

    /**
     * Calculates the line offset to be used for a word that does not fit into
     * the remaining line width.
     *
     * @param targetBounds the bounds of the word to place
     * @param upperLeft the upper left corner of the layout area
     * @param maxWidth the maximum width of a line
     * @param lineOffset the current line offset
     * @return the line offset to be used for the word
     */
    public Point2D tweetWordLineOffset(Bounds targetBounds, Point2D upperLeft, double maxWidth, Point2D lineOffset) {
        double x = upperLeft.getX() + targetBounds.getMinX() - lineOffset.getX();
        double rightMargin = upperLeft.getX() + maxWidth;
        if (x + targetBounds.getWidth() > rightMargin) {
            return new Point2D(lineOffset.getX() + (x - upperLeft.getX()), lineOffset.getY() + targetBounds.getHeight());
        }
        return lineOffset;
    }

    /**
     * Calculates the position at which the given word is to be placed.
     *
     * @param targetBounds the bounds of the word to place
     * @param upperLeft the upper left corner of the layout area
     * @param lineOffset the current line offset
     * @return the position at which the word is to be placed
     */
    public Point2D layoutTweetWord(Bounds targetBounds, Point2D upperLeft, Point2D lineOffset) {
        double y = upperLeft.getY() + targetBounds.getMinY() + lineOffset.getY();
        double x = upperLeft.getX() + targetBounds.getMinX() - lineOffset.getX();
        return new Point2D(x, y);
    }

    private void fontSizeAdaption(Text text, double fontSize) {
        text.setFont(Font.font(text.getFont().getFamily(), fontSize));
    }

    private List<TweetWord> recalcTweetLayout() {
        TextFlow flow = new TextFlow();
        flow.setMaxWidth(300);
        pattern.splitAsStream(configuration.tweet.getDisplayEnhancedText())
                .forEach(w -> {
                    Text textWord = wordNodeFactory.createTextNode(w.concat(" "));
                    fontSizeAdaption(textWord, configuration.tweetFontSize);
                    textWord.getStyleClass().setAll("tag");
                    flow.getChildren().add(textWord);
                });
        flow.requestLayout();
        return flow.getChildren().stream()
                .map(node -> new TweetWord(node.getBoundsInParent(), ((Text) node).getText()))
                .toList();
    }

    /**
     * Creates a new {@link TweetLayout} for the given configuration.
     *
     * @param configuration the configuration of the tweet layout
     * @return the created tweet layout
     */
    public static TweetLayout createTweetLayout(Configuration configuration) {
        return new TweetLayout(configuration);
    }

    /**
     * Configuration of a {@link TweetLayout}.
     */
    public static class Configuration {

        private final Tweet tweet;
        private final Font font;
        private final double tweetFontSize;

        /**
         * Creates a new instance.
         *
         * @param tweet the tweet to lay out
         * @param font the font to use
         * @param tweetFontSize the font size to use for the tweet words
         */
        public Configuration(Tweet tweet, Font font, double tweetFontSize) {
            this.tweet = tweet;
            this.font = font;
            this.tweetFontSize = tweetFontSize;
        }
    }

    /**
     * A single word of a laid out tweet together with its bounds.
     */
    public static class TweetWord {

        /**
         * The bounds of the word.
         */
        public final Bounds bounds;

        /**
         * The text of the word.
         */
        public final String text;

        /**
         * Creates a new instance.
         *
         * @param bounds the bounds of the word
         * @param text the text of the word
         */
        public TweetWord(Bounds bounds, String text) {
            this.bounds = bounds;
            this.text = text;
        }

        @Override
        public String toString() {
            return "TweetWord{" + "text=" + text + ", bounds=" + bounds + '}';
        }
    }

    /**
     * Associates a {@link TweetWord} with the {@link Text} node displaying it.
     */
    public static class TweetWordNode {

        /**
         * The word being displayed.
         */
        public final TweetWord tweetWord;

        /**
         * The text node displaying the word.
         */
        public final Text textNode;

        /**
         * Creates a new instance.
         *
         * @param tweetWord the word being displayed
         * @param textNode the text node displaying the word
         */
        public TweetWordNode(TweetWord tweetWord, Text textNode) {
            this.tweetWord = tweetWord;
            this.textNode = textNode;
        }
    }
}
