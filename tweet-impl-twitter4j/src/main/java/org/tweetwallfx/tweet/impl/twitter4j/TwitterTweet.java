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
package org.tweetwallfx.tweet.impl.twitter4j;

import java.util.Arrays;
import java.util.Date;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.SymbolTweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

final class TwitterTweet implements Tweet {

    private static final HashtagTweetEntry[] NIL_HTES = new HashtagTweetEntry[0];
    private static final MediaTweetEntry[] NIL_MTES = new MediaTweetEntry[0];
    private static final SymbolTweetEntry[] NIL_STES = new SymbolTweetEntry[0];
    private static final UrlTweetEntry[] NIL_UTES = new UrlTweetEntry[0];
    private static final UserMentionTweetEntry[] NIL_UMTES = new UserMentionTweetEntry[0];
    private final Status status;
    private final TwitterUser user;
    private final HashtagTweetEntry[] hashtagTweetEntries;
    private final MediaTweetEntry[] mediaTweetEntries;
    private final SymbolTweetEntry[] symbolTweetEntries;
    private final UrlTweetEntry[] urlTweetTweetEntries;
    private final UserMentionTweetEntry[] userMentionTweetEntries;
    private final TwitterTweet retweetedTweet;

    public TwitterTweet(final Status status) {
        this.status = status;
        this.user = new TwitterUser(status);

        final HashtagEntity[] hashtagEntities = status.getHashtagEntities();

        if (null == hashtagEntities) {
            hashtagTweetEntries = NIL_HTES;
        } else {
            hashtagTweetEntries = Arrays.stream(hashtagEntities)
                    .map(he -> new TwitterHashtagTweetEntry(he))
                    .toArray(c -> new HashtagTweetEntry[c]);
        }

        final MediaEntity[] mediaEntities = status.getMediaEntities();

        if (null == mediaEntities) {
            mediaTweetEntries = NIL_MTES;
        } else {
            mediaTweetEntries = Arrays.stream(mediaEntities)
                    .map(me -> new TwitterMediaTweetEntry(me))
                    .toArray(c -> new MediaTweetEntry[c]);
        }

        final SymbolEntity[] symbolEntities = status.getSymbolEntities();

        if (null == symbolEntities) {
            symbolTweetEntries = NIL_STES;
        } else {
            symbolTweetEntries = Arrays.stream(symbolEntities)
                    .map(se -> new TwitterSymbolTweetEntry(se))
                    .toArray(c -> new SymbolTweetEntry[c]);
        }

        final URLEntity[] urlEntities = status.getURLEntities();

        if (null == urlEntities) {
            urlTweetTweetEntries = NIL_UTES;
        } else {
            urlTweetTweetEntries = Arrays.stream(urlEntities)
                    .map(ue -> new TwitterUrlTweetEntry(ue))
                    .toArray(c -> new UrlTweetEntry[c]);
        }

        final UserMentionEntity[] userMentionEntities = status.getUserMentionEntities();

        if (null == userMentionEntities) {
            userMentionTweetEntries = NIL_UMTES;
        } else {
            userMentionTweetEntries = Arrays.stream(userMentionEntities)
                    .map(ume -> new TwitterUserMentionTweetEntry(ume))
                    .toArray(c -> new UserMentionTweetEntry[c]);
        }
        
        retweetedTweet = isRetweet()
                ? new TwitterTweet(status.getRetweetedStatus())
                : null;
    }

    @Override
    public Date getCreatedAt() {
        return status.getCreatedAt();
    }

    @Override
    public int getFavoriteCount() {
        return status.getFavoriteCount();
    }

    @Override
    public long getId() {
        return status.getId();
    }

    @Override
    public String getInReplyToScreenName() {
        return status.getInReplyToScreenName();
    }

    @Override
    public long getInReplyToTweetId() {
        return status.getInReplyToStatusId();
    }

    @Override
    public long getInReplyToUserId() {
        return status.getInReplyToUserId();
    }

    @Override
    public String getLang() {
        return status.getLang();
    }

    @Override
    public int getRetweetCount() {
        return status.getRetweetCount();
    }

    @Override
    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    @Override
    public String getText() {
        return status.getText();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean isRetweet() {
        return status.isRetweet();
    }

    @Override
    public HashtagTweetEntry[] getHashtagEntries() {
        return hashtagTweetEntries;
    }

    @Override
    public MediaTweetEntry[] getMediaEntries() {
        return mediaTweetEntries;
    }

    @Override
    public SymbolTweetEntry[] getSymbolEntries() {
        return symbolTweetEntries;
    }

    @Override
    public UrlTweetEntry[] getUrlEntries() {
        return urlTweetTweetEntries;
    }

    @Override
    public UserMentionTweetEntry[] getUserMentionEntries() {
        return userMentionTweetEntries;
    }

    private static class TwitterUser implements User {

        private final twitter4j.User user;

        public TwitterUser(final Status status) {
            this.user = status.getUser();
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
    }

    @Override
    public String toString() {
        return "TwitterTweet{" + "status=" + status + '}';
    }
}
