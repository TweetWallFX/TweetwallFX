/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
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
package org.tweetwallfx.generic;

import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;
import org.tweetwallfx.tweet.StringPropertyAppender;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.twod.TagTweets;

public class Main extends Application {

    private static final String STARTUP = "org.tweetwallfx.startup";
    private static final Logger LOG = LogManager.getLogger(Main.class);
    private static final Logger LOGGER = LogManager.getLogger(STARTUP);

    @Override
    public void start(Stage primaryStage) {
        final TweetwallSettings tweetwallSettings
                = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(
                borderPane,
                tweetwallSettings.getResolution().getWidth(),
                tweetwallSettings.getResolution().getHeight());
        borderPane.getStyleClass().add("splash");

        Optional.ofNullable(tweetwallSettings.getStylesheetResource())
                .map(ClassLoader.getSystemClassLoader()::getResource)
                .map(java.net.URL::toExternalForm)
                .ifPresent(scene.getStylesheets()::add);
        Optional.ofNullable(tweetwallSettings.getStylesheetFile())
                .ifPresent(scene.getStylesheets()::add);

        StringPropertyAppender spa = new StringPropertyAppender();

        LoggerContext context = LoggerContext.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
        spa.start();
        LoggerConfig slc = config.getLoggerConfig(LOGGER.getName());
        slc.setLevel(Level.TRACE);
        slc.addAppender(spa, Level.TRACE, null);

        HBox statusLineHost = new HBox();
        Text statusLineText = new Text();
        statusLineText.getStyleClass().addAll("statusline");
        statusLineText.textProperty().bind(spa.stringProperty());
        statusLineHost.getChildren().add(statusLineText);

        final TagTweets tweetsTask = new TagTweets(borderPane);
        Platform.runLater(tweetsTask::start);

        scene.setOnKeyTyped((KeyEvent event) -> {
            if (event.isMetaDown() && event.getCharacter().equals("d")) {
                if (null == statusLineHost.getParent()) {
                    borderPane.setBottom(statusLineHost);
                } else {
                    borderPane.getChildren().remove(statusLineHost);
                }
            }
        });

        primaryStage.setTitle(tweetwallSettings.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();

        if (tweetwallSettings.isScaling()) {
            primaryStage.setFullScreen(false);

            final Slider scaling = new Slider(0, 200, 100);
            final Label scalingCaption = new Label("Scaling Factor:");
            final Label scalingValue = new Label();

            scaling.setShowTickMarks(true);
            scaling.setShowTickLabels(false);
            scaling.setMajorTickUnit(25d);
            scaling.setBlockIncrement(5d);

            scalingValue.textProperty().bind(Bindings.convert(scaling.valueProperty()));

            final GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(10);
            grid.setHgap(20);

            GridPane.setConstraints(scalingCaption, 0, 0);
            grid.getChildren().add(scalingCaption);

            GridPane.setConstraints(scaling, 1, 0);
            grid.getChildren().add(scaling);

            GridPane.setConstraints(scalingValue, 2, 0);
            grid.getChildren().add(scalingValue);

            scaling.valueProperty().addListener((ov, oldV, newV) -> {
                scene.getRoot().scaleXProperty().setValue(newV.doubleValue() / 100d);
                scene.getRoot().scaleYProperty().setValue(newV.doubleValue() / 100d);
            });

            final Scene scalingScene = new Scene(new BorderPane(grid), 300, 80);

            borderPane.setMaxWidth(tweetwallSettings.getResolution().getWidth());
            borderPane.setMaxHeight(tweetwallSettings.getResolution().getHeight());

            final Stage secondaryStage = new Stage();
            secondaryStage.setTitle("Scale");
            secondaryStage.setScene(scalingScene);
            secondaryStage.show();
        } else {
            primaryStage.setFullScreen(!Boolean.getBoolean("org.tweetwallfx.disable-full-screen"));
        }
    }

    @Override
    public void stop() {
        LOG.info("closing...");
        Tweeter.getInstance().shutdown();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
