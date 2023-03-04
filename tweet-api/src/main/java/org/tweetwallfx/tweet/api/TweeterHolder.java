/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 TweetWallFX
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

final class TweeterHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TweeterHolder.class);
    private static Tweeter instance;

    static Tweeter instance() {
        if (null == instance) {
            synchronized (TweeterHolder.class) {
                instance = createInstance(ServiceLoader.load(Tweeter.class));
            }
        }
        return instance;
    }

    static Tweeter createInstance(Iterable<Tweeter> tweeterIterable) {
        final List<Tweeter> tweeters = new ArrayList<>(2);
        for (Tweeter tweeter : tweeterIterable) {
            if (tweeter.isEnabled()) {
                LOGGER.info("Found enabled tweeter {}", tweeter);
                tweeters.add(tweeter);
            } else {
                LOGGER.info("Skipped disabled tweeter {}", tweeter);
            }
        }
        final int tweetersAmount = tweeters.size();
        if (tweetersAmount == 1) {
            return tweeters.get(0);
        } else if (tweetersAmount > 1) {
            return new CompositeTweeter(tweeters);
        } else {
            throw new IllegalStateException("No implementation of Tweeter found!");
        }
    }
}
