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

public interface User {

    String getBiggerProfileImageUrl();

    long getId();

    String getLang();

    String getName();

    String getProfileImageUrl();

    String getScreenName();
    
    int getFollowersCount();

    boolean isVerified();
    /**
     * Available but not implemented. {@code
     *
     * String              getBiggerProfileImageUrlHttps();
     * Date                getCreatedAt();
     * String              getDescription();
     * UrlTweetEntry[]     getDescriptionUrlEntries();
     * int                 getFavouritesCount();
     * int                 getFollowersCount();
     * int                 getFriendsCount();
     * int                 getListedCount();
     * String              getLocation();
     * String              getMiniProfileImageUrl();
     * String              getMiniProfileImageUrlHttps();
     * String              getOriginalProfileImageUrl();
     * String              getOriginalProfileImageUrlHttps();
     * String              getProfileBackgroundColor();
     * String              getProfileBackgroundImageUrl();
     * String              getProfileBackgroundImageUrlHttps();
     * String              getProfileBannerIPadUrl();
     * String              getProfileBannerIPadRetinaUrl();
     * String              getProfileBannerMobileUrl();
     * String              getProfileBannerMobileRetinaUrl();
     * String              getProfileBannerRetinaUrl();
     * String              getProfileBannerUrl();
     * String              getProfileImageUrlHttps();
     * String              getProfileLinkColor();
     * String              getProfileSidebarBorderColor();
     * String              getProfileSidebarFillColor();
     * String              getProfileTextColor();
     * String              getTimeZone();
     * Tweet               getTweet();
     * int                 getTweetCount();
     * String              getUrl();
     * UrlTweetEntry       getUrlEntry();
     * int                 getUtcOffset();
     * String[]            getWithheldInCountries();
     * boolean             isContributorsEnabled();
     * boolean             isDefaultProfile();
     * boolean             isDefaultProfileImage();
     * boolean             isFollowRequestSent();
     * boolean             isGeoEnabled();
     * boolean             isProfileBackgroundTiled();
     * boolean             isProfileUseBackgroundImage();
     * boolean             isProtected();
     * boolean             isShowAllInlineMedia();
     * boolean             isTranslator();
     * }
     */
}
