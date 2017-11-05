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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.tweetwallfx.tweet.api.Tweet;

public class TweetLayout {

    private final Pattern pattern = Pattern.compile("\\s+");

    private final Configuration configuration;
    private final TweetWordNodeFactory wordNodeFactory;
    private final List<TweetWord> tweetWords;

    private TweetLayout(Configuration configuration) {
        this.configuration = configuration;
        wordNodeFactory = TweetWordNodeFactory.createFactory(new TweetWordNodeFactory.Configuration(configuration.font, configuration.tweetFontSize));
        tweetWords = recalcTweetLayout();
    }

    public List<TweetWord> getWordLayoutInfo() {
        return tweetWords;
    }

    public Point2D tweetWordLineOffset(Bounds targetBounds, Point2D upperLeft, double maxWidth, Point2D lineOffset) {
        double x = upperLeft.getX() + targetBounds.getMinX() - lineOffset.getX();
        double rightMargin = upperLeft.getX() + maxWidth;
        if (x + targetBounds.getWidth() > rightMargin) {
            return new Point2D(lineOffset.getX() + (x - upperLeft.getX()), lineOffset.getY() + targetBounds.getHeight());
        }
        return lineOffset;
    }

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
        return flow.getChildren().stream().map(node -> new TweetWord(node.getBoundsInParent(), ((Text) node).getText())).collect(Collectors.toList());
    }

    public static TweetLayout createTweetLayout(Configuration configuration) {
        return new TweetLayout(configuration);
    }

    public static class Configuration {

        private final Tweet tweet;
        private final Font font;
        private final double tweetFontSize;

        public Configuration(Tweet tweet, Font font, double tweetFontSize) {
            this.tweet = tweet;
            this.font = font;
            this.tweetFontSize = tweetFontSize;
        }
    }

    public static class TweetWord {

        public final Bounds bounds;
        public final String text;

        public TweetWord(Bounds bounds, String text) {
            this.bounds = bounds;
            this.text = text;
        }

        @Override
        public String toString() {
            return "TweetWord{" + "text=" + text + ", bounds=" + bounds + '}';
        }
    }

    public static class TweetWordNode {

        public final TweetWord tweetWord;
        public final Text textNode;

        public TweetWordNode(TweetWord tweetWord, Text textNode) {
            this.tweetWord = tweetWord;
            this.textNode = textNode;
        }
    }
}
