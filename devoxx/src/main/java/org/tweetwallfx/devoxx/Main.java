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
package org.tweetwallfx.devoxx;

import com.beust.jcommander.Parameter;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openide.util.lookup.ServiceProvider;
import org.tweetwallfx.cmdargs.CommandLineArgumentParser;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.tweet.TweetSetData;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.twod.TagTweets;

/**
 * @author sven
 */
public class Main extends Application {

    private Tweeter tweeter;
    private TagTweets tweetsTask;

    @Override
    public void start(Stage primaryStage) {
        CommandLineArgumentParser.parseArguments(getParameters().getRaw());

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        StopList.add(Params.query);

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
            if (!Params.query.isEmpty() && tweeter != null) {
                tweetsTask = new TagTweets(new TweetSetData(tweeter, Params.query), borderPane);
                tweetsTask.start();
            }
        });

        primaryStage.setTitle(Params.title);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(Params.stylesheet.toExternalForm());
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

    @ServiceProvider(service = CommandLineArgumentParser.ParametersObject.class)
    public static final class Params implements CommandLineArgumentParser.ParametersObject {

        @Parameter(names = "-query", description = "Query used for querying Twitter")
        private static String query = "#devoxx";
//        @Parameter(names = "-stylesheet", required = true, description = "The stylesheet to apply")
//        private static URL stylesheet;
        @Parameter(names = "-title", description = "The title of the TweetWallStage")
        private static String title = "The JavaFX Tweetwall for Devoxx!";
}
}
