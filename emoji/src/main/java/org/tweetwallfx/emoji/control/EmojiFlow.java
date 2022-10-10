/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.emoji.EmojiImageCache;
import org.tweetwallfx.emoji.Emojify;
import org.tweetwallfx.emoji.Twemoji;

public class EmojiFlow extends TextFlow {

    private static final Logger LOG = LogManager.getLogger(EmojiFlow.class);

    private final ObjectProperty<String> textProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> emojiFitWidthProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> emojiFitHeightProperty = new SimpleObjectProperty<>();

    public EmojiFlow() {
        this.emojiFitWidthProperty.set(12);
        this.emojiFitHeightProperty.set(12);
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
                Text textNode = new Text();
                textNode.setText(s);
                textNode.getStyleClass().add("tweetText");
                textNode.applyCss();
                this.getChildren().add(textNode);
                continue;
            } else if (ob instanceof Twemoji emoji) {
                try {
                    this.getChildren().add(createEmojiImageNode(emoji));
                } catch (RuntimeException e) {
                    LOG.error("Image with hex code: " + emoji.hex() + " appear not to exist in resources path", e);
                    Text textNode = new Text();
                    textNode.setText(emoji.hex());
                    textNode.getStyleClass().add("tweetText");
                    textNode.applyCss();
                    this.getChildren().add(textNode);
                }
            }
        }
    }

    private ImageView createEmojiImageNode(Twemoji emoji) throws NullPointerException {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(emojiFitWidthProperty.get());
        imageView.setFitHeight(emojiFitHeightProperty.get());

        imageView.setImage(new Image(EmojiImageCache.INSTANCE.get(emoji.hex()).getInputStream()));
        return imageView;
    }
}
