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
package org.tweetwallfx.tweet.api;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public abstract class Tweeter {

    private static Tweeter INSTANCE;
    private final ReadOnlyObjectWrapper<Exception> latestException = new ReadOnlyObjectWrapper<>(null);

    public static final Tweeter getInstance() {
        if (null == INSTANCE) {
            synchronized (Tweeter.class) {
                createInstance();
            }
        }

        return INSTANCE;
    }

    private static void createInstance() {
        final Iterator<Tweeter> itTweeter = ServiceLoader.load(Tweeter.class).iterator();

        if (itTweeter.hasNext()) {
            INSTANCE = itTweeter.next();
        } else {
            throw new IllegalStateException("No implementation of Tweeter found!");
        }
    }

    public abstract TweetStream createTweetStream(TweetFilterQuery filterQuery);

    public abstract Tweet getTweet(final long tweetId);

    public abstract Stream<Tweet> search(final TweetQuery tweetQuery);

    public abstract Stream<Tweet> searchPaged(final TweetQuery tweetQuery, int numberOfPages);

    public ReadOnlyObjectProperty<Exception> latestException() {
        return latestException.getReadOnlyProperty();
    }

    public final Exception getLatestException() {
        return latestException.get();
    }

    protected final void setLatestException(Exception newValue) {
        latestException.set(newValue);
    }

    public void createTweetStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
