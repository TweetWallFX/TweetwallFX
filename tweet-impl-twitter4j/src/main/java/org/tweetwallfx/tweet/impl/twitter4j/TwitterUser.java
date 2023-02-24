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
package org.tweetwallfx.tweet.impl.twitter4j;

import org.tweetwallfx.tweet.api.User;

public class TwitterUser implements User {

    private final twitter4j.v1.User user;

    public TwitterUser(final twitter4j.v1.User user) {
        this.user = user;
    }

    @Override
    public String getBiggerProfileImageUrl() {
        return user.getBiggerProfileImageURL();
    }

    @Override
    public long getId() {
        return user.getId();
    }

    @Override
    public String getLang() {
        return user.getLang();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getProfileImageUrl() {
        return user.getProfileImageURL();
    }

    @Override
    public String getScreenName() {
        return user.getScreenName();
    }

    @Override
    public boolean isVerified() {
        return user.isVerified();
    }

    @Override
    public int getFollowersCount() {
        return user.getFollowersCount();
    }
}
