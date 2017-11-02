/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.Tweeter;
import org.tweetwallfx.tweet.api.entry.EmojiTweetEntry;
import org.tweetwallfx.tweet.api.entry.HashtagTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.TweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

public class TwitterTweeterTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    private Tweeter getTweeter() {
        final Tweeter tweeter = Tweeter.getInstance();
        System.out.println("tweeter: " + tweeter);
        return tweeter;
    }

    private static void skipIfOauthisNotConfigured() {
        Assume.assumeNotNull(
                Configuration.getInstance().getConfig("tweetwall.twitter.oauth.consumerKey", null),
                Configuration.getInstance().getConfig("tweetwall.twitter.oauth.consumerSecret", null),
                Configuration.getInstance().getConfig("tweetwall.twitter.oauth.accessToken", null),
                Configuration.getInstance().getConfig("tweetwall.twitter.oauth.accessTokenSecret", null)
        );
    }

    @Test
    public void gettingInstanceFromTweeter() {
        assertNotNull(getTweeter());
    }

    private void testTextFiltering(final long id, final Class<? extends TweetEntry> tweetEntryClass, final String filteredString) {
        final Tweeter tweeter = getTweeter();
        assertNotNull(tweeter);
        skipIfOauthisNotConfigured();

        final Tweet tweet = tweeter.getTweet(id);
        System.out.println("tweet: " + tweet);
        assertNotNull(tweet);

        final String textWOEmojis = tweet.getTextWithout(EmojiTweetEntry.class).get();
        System.out.println("textWOEmojis: " + textWOEmojis);

        final String textWOEmojisAndEntry = tweet.getTextWithout(EmojiTweetEntry.class).getTextWithout(tweetEntryClass).get();
        System.out.println("textWOEmojisAndEntry: " + textWOEmojisAndEntry);
        System.out.println("filteredString: " + filteredString);
        assertEquals(filteredString, textWOEmojisAndEntry);
    }

    @Test
    public void tweetTextFilteringWorks_therealdanvega_925199490776293376() {
        // https://twitter.com/therealdanvega/status/925199490776293376
        testTextFiltering(
                925199490776293376L,
                HashtagTweetEntry.class,
                "Not sure how many times I can say it… I really love working with and (thanks in large part to )");
    }

    @Test
    public void tweetTextFilteringWorks_vbrabant_925750697861279745() {
        // https://twitter.com/vbrabant/status/925750697861279745
        testTextFiltering(
                925750697861279745L,
                UserMentionTweetEntry.class,
                "");
    }

    @Test
    public void tweetTextFilteringWorks_mraible_925080175091552256() {
        // https://twitter.com/mraible/status/925080175091552256
        testTextFiltering(
                925080175091552256L,
                MediaTweetEntry.class,
                "New @java_hipster stickers coming to a #Devoxx near you! Thanks to @oktadev for sponsoring.");
    }

    private void testEnhancedText(final long id, final String... shallNotContain) {
        final Tweeter tweeter = getTweeter();
        assertNotNull(tweeter);
        skipIfOauthisNotConfigured();

        final Tweet tweet = tweeter.getTweet(id);
        System.out.println("tweet: " + tweet);
        assertNotNull(tweet);

        final String text = tweet.getText();
        System.out.println("text: " + text);

        String displayEnhancedText = tweet.getDisplayEnhancedText();
        System.out.println("displayEnhancedText: " + displayEnhancedText);
        Arrays.stream(shallNotContain).forEach(s -> System.out.println("shallNotContain: " + s));
        Arrays.stream(shallNotContain).forEach(s -> assertFalse(displayEnhancedText.contains(s)));
    }

    @Test
    public void tweetDisplayEnhancedText_vbrabant_925750697861279745() {
        // https://twitter.com/vbrabant/status/925750697861279745
        testEnhancedText(
                925750697861279745L,
                "@Stephan007");

    }
}
