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

import org.mastodon4j.core.api.entities.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserMentionPredicate implements Predicate<Status> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMentionPredicate.class);
    private final Set<String> userList;

    UserMentionPredicate(List<String> userList) {
        this.userList = Objects.requireNonNull(userList, "userList must not be null").stream()
                .map(user -> user.substring(1))
                .collect(Collectors.toCollection(() -> new TreeSet<>(String::compareToIgnoreCase)));
    }

    private boolean isMentionedUser(Status.Mention mention) {
        return userList.contains(mention.username());
    }

    @Override
    public boolean test(Status status) {
        final List<Status.Mention> mentions = status.mentions();
        if (mentions.stream().anyMatch(this::isMentionedUser)) {
            return true;
        } else {
            LOGGER.debug("No matching users {} mentioned in {}", userList, mentions);
            return false;
        }
    }
}
