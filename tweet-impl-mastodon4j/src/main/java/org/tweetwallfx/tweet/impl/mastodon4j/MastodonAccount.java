/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 TweetWallFX
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
package org.tweetwallfx.tweet.impl.mastodon4j;

import org.mastodon4j.core.api.entities.Account;
import org.mastodon4j.core.api.entities.Field;
import org.tweetwallfx.tweet.api.User;

import java.util.Objects;

public class MastodonAccount implements User {
    private final Account account;

    public MastodonAccount(Account account) {
        this.account = account;
    }

    @Override
    public String getBiggerProfileImageUrl() {
        return getProfileImageUrl();
    }

    @Override
    public long getId() {
        return Long.parseLong(account.id());
    }

    @Override
    public String getLang() {
        return "not-supported";
    }

    @Override
    public String getName() {
        return account.username();
    }

    @Override
    public String getProfileImageUrl() {
        return account.avatar();
    }

    @Override
    public String getScreenName() {
        return account.display_name();
    }

    @Override
    public int getFollowersCount() {
        return account.followers_count();
    }

    @Override
    public boolean isVerified() {
        return account.fields().stream().map(Field::verified_at).anyMatch(Objects::nonNull);
    }
}
