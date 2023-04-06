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
import org.mastodon4j.core.api.entities.Search;
import org.mastodon4j.core.api.entities.Status;

import java.util.List;

public class MastodonEntities {
    public static Account createAccount(String id, String userName) {
        return new Account(id, userName, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null);
    }

    public static Status.Mention createMention(String id, String username) {
        return new Status.Mention(id, username, null, username);
    }

    public static Status createStatus(String id, String content) {
        return createStatus(id, content, null, List.of());
    }

    public static Status createStatus(String id, String content, Account account) {
        return createStatus(id, content, account, List.of());
    }

    public static Status createStatus(String id, String content, List<Status.Mention> mentions) {
        return createStatus(id, content, null, mentions);
    }

    public static Status createStatus(String id, String content, Account account, List<Status.Mention> mentions) {
        return new Status(id, null, null, account, content, null, null,
                null, null, null, mentions, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null);
    }

    public static Search createSearch() {
        return new Search(List.of(), List.of(), List.of());
    }
}
