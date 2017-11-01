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
package org.tweetwallfx.embeddable;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.twod.TagTweets;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * @author martin
 */
public final class EmbeddedTweetwall extends Parent {

    private TagTweets tweetsTask = null;
    private final ReadOnlyStringWrapper stylesheetProperty = new ReadOnlyStringWrapper(null);

    public EmbeddedTweetwall() {
        stylesheetProperty.addListener((o, ov, nv) -> {
            getScene().getStylesheets().remove(ov);
            getScene().getStylesheets().add(nv);
        });
    }

    public void start(final String query) {
        StopList.add(query);
        final BorderPane borderPane = new BorderPane();

        tweetsTask = new TagTweets(borderPane);
        tweetsTask.start();

        getChildren().setAll(borderPane);
    }

    public void stop() {
        System.out.println("closing...");
        Tweeter.getInstance().shutdown();
    }
}
