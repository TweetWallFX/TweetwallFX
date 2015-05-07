/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.tweetwallfx.twitter.TweetInfo;



/**
 *
 * @author sven
 */
public class Wordle extends Control {

    public enum LayoutMode {
        WORDLE, TWEET
    }

    ObjectProperty<List<Word>> wordsProperty = new SimpleObjectProperty<>(new ArrayList<>());
    ObjectProperty<TweetInfo> tweetInfoProperty = new SimpleObjectProperty<>();

    ObjectProperty<LayoutMode> layoutModeProperty = new SimpleObjectProperty<>(LayoutMode.WORDLE);

    public Wordle() {
        getStyleClass().setAll("wordle");
    }

    public void setTweet(TweetInfo status) {
        tweetInfoProperty.set(status);
    }    
    
    public ObjectProperty<TweetInfo> tweetInfoProperty() {
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
    private SimpleStyleableStringProperty logo;

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

    @Override
    public String getUserAgentStylesheet() {
        return this.getClass().getResource("wordle.css").toExternalForm();
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
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables
                    = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    LOGO_GRAPHIC
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
