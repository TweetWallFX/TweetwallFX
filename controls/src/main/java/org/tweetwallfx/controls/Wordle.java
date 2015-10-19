/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.tweetwallfx.tweet.api.Tweet;

/**
 * @author sven
 */
public class Wordle extends Control {

    public enum LayoutMode {

        WORDLE, TWEET
    }

    ObjectProperty<List<Word>> wordsProperty = new SimpleObjectProperty<>(new ArrayList<>());
    ObjectProperty<Tweet> tweetInfoProperty = new SimpleObjectProperty<>();

    ObjectProperty<LayoutMode> layoutModeProperty = new SimpleObjectProperty<>(LayoutMode.WORDLE);
    private SimpleStyleableStringProperty logo;
    private SimpleStyleableBooleanProperty favIconsVisible;
    
    private String userAgentStylesheet = null;

    public Wordle() {
        getStyleClass().setAll("wordle");
    }

    public void setTweet(Tweet status) {
        tweetInfoProperty.set(status);
    }

    public ObjectProperty<Tweet> tweetInfoProperty() {
        return tweetInfoProperty;
    }

    public void setWords(List<Word> words) {
        wordsProperty.set(words);
    }

    public ObjectProperty<List<Word>> wordsProperty() {
        return wordsProperty;
    }

    public void setLayoutMode(LayoutMode layoutMode) {
        layoutModeProperty.set(layoutMode);
    }

    public ObjectProperty<LayoutMode> layoutModeProperty() {
        return layoutModeProperty;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new WordleSkin(this);
    }

    public String getLogo() {
        return logo.get();
    }

    public void setLogo(String value) {
        logo.set(value);
    }

    public SimpleStyleableStringProperty logoProperty() {
        if (logo == null) {
            logo = new SimpleStyleableStringProperty(StyleableProperties.LOGO_GRAPHIC, Wordle.this, "logo", null);
        }
        return logo;
    }

    public Boolean isFavIconsVisible() {
        return favIconsVisible.get();
    }

    public void setFavIconsVisible(Boolean value) {
        favIconsVisible.set(value);
    }

    public SimpleStyleableBooleanProperty favIconsVisibleProperty() {
        if (favIconsVisible == null) {
            favIconsVisible = new SimpleStyleableBooleanProperty(StyleableProperties.FAVICONS_VISIBLE, Wordle.this, "-fx-fav-icons-visible", true);
        }
        return favIconsVisible;
    }

    @Override
    public String getUserAgentStylesheet() {
        if (null == userAgentStylesheet) {
            userAgentStylesheet = this.getClass().getResource("wordle.css").toExternalForm();
        }
        return userAgentStylesheet;
    }

    private static class StyleableProperties {

        private static final CssMetaData< Wordle, String> LOGO_GRAPHIC
                = new CssMetaData<Wordle, String>("-fx-graphic",
                        StyleConverter.getUrlConverter(), null) {
                    @Override
                    public boolean isSettable(Wordle control) {
                        return control.logo == null || !control.logo.isBound();
                    }

                    @Override
                    public StyleableProperty<String> getStyleableProperty(Wordle control) {
                        return control.logoProperty();
                    }
                };

        private static final CssMetaData< Wordle, Boolean> FAVICONS_VISIBLE
                = new CssMetaData<Wordle, Boolean>("-fx-fav-icons-visible",
                        StyleConverter.getBooleanConverter()) {

                    @Override
                    public boolean isSettable(Wordle control) {
                        return control.favIconsVisible == null || !control.favIconsVisible.isBound();
                    }

                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(Wordle control) {
                        return control.favIconsVisibleProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables
                    = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    LOGO_GRAPHIC,
                    FAVICONS_VISIBLE
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

}
