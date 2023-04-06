/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 TweetWallFX
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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.ConnectionSettings;
import org.tweetwallfx.tweet.impl.twitter4j.config.TwitterSettings;
import twitter4j.Twitter;
import twitter4j.v1.Status;
import twitter4j.v1.TwitterV1;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.tweetwallfx.tweet.impl.twitter4j.TwitterTweeter.TWITTER_SETTINGS;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 * <p>
 * Place your oauth credentials in a properties file:
 * <p>
 * # Step 1. Sign in in https://dev.twitter.com # Step 2. Create an app in
 * https://apps.twitter.com # 2.1 Name: DevoxxTweetWall # 2.2 Description:
 * JavaFX based application for displaying 3D rotating tweets at Devoxx 2014 #
 * 2.3 Website: (add your website or the link of your repository, for instance)
 * # 3. When the application is created, these two keys are generated: # Step
 * 3.1 Assing API Key to oauth.consumerKey # Step 3.2 Assing Api secret to
 * oauth.consumerSecret # Step 4. Click on create an Access token. Two new keys
 * are generated: # Step 4.1 Assign Access token to oauth.accessToken # Step 4.2
 * Assign Acces token secret to oauth.accessTokenSecret
 * <p>
 * Don't share this credentials with anybody, don't commit the properties file
 * to the repo !!
 */
final class TwitterOAuth {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterOAuth.class);
    private static final AtomicReference<TwitterOAuth> INSTANCE_REFERENCE = new AtomicReference<>();
    private static final ReadOnlyObjectWrapper<Exception> EXCEPTION = new ReadOnlyObjectWrapper<>(null);

    private final TwitterV1 twitter;
    private Consumer<Status> statusConsumer;

    private TwitterOAuth() {
        final Configuration tweetWallFxConfig = Configuration.getInstance();
        final Twitter.TwitterBuilder builder = Twitter.newBuilder();

        builder.prettyDebugEnabled(TWITTER_SETTINGS.debugEnabled());
        builder.tweetModeExtended(TWITTER_SETTINGS.extendedMode());

        TwitterSettings.OAuth twitterOAuthSettings = TWITTER_SETTINGS.oauth();
        builder.oAuthConsumer(twitterOAuthSettings.consumerKey(), twitterOAuthSettings.consumerSecret());
        builder.oAuthAccessToken(twitterOAuthSettings.accessToken(), twitterOAuthSettings.accessTokenSecret());

        // optional proxy settings
        tweetWallFxConfig.getConfigTypedOptional(ConnectionSettings.CONFIG_KEY, ConnectionSettings.class)
                .map(ConnectionSettings::proxy)
                .filter(proxy -> !proxy.host().isEmpty())
                .ifPresent(proxy -> {
                    builder.httpProxyHost(proxy.host());
                    builder.httpProxyPort(proxy.port());
                    builder.httpProxyUser(proxy.user());
                    builder.httpProxyPassword(proxy.password());
                });

        builder.onException(failure -> {
            EXCEPTION.set(failure);
            LOGGER.error("Error on twitter backend", failure);
        });
        builder.onStatus(this::onStatus);

        twitter = builder.build().v1();
    }

    private static TwitterOAuth checkOrInitialize(TwitterOAuth oldValue) {
        if (oldValue == null) {
            return new TwitterOAuth();
        }
        return oldValue;
    }

    private void onStatus(Status status) {
        if (statusConsumer == null) {
            LOGGER.debug("Not handled status: {}", status);
        } else {
            statusConsumer.accept(status);
        }
    }

    public TwitterV1 twitterV1() {
        return twitter;
    }

    public TwitterOAuth statusConsumer(Consumer<Status> statusConsumer) {
        this.statusConsumer = statusConsumer;
        return this;
    }

    public static ReadOnlyObjectProperty<Exception> exception() {
        return EXCEPTION.getReadOnlyProperty();
    }

    public static TwitterOAuth instance() {
        return INSTANCE_REFERENCE.updateAndGet(TwitterOAuth::checkOrInitialize);
    }
}
