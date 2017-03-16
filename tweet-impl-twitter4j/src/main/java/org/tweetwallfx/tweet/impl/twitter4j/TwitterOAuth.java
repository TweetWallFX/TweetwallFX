/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * Place your oauth credentials in a properties file:
 *
 * # Step 1. Sign in in https://dev.twitter.com # Step 2. Create an app in
 * https://apps.twitter.com # 2.1 Name: DevoxxTweetWall # 2.2 Description:
 * JavaFX based application for displaying 3D rotating tweets at Devoxx 2014 #
 * 2.3 Website: (add your website or the link of your repository, for instance)
 * # 3. When the application is created, these two keys are generated: # Step
 * 3.1 Assing API Key to oauth.consumerKey # Step 3.2 Assing Api secret to
 * oauth.consumerSecret # Step 4. Click on create an Access token. Two new keys
 * are generated: # Step 4.1 Assign Access token to oauth.accessToken # Step 4.2
 * Assign Acces token secret to oauth.accessTokenSecret
 *
 * Don't share this credentials with anybody, don't commit the properties file
 * to the repo !!
 *
 * @author jpereda
 */
final class TwitterOAuth {

    private static final Logger log = LogManager.getLogger(TwitterOAuth.class);
    
    private static Configuration configuration = null;
    private static final AtomicBoolean INITIATED = new AtomicBoolean(false);
    private static final ReadOnlyObjectWrapper<Exception> exception = new ReadOnlyObjectWrapper<>(null);

    private TwitterOAuth() {
    }

    public static ReadOnlyObjectProperty<Exception> exception() {
        return exception.getReadOnlyProperty();
    }
    
    public static Configuration getConfiguration() {
        synchronized (TwitterOAuth.class) {
            if (INITIATED.compareAndSet(false, true)) {
                configuration = createConfiguration();
            }
        }

        return configuration;
    }

    private static Configuration createConfiguration() {
        org.tweetwallfx.config.Configuration tweetWallFxConfig = org.tweetwallfx.config.Configuration.getInstance();
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(false);
        builder.setOAuthConsumerKey(tweetWallFxConfig.getConfig("tweetwall.twitter.oauth.consumerKey"));
        builder.setOAuthConsumerSecret(tweetWallFxConfig.getConfig("tweetwall.twitter.oauth.consumerSecret"));
        builder.setOAuthAccessToken(tweetWallFxConfig.getConfig("tweetwall.twitter.oauth.accessToken"));
        builder.setOAuthAccessTokenSecret(tweetWallFxConfig.getConfig("tweetwall.twitter.oauth.accessTokenSecret"));
        // optional proxy settings
        String httpProxyHost = tweetWallFxConfig.getConfig("tweetwall.proxy.host", "");
        if (!httpProxyHost.isEmpty()) {
            builder.setHttpProxyHost(httpProxyHost);
            builder.setHttpProxyPort(Integer.parseInt(tweetWallFxConfig.getConfig("tweetwall.proxy.port")));
            builder.setHttpProxyUser(tweetWallFxConfig.getConfig("tweetwall.proxy.user", ""));
            builder.setHttpProxyPassword(tweetWallFxConfig.getConfig("tweetwall.proxy.password", ""));
        }
        Configuration conf = builder.build();

        // check Configuration
        if (conf.getOAuthConsumerKey() != null && !conf.getOAuthConsumerKey().isEmpty()
                && conf.getOAuthConsumerSecret() != null && !conf.getOAuthConsumerSecret().isEmpty()
                && conf.getOAuthAccessToken() != null && !conf.getOAuthAccessToken().isEmpty()
                && conf.getOAuthAccessTokenSecret() != null && !conf.getOAuthAccessTokenSecret().isEmpty()) {
            Twitter twitter = new TwitterFactory(conf).getInstance();
            try {
                User user = twitter.verifyCredentials();
                log.info("User " + user.getName() + " validated");
            } catch (TwitterException ex) {
                exception.set(ex);
                //  statusCode=400, message=Bad Authentication data -> wrong token
                //  statusCode=401, message=Could not authenticate you ->wrong consumerkey
                int statusCode = ex.getStatusCode();
                log.error("Error statusCode=" + statusCode + " " + (statusCode > 0 ? ex.getErrorMessage() : ex.getMessage()));
                conf = null;
            }
        } else {
            exception.set(new IllegalStateException("Missing credentials!"));
        }

        return conf;
    }
}
