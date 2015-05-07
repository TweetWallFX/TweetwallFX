/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author sven
 */
public class WordleControlTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        Wordle wordle = new Wordle();
        
        List<Word> words = Arrays.asList(new Word[] {
                new Word("#Devoxx", 20), 
                new Word("trip", 10),
                new Word("fun", 15),
                new Word("Java", 15),
                new Word("JavaFX", 25),
                new Word("Java EE", 5),
        });
        
        
        wordle.setWords(words);
        
        BorderPane pane = new BorderPane(wordle);
        
        Scene scene = new Scene(pane, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
