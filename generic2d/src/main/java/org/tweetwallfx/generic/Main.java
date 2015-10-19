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

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.twod.TagTweets;
import org.tweetwallfx.tweet.TweetSetData;

/**
 * @author martin
 */
public class Main extends Application {

    private final String query = Configuration.getInstance().getConfig("tweetwall.twitter.query");
    private final String title = Configuration.getInstance().getConfig("tweetwall.title");
    private final String stylesheet = Configuration.getInstance().getConfig("tweetwall.stylesheet", null);
    private Tweeter tweeter;
    private TagTweets tweetsTask;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        StopList.add(query);

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        tweeter = Tweeter.getInstance();
                        return null;
                    }
                };
                return task;
            }
        };

        service.setOnSucceeded(e -> {
            if (!query.isEmpty() && tweeter != null) {
                tweetsTask = new TagTweets(new TweetSetData(tweeter, query), borderPane);
                tweetsTask.start();
            }
        });
        service.setOnFailed(e -> {
            System.err.println("FAILED!");
            Main.this.stop();
            primaryStage.close();
        });

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        
        if (null != stylesheet) {
            scene.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource(stylesheet).toExternalForm());
        }

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
