/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 TweetWallFX
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.emoji.EmojiImageCache;
import org.tweetwallfx.emoji.Emojify;
import org.tweetwallfx.emoji.Twemoji;

public class EmojiFlow extends TextFlow {

    private static final Logger LOG = LoggerFactory.getLogger(EmojiFlow.class);

    private final ObjectProperty<String> textProperty = new SimpleObjectProperty<>();
    private final int emojiFitWidthProperty = 12;
    private final int emojiFitHeightProperty = 12;

    public EmojiFlow() {
        this.textProperty.addListener((o, old, text) -> updateContent(text));
    }

    public final String getText() {
        return textProperty.get();
    }

    public final void setText(String text) {
        textProperty.set(text);
    }

    private void updateContent(String message) {
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
        textNode.getStyleClass().add("tweetText");
        textNode.applyCss();
        return textNode;
    }

    private ImageView createEmojiImageNode(Twemoji emoji) throws NullPointerException {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(emojiFitWidthProperty);
        imageView.setFitHeight(emojiFitHeightProperty);
        imageView.setImage(new Image(EmojiImageCache.INSTANCE.get(emoji.hex()).getInputStream()));
        return imageView;
    }
}
