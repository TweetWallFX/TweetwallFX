/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 TweetWallFX
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
package org.tweetwallfx.emoji.control;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.emoji.EmojiImageCache;
import org.tweetwallfx.emoji.Emojify;
import org.tweetwallfx.emoji.Twemoji;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A {@link TextFlow} that displays the given text and renders emoji found
 * therein as their corresponding Twemoji images.
 */
public final class EmojiFlow extends TextFlow {

    private static final Logger LOG = LoggerFactory.getLogger(EmojiFlow.class);

    private final ObjectProperty<String> textProperty = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty emojiFitWidthProperty = new SimpleDoubleProperty(12);
    private final SimpleDoubleProperty emojiFitHeightProperty = new SimpleDoubleProperty(12);

    /**
     * Creates a new instance with no text displayed.
     */
    public EmojiFlow() {
        this.textProperty.addListener((o, old, text) -> updateContent(text));
        this.emojiFitWidthProperty.addListener((o, old, newValue) -> updateContent(textProperty.get()));
        this.emojiFitHeightProperty.addListener((o, old, newValue) -> updateContent(textProperty.get()));
    }

    /**
     * Creates a new instance displaying the given text.
     *
     * @param text the text to display
     */
    public EmojiFlow(String text) {
        this();
        setText(text);
    }

    /**
     * Creates a new instance displaying the given text with the emoji images
     * fitted to the given dimensions.
     *
     * @param text the text to display
     * @param fitWidth the fit width of the emoji images
     * @param fitHeight the fit height of the emoji images
     */
    public EmojiFlow(String text, double fitWidth, double fitHeight) {
        this(text);
        setEmojiFitWidth(fitWidth);
        setEmojiFitHeight(fitHeight);
    }

    /**
     * {@return the text displayed by this {@code EmojiFlow}}
     */
    public final String getText() {
        return textProperty.get();
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text the text to display
     */
    public final void setText(String text) {
        textProperty.set(text);
    }

    /**
     * {@return the fit width used for the emoji images}
     */
    public final double getEmojiFitWidth() {
        return emojiFitWidthProperty.get();
    }

    /**
     * Sets the fit width used for the emoji images.
     *
     * @param fitWidth the fit width to set
     */
    public final void setEmojiFitWidth(double fitWidth) {
        emojiFitWidthProperty.set(fitWidth);
    }

    /**
     * {@return the fit height used for the emoji images}
     */
    public final double getEmojiFitHeight() {
        return emojiFitHeightProperty.get();
    }

    /**
     * Sets the fit height used for the emoji images.
     *
     * @param fitHeight the fit height to set
     */
    public final void setEmojiFitHeight(double fitHeight) {
        emojiFitHeightProperty.set(fitHeight);
    }

    private void updateContent(String message) {
        this.getChildren().clear();
        List<Object> obs = Emojify.tokenizeStringToTextAndEmoji(message);
        for (Object ob : obs) {
            if (ob instanceof String s) {
                this.getChildren().add(createTextNode(s));
            } else if (ob instanceof Twemoji emoji) {
                try {
                    this.getChildren().add(createEmojiImageNode(emoji));
                } catch (RuntimeException e) {
                    LOG.error("Image with hex code: {} appear not to exist in resources path", emoji.hex(), e);
                    this.getChildren().add(createTextNode(emoji.hex()));
                }
            }
        }
    }

    private Text createTextNode(String text) {
        Text textNode = new Text();
        textNode.setText(text);
        textNode.getStyleClass().add("emojiFlow");
        textNode.applyCss();
        return textNode;
    }

    private ImageView createEmojiImageNode(Twemoji emoji) throws NullPointerException {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(emojiFitWidthProperty.get());
        imageView.setFitHeight(emojiFitHeightProperty.get());
        imageView.setImage(new Image(EmojiImageCache.INSTANCE.get(emoji.hex()).getInputStream()));
        return imageView;
    }
}
