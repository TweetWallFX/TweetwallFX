/*
 * The MIT License
 *
 * Copyright 2018 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.io.InputStream;
import java.util.function.Supplier;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.tweetwall.util.ToString.createToString;
import static org.tweetwall.util.ToString.map;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.User;

public class TweetUserProfileImageDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LogManager.getLogger(TweetUserProfileImageDataProvider.class);
    private final Config config;

    private TweetUserProfileImageDataProvider(final Config config) {
        this.config = config;
    }

    public Image getImage(final User user) {
        return new Image(
                ProfileImageCache.INSTANCE.getCachedOrLoad(user.getProfileImageUrl()).get(),
                config.getProfileWidth(),
                config.getProfileHeight(),
                config.isPreserveRation(),
                config.isSmooth());
    }

    public Image getImageBig(final User user) {
        return new Image(
                ProfileImageCache.INSTANCE.getCachedOrLoad(user.getBiggerProfileImageUrl()).get(),
                config.getProfileWidth(),
                config.getProfileHeight(),
                config.isPreserveRation(),
                config.isSmooth());
    }

    @Override
    public void processHistoryTweet(final Tweet tweet) {
        processTweet(tweet);
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        processTweet(tweet);
    }

    private void processTweet(final Tweet tweet) {
        LOG.info("new Tweet received: {}", tweet.getId());
        processTweetImpl(tweet);
    }

    private void processTweetImpl(final Tweet tweet) {
        processUser(tweet.getUser());

        final Tweet retweet = tweet.getRetweetedTweet();

        if (null != retweet) {
            processTweetImpl(retweet);
        }
    }

    private void processUser(final User user) {
        ProfileImageCache.INSTANCE.getCachedOrLoad(user.getProfileImageUrl(), this::handleLoadedContent);
        ProfileImageCache.INSTANCE.getCachedOrLoad(user.getBiggerProfileImageUrl(), this::handleLoadedContent);
    }

    private void handleLoadedContent(final Supplier<InputStream> cache) {
        // do nothing
    }

    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public TweetUserProfileImageDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new TweetUserProfileImageDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<TweetUserProfileImageDataProvider> getDataProviderClass() {
            return TweetUserProfileImageDataProvider.class;
        }
    }

    public static class Config {

        private int profileWidth = 64;
        private int profileHeight = 64;
        private boolean preserveRation = true;
        private boolean smooth = false;

        public int getProfileWidth() {
            return profileWidth;
        }

        public void setProfileWidth(final int profileWidth) {
            this.profileWidth = profileWidth;
        }

        public int getProfileHeight() {
            return profileHeight;
        }

        public void setProfileHeight(final int profileHeight) {
            this.profileHeight = profileHeight;
        }

        public boolean isPreserveRation() {
            return preserveRation;
        }

        public void setPreserveRation(final boolean preserveRation) {
            this.preserveRation = preserveRation;
        }

        public boolean isSmooth() {
            return smooth;
        }

        public void setSmooth(final boolean smooth) {
            this.smooth = smooth;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "profileWidth", getProfileWidth(),
                    "profileHeight", getProfileHeight(),
                    "preserveRation", isPreserveRation(),
                    "smooth", isSmooth()
            )) + " extends " + super.toString();
        }
    }
}
