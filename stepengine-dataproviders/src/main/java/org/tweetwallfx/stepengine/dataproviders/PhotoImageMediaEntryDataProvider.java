/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 TweetWallFX
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

import java.util.Arrays;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;
import static org.tweetwallfx.util.Nullable.valueOrDefault;

public class PhotoImageMediaEntryDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LogManager.getLogger(PhotoImageMediaEntryDataProvider.class);
    private final Config config;

    private PhotoImageMediaEntryDataProvider(final Config config) {
        this.config = config;
    }

    public Image getImage(final MediaTweetEntry mte) {
        return new Image(PhotoImageCache.INSTANCE
                .getCached(mte)
                .getInputStream());
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
        if (null == tweet.getMediaEntries()
                || (tweet.isRetweet() && !config.includeRetweets())) {
            return;
        }
        LOG.debug("processing new Tweet: {}", tweet.getId());
        Arrays.stream(tweet.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .forEach(PhotoImageCache.INSTANCE::addToCacheAsync);
    }

    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public PhotoImageMediaEntryDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new PhotoImageMediaEntryDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<PhotoImageMediaEntryDataProvider> getDataProviderClass() {
            return PhotoImageMediaEntryDataProvider.class;
        }
    }

    private static record Config(
            Boolean includeRetweets) {

        @SuppressWarnings("unused")
        public Config(
                final Boolean includeRetweets) {
            this.includeRetweets = valueOrDefault(includeRetweets, false);
        }
    }
}
