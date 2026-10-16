/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

/**
 * A user of the social media service providing {@link Tweet Tweets}.
 */
public interface User {

    /**
     * Returns the URL of the larger variant of the profile image.
     *
     * @return the URL of the larger profile image
     */
    String getBiggerProfileImageUrl();

    /**
     * Returns the unique id of this user.
     *
     * @return the id of this user
     */
    long getId();

    /**
     * Returns the language of this user.
     *
     * @return the language of this user
     */
    String getLang();

    /**
     * Returns the display name of this user.
     *
     * @return the display name of this user
     */
    String getName();

    /**
     * Returns the URL of the profile image.
     *
     * @return the URL of the profile image
     */
    String getProfileImageUrl();

    /**
     * Returns the screen name (handle) of this user.
     *
     * @return the screen name of this user
     */
    String getScreenName();

    /**
     * Returns the number of followers of this user.
     *
     * @return the number of followers of this user
     */
    int getFollowersCount();

    /**
     * Determines if this user is verified.
     *
     * @return {@code true} if this user is verified, otherwise {@code false}
     */
    boolean isVerified();
}
