/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 TweetWallFX
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.User;
import org.tweetwallfx.tweet.impl.twitter4j.config.TwitterSettings;
import org.tweetwallfx.tweet.api.entry.EmojiTweetEntry;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.TweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TwitterTweeterTest {

    private Tweeter getTweeter() {
        final Tweeter tweeter = Tweeter.getInstance();
        System.out.println("tweeter: " + tweeter);
        return tweeter;
    }

    static boolean oauthIsConfigured() {
        final TwitterSettings twitterSettings = Configuration.getInstance()
                .getConfigTyped(TwitterSettings.CONFIG_KEY, TwitterSettings.class);
        return twitterSettings != null &&
               twitterSettings.oauth() != null &&
               twitterSettings.oauth().accessToken() != null &&
               twitterSettings.oauth().accessTokenSecret() != null &&
               twitterSettings.oauth().consumerKey() != null &&
               twitterSettings.oauth().consumerSecret() != null;
    }

    @Test
    void gettingInstanceFromTweeter() {
        assertThat(getTweeter()).isNotNull();
    }

    private void testTextFiltering(final long id, final Class<? extends TweetEntry> tweetEntryClass, final String filteredString) {
        final Tweeter tweeter = getTweeter();
        assertThat(tweeter).isNotNull();

        final Tweet tweet = tweeter.getTweet(id);
        System.out.println("tweet: " + tweet);
        assertThat(tweet).isNotNull();
        System.out.println("tweet.text: " + tweet.getText());

        final String textWOEmojis = tweet.getTextWithout(EmojiTweetEntry.class).get();
        System.out.println("textWOEmojis: " + textWOEmojis);

        final String textWOEmojisAndEntry = tweet.getTextWithout(EmojiTweetEntry.class).getTextWithout(tweetEntryClass).get();
        System.out.println("textWOEmojisAndEntry: " + textWOEmojisAndEntry);
        System.out.println("filteredString: " + filteredString);
        assertThat(textWOEmojisAndEntry).isEqualTo(filteredString);
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetTextFilteringWorks_therealdanvega_925199490776293376() {
        // https://twitter.com/therealdanvega/status/925199490776293376
        testTextFiltering(
                925199490776293376L,
                HashtagTweetEntry.class,
                "Not sure how many times I can say it… I really love working with and (thanks in large part to )");
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetTextFilteringWorks_vbrabant_925750697861279745() {
        // https://twitter.com/vbrabant/status/925750697861279745
        testTextFiltering(
                925750697861279745L,
                UserMentionTweetEntry.class,
                "");
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetTextFilteringWorks_mraible_925080175091552256() {
        // https://twitter.com/mraible/status/925080175091552256
        testTextFiltering(
                925080175091552256L,
                MediaTweetEntry.class,
                "New @java_hipster stickers coming to a #Devoxx near you! Thanks to @oktadev for sponsoring.");
    }

    private void testEnhancedText(final long id, final String... shallNotContain) {
        final Tweet tweet = getTweeter().getTweet(id);
        System.out.println("tweet: " + tweet);
        assertThat(tweet).isNotNull();

        final String text = tweet.getText();
        System.out.println("text: " + text);

        String displayEnhancedText = tweet.getDisplayEnhancedText();
        System.out.println("displayEnhancedText: " + displayEnhancedText);
        Arrays.stream(shallNotContain).forEach(s -> System.out.println("shallNotContain: " + s));
        Arrays.stream(shallNotContain).forEach(s -> assertThat(displayEnhancedText.contains(s)).isFalse());
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetDisplayEnhancedText_vbrabant_925750697861279745() {
        // https://twitter.com/vbrabant/status/925750697861279745
        testEnhancedText(
                925750697861279745L,
                "@Stephan007");
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetGetFriends() {
        final Tweeter tweeter = getTweeter();
        List<String> users = tweeter
                .getFriends("Devoxx")
                .map(User::getScreenName)
                .limit(1000)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        System.out.println("users.size: " + users.size());
        users.forEach(System.out::println);
        assertThat(users).withFailMessage("Did not find any Users").isNotEmpty();
    }

    @Test
    @EnabledIf("oauthIsConfigured")
    void tweetGetFollowers() {
        final Tweeter tweeter = getTweeter();
        List<String> users = tweeter
                .getFollowers("Devoxx")
                .map(User::getScreenName)
                .limit(1000)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        System.out.println("users.size: " + users.size());
        users.forEach(System.out::println);
        assertThat(users).withFailMessage("Did not find any Users").isNotEmpty();
    }
}
