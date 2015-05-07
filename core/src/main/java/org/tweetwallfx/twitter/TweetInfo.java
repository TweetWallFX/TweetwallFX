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
package org.tweetwallfx.twitter;

import java.util.Date;
import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * @author jpereda
 */
public class TweetInfo {

    private final Status status;

    public TweetInfo(Status status) {
        this.status = status;
    }

    public String getName() {
        return status.getUser().getName();
    }

    public String getText() {
        return status.getText();
    }

    public String getImageURL() {
        return status.getUser().getProfileImageURL();
    }

    public String getHandle() {
        return status.getUser().getScreenName();
    }

    public Date getDate() {
        return status.getCreatedAt();
    }

    public MediaEntity[] getMediaEntities() {
        return status.getMediaEntities();
    }

    public int getFavoriteCount() {
        return status.getFavoriteCount();
    }

    public int getRetweetCount() {
        return status.getRetweetCount();
    }

    @Override
    public String toString() {
        return "TweetInfo{" + "status=" + status + '}';
    }

}
