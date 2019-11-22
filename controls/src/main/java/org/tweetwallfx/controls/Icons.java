/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TweetWallFX
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
package org.tweetwallfx.controls;

import javafx.scene.shape.Shape;
import javafx.scene.shape.SVGPath;

/**
 * Icons generated via an SVG Path.
 */
public enum Icons {

    /**
     * <a href="http://materialdesignicons.com/icon/check-circle">Material
     * Design Icons: Check circle</a>
     */
    CHECK_CIRCLE(
            "M12 2C6.5 2 2 6.5 2 12S6.5 22 12 22 22 17.5 22 12 17.5 2 12 2M10 17L5 12L6.41 10.59L10 14.17L17.59 6.58L19 8L10 17Z"),
    /**
     * <a href="http://materialdesignicons.com/icon/square">Material Design
     * Icons: Square</a>
     */
    SQUARE(
            "M3,3V21H21V3"),
    /**
     * <a href="http://materialdesignicons.com/icon/thumb-up">Material Design
     * Icons: Thumb Up</a>
     */
    THUMB_UP(
            "M23,10C23,8.89 22.1,8 21,8H14.68L15.64,3.43C15.66,3.33 15.67,3.22 15.67,3.11C15.67,2.7 15.5,2.32 15.23,2.05L14.17,1L7.59,7.58C7.22,7.95 7,8.45 7,9V19A2,2 0 0,0 9,21H18C18.83,21 19.54,20.5 19.84,19.78L22.86,12.73C22.95,12.5 23,12.26 23,12V10M1,21H5V9H1V21Z"),
    /**
     * <a href="http://materialdesignicons.com/icon/twitter-retweet">Material
     * Design Icons: Twitter Retweet</a>
     */
    TWITTER_RETWEET(
            "M6,5.75L10.25,10H7V16H13.5L15.5,18H7A2,2 0 0,1 5,16V10H1.75L6,5.75M18,18.25L13.75,14H17V8H10.5L8.5,6H17A2,2 0 0,1 19,8V14H22.25L18,18.25Z");
    private final String svg;

    Icons(final String svg) {
        this.svg = svg;
    }

    public Shape createShape() {
        final SVGPath result = new SVGPath();
        result.setContent(svg);
//        result.prefWidth​(24);
//        result.prefHeight(24);
        return result;
    }
}
