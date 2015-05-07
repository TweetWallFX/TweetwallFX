/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.netbeans;

import java.util.List;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.tweetwallfx.controls.StopList;
import org.tweetwallfx.controls.Wordle;
import org.tweetwallfx.twitter.TwitterOAuth;
import org.tweetwallfx.twod.TagTweets;
import twitter4j.conf.Configuration;

/**
 *
 * @author sven
 */
public class Main extends Application {

    private Configuration conf;
    private final String hashtag = "#netbeans OR #netbeansday";// #google";
    private TagTweets tweetsTask;

    @Override
    public void start(Stage primaryStage) {

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        StopList.add(hashtag);

        final Service service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final List<String> rawParameters = getParameters().getRaw();
                        conf = TwitterOAuth.getInstance(rawParameters.toArray(new String[rawParameters.size()])).readOAuth();
                        return null;
                    }
                };
                return task;
            }
        };

        service.setOnSucceeded(e -> {
            if (!hashtag.isEmpty() && conf != null) {
                tweetsTask = new TagTweets(conf, hashtag, borderPane);
                tweetsTask.start();
            }
        });
               
        primaryStage.setTitle("The JavaFX Tweetwall for NetBeans Day!");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("/netbeans.css").toExternalForm());
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
