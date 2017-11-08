/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwallfx.twod;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.controls.Wordle;

/**
 * TweetWallFX - Devoxx 2014-17 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * Tasks to perform a search on Twitter for some hashtag, create an HBox with
 * each tweets, crate a snapshot and then load the image as diffuseMap of a
 * segmented torus Tasks and blockingQueues take care of this complex process
 *
 * @author José Pereda
 * @author Sven Reimers
 */
public class TagTweets {

    private static final String STARTUP = "org.tweetwallfx.startup";
    private static final Logger STARTUP_LOGGER = LogManager.getLogger(STARTUP);

    private Wordle wordle;
    private final BorderPane root;
    private final HBox hWordle = new HBox();

    public TagTweets(final BorderPane root) {
        this.root = root;
    }

    public void start() {
        STARTUP_LOGGER.trace("TagTweets.start");
        hWordle.setAlignment(Pos.CENTER);
        hWordle.prefWidthProperty().bind(root.widthProperty());
        hWordle.prefHeightProperty().bind(root.heightProperty());

        root.setCenter(hWordle);
        String searchText = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class).getQuery();
        STARTUP_LOGGER.trace("** 1. Creating Tag Cloud for " + searchText);

        STARTUP_LOGGER.trace("** create wordle");

        wordle = new Wordle();
        hWordle.getChildren().setAll(wordle);
        wordle.prefWidthProperty().bind(hWordle.widthProperty());
        wordle.prefHeightProperty().bind(hWordle.heightProperty());

        STARTUP_LOGGER.trace("** create wordle done");
        STARTUP_LOGGER.trace("** 2. Starting new Tweets search for " + searchText);
    }
}
