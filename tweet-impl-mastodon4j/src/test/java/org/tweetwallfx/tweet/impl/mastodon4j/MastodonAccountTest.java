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

import org.junit.jupiter.api.Test;
import org.mastodon4j.core.api.entities.Account;
import org.mastodon4j.core.api.entities.Field;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MastodonAccountTest {
    MastodonAccount accountOne = new MastodonAccount(new Account("1", "userName1", null, null,
            "displayName1", null, "avatar1", null, null, null, null,
            List.of(), null, null, null, null, null, null, null,
            null, null, null, null, 11, null));
    MastodonAccount accountTwo = new MastodonAccount(new Account("2", "userName2", null, null,
            "displayName2", null, "avatar2", null, null, null, null,
            fields(), null, null, null, null, null, null, null,
            null, null, null, null, 22, null));

    @Test
    void getBiggerProfileImageUrl() {
        assertThat(accountOne.getBiggerProfileImageUrl()).isEqualTo("avatar1");
        assertThat(accountTwo.getBiggerProfileImageUrl()).isEqualTo("avatar2");
    }

    @Test
    void getId() {
        assertThat(accountOne.getId()).isEqualTo(1L);
        assertThat(accountTwo.getId()).isEqualTo(2L);
    }

    @Test
    void getLang() {
        assertThat(accountOne.getLang()).isEqualTo("not-supported");
        assertThat(accountTwo.getLang()).isEqualTo("not-supported");
    }

    @Test
    void getName() {
        assertThat(accountOne.getName()).isEqualTo("userName1");
        assertThat(accountTwo.getName()).isEqualTo("userName2");
    }

    @Test
    void getProfileImageUrl() {
        assertThat(accountOne.getProfileImageUrl()).isEqualTo("avatar1");
        assertThat(accountTwo.getProfileImageUrl()).isEqualTo("avatar2");
    }

    @Test
    void getScreenName() {
        assertThat(accountOne.getScreenName()).isEqualTo("displayName1");
        assertThat(accountTwo.getScreenName()).isEqualTo("displayName2");
    }

    @Test
    void getFollowersCount() {
        assertThat(accountOne.getFollowersCount()).isEqualTo(11);
        assertThat(accountTwo.getFollowersCount()).isEqualTo(22);
    }

    @Test
    void isVerified() {
        assertThat(accountOne.isVerified()).isFalse();
        assertThat(accountTwo.isVerified()).isTrue();
    }

    private static List<Field> fields() {
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("fieldOne", "valueOne", null));
        fields.add(new Field("fieldTwo", "valueTwo", ZonedDateTime.now(ZoneId.systemDefault())));
        fields.add(new Field("fieldThree", "valueThree", null));
        return fields;
    }
}
