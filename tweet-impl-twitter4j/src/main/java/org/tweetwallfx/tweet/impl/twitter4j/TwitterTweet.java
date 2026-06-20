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
package org.tweetwallfx.tweet.impl.twitter4j;

import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.SymbolTweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;
import twitter4j.v1.HashtagEntity;
import twitter4j.v1.MediaEntity;
import twitter4j.v1.Status;
import twitter4j.v1.SymbolEntity;
import twitter4j.v1.URLEntity;
import twitter4j.v1.UserMentionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class TwitterTweet implements Tweet {

    private final Status status;
    private final TwitterUser user;
    private final List<HashtagTweetEntry> hashtagTweetEntries;
    private final List<MediaTweetEntry> mediaTweetEntries;
    private final List<SymbolTweetEntry> symbolTweetEntries;
    private final List<UrlTweetEntry> urlTweetTweetEntries;
    private final List<UserMentionTweetEntry> userMentionTweetEntries;
    private final TwitterTweet retweetedTweet;

    public TwitterTweet(final Status status) {
        this.status = status;
        this.user = new TwitterUser(status.getUser());

        hashtagTweetEntries = Optional.ofNullable(status.getHashtagEntities())
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(TwitterHashtagTweetEntry::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        mediaTweetEntries = Optional.ofNullable(status.getMediaEntities())
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(TwitterMediaTweetEntry::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        symbolTweetEntries = Optional.ofNullable(status.getSymbolEntities())
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(TwitterSymbolTweetEntry::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        urlTweetTweetEntries = Optional.ofNullable(status.getURLEntities())
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(TwitterUrlTweetEntry::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        userMentionTweetEntries = Optional.ofNullable(status.getUserMentionEntities())
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(TwitterUserMentionTweetEntry::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

        retweetedTweet = isRetweet()
                ? new TwitterTweet(status.getRetweetedStatus())
                : null;
    }

    @Override
    public LocalDateTime getCreatedAt() {
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
    public Tweet getOriginTweet() {
        Tweet rt = this;

        while (rt.isRetweet()) {
            rt = rt.getRetweetedTweet();
        }

        return rt;
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
    public boolean isTruncated() {
        return status.isTruncated();
    }

    @Override
    public List<HashtagTweetEntry> getHashtagEntries() {
        return List.copyOf(hashtagTweetEntries);
    }

    @Override
    public List<MediaTweetEntry> getMediaEntries() {
        return List.copyOf(mediaTweetEntries);
    }

    @Override
    public List<SymbolTweetEntry> getSymbolEntries() {
        return List.copyOf(symbolTweetEntries);
    }

    @Override
    public List<UrlTweetEntry> getUrlEntries() {
        return List.copyOf(urlTweetTweetEntries);
    }

    @Override
    public List<UserMentionTweetEntry> getUserMentionEntries() {
        return List.copyOf(userMentionTweetEntries);
    }

    @Override
    public String toString() {
        return "TwitterTweet{" + "status=" + status + '}';
    }
}
