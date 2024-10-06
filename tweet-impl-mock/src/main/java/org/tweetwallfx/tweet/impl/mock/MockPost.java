/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mock;

import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.api.entry.EmojiTweetEntry;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.SymbolTweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

import java.time.LocalDateTime;
import java.util.List;

public record MockPost(long id, String text, User user, LocalDateTime created,
                       Tweet originPost,
                       int favoriteCount, int repostCount,
                       List<MediaTweetEntry> mediaTweetEntries) implements Tweet {

    public MockPost(long id, String text, User user, LocalDateTime created,
                    Tweet originPost,
                    int favoriteCount, int repostCount,
                    MediaTweetEntry... mediaTweetEntries) {
        this(id, text, user, created, originPost, favoriteCount, repostCount, List.of(mediaTweetEntries));
    }

    @Override
    public Tweet originPost() {
        return originPost;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return created;
    }

    @Override
    public int getFavoriteCount() {
        return favoriteCount;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getInReplyToTweetId() {
        return 0;
    }

    @Override
    public long getInReplyToUserId() {
        return 0;
    }

    @Override
    public String getInReplyToScreenName() {
        return null; //TODO
    }

    @Override
    public String getLang() {
        return null; //TODO
    }

    @Override
    public int getRetweetCount() {
        return repostCount;
    }

    @Override
    public Tweet getRetweetedTweet() {
        return null; //TODO
    }

    @Override
    public Tweet getOriginTweet() {
        if (originPost != null) {
            return originPost;
        }
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean isRetweet() {
        return originPost != null;
    }

    @Override
    public boolean isTruncated() {
        return false;
    }

    @Override
    public HashtagTweetEntry[] getHashtagEntries() {
        return new HashtagTweetEntry[0];
    }

    @Override
    public MediaTweetEntry[] getMediaEntries() {
        return mediaTweetEntries.toArray(MediaTweetEntry[]::new);
    }

    @Override
    public SymbolTweetEntry[] getSymbolEntries() {
        return new SymbolTweetEntry[0];
    }

    @Override
    public UrlTweetEntry[] getUrlEntries() {
        return new UrlTweetEntry[0];
    }

    @Override
    public UserMentionTweetEntry[] getUserMentionEntries() {
        return new UserMentionTweetEntry[0];
    }

    @Override
    public EmojiTweetEntry[] getEmojiEntries() {
        return Tweet.super.getEmojiEntries();
    }

    @Override
    public String getDisplayEnhancedText() {
        return Tweet.super.getDisplayEnhancedText();
    }

    @Override
    public TextExtractor getDisplayEnhancedTextExtractor() {
        return Tweet.super.getDisplayEnhancedTextExtractor();
    }
}
