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
package org.tweetwallfx.generic;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.tweet.StringPropertyAppender;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.twod.TagTweets;
import org.tweetwallfx.tweet.TweetSetData;

/**
 * @author martin
 */
public class Main extends Application {

    private static final String STARTUP = "org.tweetwallfx.startup";
    Logger startupLogger = LogManager.getLogger(STARTUP);
    
    private static final String query = Configuration.getInstance().getConfig("tweetwall.twitter.query");
    private static final String title = Configuration.getInstance().getConfig("tweetwall.title");
    private static final String stylesheet = Configuration.getInstance().getConfig("tweetwall.stylesheet", null);
    private Tweeter tweeter;
    private TagTweets tweetsTask;
  
    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        
        if (null != stylesheet) {
            scene.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource(stylesheet).toExternalForm());
        }        
        //extract Hashtags from complex query and add to StopList
        final Matcher m = Pattern.compile("#[\\S]+").matcher(query);
        while (m.find()) {
            StopList.add(m.group(0));
        }
        
        StringPropertyAppender spa = new StringPropertyAppender();
        
        LoggerContext context = LoggerContext.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
        spa.start();
        LoggerConfig slc = config.getLoggerConfig(startupLogger.getName());
        slc.setLevel(Level.TRACE);
        slc.addAppender(spa, Level.TRACE, null);

        HBox statusLineHost = new HBox();
        Text statusLineText = new Text();
        statusLineText.getStyleClass().addAll("statusline");
        statusLineText.textProperty().bind(spa.stringProperty());
        statusLineHost.getChildren().add(statusLineText);
        
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        startupLogger.trace("Create Tweeter ...");
                        tweeter = Tweeter.getInstance();
                        startupLogger.trace("Tweeter created!");
                        return null;
                    }
                };
                return task;
            }
        };

        service.setOnSucceeded(e -> {
            startupLogger.trace("Start Tweet task ...");
            if (!query.isEmpty() && tweeter != null) {
                tweetsTask = new TagTweets(new TweetSetData(tweeter, query), borderPane);
                tweetsTask.start();
            }
        });
        service.setOnFailed(e -> {
            Throwable exception = e.getSource().getException();
            System.err.println("FAILED! " + exception);
            System.err.println("Cause!  " + (null != exception?exception.getCause():"Unknown!"));
            Main.this.stop();
            primaryStage.close();
        });
        
        scene.setOnKeyTyped((KeyEvent event) -> {
            if (event.isMetaDown() && event.getCharacter().equals("d")) {
                if (null == statusLineHost.getParent()) {
                    borderPane.setBottom(statusLineHost);
                }
                else {
                    borderPane.getChildren().remove(statusLineHost);
                }
            }
        });

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        
        primaryStage.show();
        primaryStage.setFullScreen(true);
        service.start();
    }

    @Override
    public void stop() {
        System.out.println("closing...");
        if (tweetsTask != null) {
            tweetsTask.stop();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
