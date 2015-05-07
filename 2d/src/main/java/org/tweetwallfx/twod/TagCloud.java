/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.twod;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.tweetwallfx.twitter.CLogOut;
import org.tweetwallfx.twitter.TwitterOAuth;
import twitter4j.conf.Configuration;

/**
 *
 * @author sven
 */
public class TagCloud extends Application {

    private Configuration conf;
    private CLogOut log;
    private final String hashtag = "#devoxx";
    private TagTweets tweetsTask;
    
    @Override
    public void start(Stage primaryStage) {
        
        try {
            AnchorPane root = FXMLLoader.<AnchorPane>load(this.getClass().getResource("TweetWallFX.fxml"));
            BorderPane borderPane = (BorderPane) root.lookup("#displayArea");
            Scene scene = new Scene(root, 800, 600);
            
            /* TWITTER */
//        log=CLogOut.getInstance();
//        log.getMessages().addListener((ov,s,s1)->System.out.println(s1));
            
            final Service service=new Service<Void>(){
                @Override protected Task<Void> createTask() {   
                    Task<Void> task = new Task<Void>(){
                        @Override protected Void call() throws Exception {
                            conf = TwitterOAuth.getInstance().readOAuth();
                            return null;
                        }
                    };
                    return task;
                }
            };
            
            service.setOnSucceeded(e->{
                if(!hashtag.isEmpty() && conf!=null){
                    tweetsTask= new TagTweets(conf, hashtag, borderPane);
                    tweetsTask.start();
                }
            });
            
            primaryStage.setTitle("The JavvaFX Tweetwall for Devoox!");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setFullScreen(true);
            service.start();
        } catch (IOException ex) {
            Logger.getLogger(TagCloud.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void stop() {
        System.out.println("closing...");
        if(tweetsTask!=null){
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
