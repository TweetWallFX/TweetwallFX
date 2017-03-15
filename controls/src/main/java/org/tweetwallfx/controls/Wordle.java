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
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableIntegerProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;
import org.tweetwallfx.controls.dataprovider.DataProvider;

/**
 * @author sven
 */
public class Wordle extends Control {

    public enum LayoutMode {

        WORDLE, TWEET
    }

    ObjectProperty<List<Word>> wordsProperty = new SimpleObjectProperty<>(new ArrayList<>());

    ObjectProperty<LayoutMode> layoutModeProperty = new SimpleObjectProperty<>(LayoutMode.WORDLE);
    private SimpleStyleableStringProperty logo;
    private SimpleStyleableStringProperty secondLogo;
    private SimpleStyleableStringProperty backgroundGraphic;
    private SimpleStyleableBooleanProperty favIconsVisible;
    private SimpleStyleableIntegerProperty displayedNumberOfTagsProperty;
    private SimpleStyleableObjectProperty<Font> fontProperty;
    private SimpleStyleableIntegerProperty fontSizeMinProperty;
    private SimpleStyleableIntegerProperty fontSizeMaxProperty;
    private SimpleStyleableIntegerProperty tweetFontSizeProperty;

    private SimpleListProperty<DataProvider> dataProviderList = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    
    private String userAgentStylesheet = null;

    public Wordle() {
        getStyleClass().setAll("wordle");
    }
    
    public void addDataProvider(DataProvider dataProvider) {
        dataProviderList.add(dataProvider);
    }           
    
