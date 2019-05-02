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

import java.awt.BorderLayout;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javafx.application.Platform;
import javax.swing.JTextField;
import org.tweetwallfx.embeddable.EmbeddedTweetwall;

public class Main {

    private static EmbeddedTweetwall tweetwall;
    private static JTextField queryTextfield;

    public static void main(String[] args) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        queryTextfield = new JTextField("#javaone");
        tweetwall = new EmbeddedTweetwall();

        final JFXPanel p = new JFXPanel();
        p.setScene(new Scene(new BorderPane(tweetwall)));

        queryTextfield.addActionListener(e -> {
            stop();
            start(queryTextfield.getText());
        });

        panel.add(queryTextfield, BorderLayout.NORTH);
        panel.add(p, BorderLayout.CENTER);

        JFrame jFrame = new JFrame("Hi there");
        jFrame.setContentPane(panel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1024, 768);
        jFrame.setVisible(true);
    }

    private static void start(final String query) {
        System.out.println(query);
        Platform.runLater(() -> tweetwall.start(query));
    }

    private static void stop() {
        tweetwall.stop();
    }
}
