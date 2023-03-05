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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mastodon4j.core.api.entities.Account;
import org.mastodon4j.core.api.entities.Status;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createAccount;
import static org.tweetwallfx.tweet.impl.mastodon4j.MastodonEntities.createStatus;

@MockitoSettings
public class AccountPredicateTest {
    @Mock(name = "org.tweetwallfx.tweet.impl.mastodon4j.AccountPredicate")
    Logger logger;

    AccountPredicate predicate;

    @BeforeEach
    void prepare() {
        predicate = new AccountPredicate(List.of("@reinhapa", "@devoxx"));
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(logger);
    }

    @Test
    void test() {
        final Account account = createAccount("xx", "JohnDoe");
        Status statusOne = createStatus("1", "bli", account);
        Set<String> filterList = new TreeSet<>(String::compareToIgnoreCase);
        filterList.addAll(List.of("devoxx","reinhapa"));

        doNothing().when(logger).debug("No matching users {} for account {}", filterList, "JohnDoe");
        assertThat(predicate.test(statusOne)).isFalse();

        Status statusTwo = createStatus("2", "bla", createAccount("42","REINHAPA"));
        assertThat(predicate.test(statusTwo)).isTrue();
    }
}