    @SuppressWarnings("unchecked")
    public <T extends DataProvider> T getDataProvider(Class<T> klazz) {
        return (T) dataProviderList.stream().filter(dp -> dp.getClass() == klazz).findFirst().orElse(null);
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
    
    public String getSecondLogo() {
        return secondLogo.get();
    }

    public void setSecondLogo(String value) {
        secondLogo.set(value);
    }

    public SimpleStyleableStringProperty secondLogoProperty() {
        if (secondLogo == null) {
            secondLogo = new SimpleStyleableStringProperty(StyleableProperties.SECOND_LOGO_GRAPHIC, Wordle.this, "secondlogo", null);
        }
        return secondLogo;
    }
    
    public String getBackgroundGraphic() {
        return backgroundGraphic.get();
    }

    public void setBackgroundGraphic(String value) {
        backgroundGraphic.set(value);
    }

    public SimpleStyleableStringProperty backgroundGraphicProperty() {
        if (backgroundGraphic == null) {
            backgroundGraphic = new SimpleStyleableStringProperty(StyleableProperties.BACKGROUND_GRAPHIC, Wordle.this, "background", null);
        }
        return backgroundGraphic;
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

    public Integer getDisplayedTagNum() {
        return displayedNumberOfTagsProperty.get();
    }

    public void setDisplayedNumberOfTags(Integer value) {
        displayedNumberOfTagsProperty.set(value);
    }

    public SimpleStyleableIntegerProperty displayedNumberOfTagsProperty() {
        if (displayedNumberOfTagsProperty == null) {
            displayedNumberOfTagsProperty = new SimpleStyleableIntegerProperty(StyleableProperties.DISPLAYED_TAGS_NUMBER, Wordle.this, "-fx-display-tag-num", 50);
        }
        return displayedNumberOfTagsProperty;
    }

    public Font getFont() {
        return fontProperty.get();
    }

    public void setFont(Font value) {
        fontProperty.set(value);
    }

    public SimpleStyleableObjectProperty<Font> fontProperty() {
        if (fontProperty == null) {
            fontProperty = new SimpleStyleableObjectProperty<>(StyleableProperties.FONT, Wordle.this, "-fx-font", Font.getDefault());
        }
        return fontProperty;
    }

    public Integer getFontSizeMin() {
        return fontSizeMinProperty.get();
    }

    public void setFontSizeMin(Integer value) {
        fontSizeMinProperty.set(value);
    }

    public SimpleStyleableIntegerProperty fontSizeMinProperty() {
        if (fontSizeMinProperty == null) {
            fontSizeMinProperty = new SimpleStyleableIntegerProperty(StyleableProperties.FONT_SIZE_MIN, Wordle.this, "-fx-font-size-min", 36);
        }
        return fontSizeMinProperty;
    }

    public Integer getFontSizeMax() {
        return fontSizeMaxProperty.get();
    }

    public void setFontSizeMax(Integer value) {
        fontSizeMaxProperty.set(value);
    }

    public SimpleStyleableIntegerProperty fontSizeMaxProperty() {
        if (fontSizeMaxProperty == null) {
            fontSizeMaxProperty = new SimpleStyleableIntegerProperty(StyleableProperties.FONT_SIZE_MAX, Wordle.this, "-fx-font-size-max", 72);
        }
        return fontSizeMaxProperty;
    }

    public Integer getTweetFontSize() {
        return tweetFontSizeProperty.get();
    }

    public void setTweetFontSize(Integer value) {
        tweetFontSizeProperty.set(value);
    }

    public SimpleStyleableIntegerProperty tweetFontSizeProperty() {
        if (tweetFontSizeProperty == null) {
            tweetFontSizeProperty = new SimpleStyleableIntegerProperty(StyleableProperties.TWEET_FONT_SIZE, Wordle.this, "-fx-tweet-font-size", 54);
        }
        return tweetFontSizeProperty;
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

        private static final CssMetaData< Wordle, String> SECOND_LOGO_GRAPHIC
                = new CssMetaData<Wordle, String>("-fx-second-graphic",
                        StyleConverter.getUrlConverter(), null) {
            @Override
            public boolean isSettable(Wordle control) {
                return control.secondLogo == null || !control.secondLogo.isBound();
            }

            @Override
            public StyleableProperty<String> getStyleableProperty(Wordle control) {
                return control.secondLogoProperty();
            }
        };
        
        private static final CssMetaData< Wordle, String> BACKGROUND_GRAPHIC
                = new CssMetaData<Wordle, String>("-fx-background-graphic",
                        StyleConverter.getUrlConverter(), null) {
            @Override
            public boolean isSettable(Wordle control) {
                return control.backgroundGraphic == null || !control.backgroundGraphic.isBound();
            }

            @Override
            public StyleableProperty<String> getStyleableProperty(Wordle control) {
                return control.backgroundGraphicProperty();
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

        private static final CssMetaData<Wordle, Number> DISPLAYED_TAGS_NUMBER
                = new CssMetaData<Wordle, Number>("-fx-display-tag-num", StyleConverter.getSizeConverter()) {

            @Override
            public boolean isSettable(Wordle control) {
                return control.displayedNumberOfTagsProperty == null || !control.displayedNumberOfTagsProperty.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(Wordle control) {
                return control.displayedNumberOfTagsProperty();
            }
        };

        private static final CssMetaData<Wordle, Font> FONT
                = new CssMetaData<Wordle, Font>("-fx-font", StyleConverter.getFontConverter()) {

            @Override
            public boolean isSettable(Wordle control) {
                return control.fontProperty == null || !control.fontProperty.isBound();
            }

            @Override
            public StyleableProperty<Font> getStyleableProperty(Wordle control) {
                return control.fontProperty();
            }
        };

        private static final CssMetaData<Wordle, Number> FONT_SIZE_MIN
                = new CssMetaData<Wordle, Number>("-fx-font-size-min", StyleConverter.getSizeConverter()) {

            @Override
            public boolean isSettable(Wordle control) {
                return control.fontSizeMinProperty == null || !control.fontSizeMinProperty.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(Wordle control) {
                return control.fontSizeMinProperty();
            }
        };

        private static final CssMetaData<Wordle, Number> FONT_SIZE_MAX
                = new CssMetaData<Wordle, Number>("-fx-font-size-max", StyleConverter.getSizeConverter()) {

            @Override
            public boolean isSettable(Wordle control) {
                return control.fontSizeMaxProperty == null || !control.fontSizeMaxProperty.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(Wordle control) {
                return control.fontSizeMaxProperty();
            }
        };

        private static final CssMetaData<Wordle, Number> TWEET_FONT_SIZE
                = new CssMetaData<Wordle, Number>("-fx-tweet-font-size", StyleConverter.getSizeConverter()) {

            @Override
            public boolean isSettable(Wordle control) {
                return control.tweetFontSizeProperty == null || !control.tweetFontSizeProperty.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(Wordle control) {
                return control.tweetFontSizeProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables
                    = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    LOGO_GRAPHIC,
                    SECOND_LOGO_GRAPHIC,
                    BACKGROUND_GRAPHIC,
                    FAVICONS_VISIBLE,
                    DISPLAYED_TAGS_NUMBER,
                    FONT,
                    FONT_SIZE_MIN,
                    FONT_SIZE_MAX,
                    TWEET_FONT_SIZE
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
