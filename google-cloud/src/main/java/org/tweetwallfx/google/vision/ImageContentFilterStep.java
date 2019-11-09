/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 TweetWallFX
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
package org.tweetwallfx.google.vision;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.google.GoogleLikelihood;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;
import static org.tweetwallfx.util.ToString.mapEntry;
import static org.tweetwallfx.util.ToString.mapOf;

/**
 * A {@link FilterStep} handling {@link Tweet}s by checking that photos comply
 * to some safe search criteria. If it does the {@link Tweet} is rejected.
 *
 * In case no safe search violations have occurred with the configured limits it
 * is terminally rejected with {@link Result#REJECTED}. Otherwise it is
 * evaluated as {@link Result#NOTHING_DEFINITE}.
 */
public class ImageContentFilterStep implements FilterStep<Tweet> {

    private static final Logger LOG = LogManager.getLogger(ImageContentFilterStep.class);
    private static final Map<Integer, Function<MediaTweetEntry, String>> MTE_SIZE_TO_URL_FUNCTIONS;

    static {
        final Map<Integer, Function<MediaTweetEntry, String>> tmp = new HashMap<>();

        tmp.put(0, mte -> mte.getMediaUrl() + ":thumb");
        tmp.put(1, mte -> mte.getMediaUrl() + ":small");
        tmp.put(2, mte -> mte.getMediaUrl() + ":medium");
        tmp.put(3, mte -> mte.getMediaUrl() + ":large");

        MTE_SIZE_TO_URL_FUNCTIONS = Collections.unmodifiableMap(tmp);
    }

    private final Config config;
    private final ImageContentAnalysis.SafeSearch requiredSafeSearch;

    private ImageContentFilterStep(final Config config) {
        this.config = config;
        requiredSafeSearch = new ImageContentAnalysis.SafeSearch();
        requiredSafeSearch.setAdult(config.getAdult().getAcceptableLikelyhood());
        requiredSafeSearch.setMedical(config.getMedical().getAcceptableLikelyhood());
        requiredSafeSearch.setRacy(config.getRacy().getAcceptableLikelyhood());
        requiredSafeSearch.setSpoof(config.getSpoof().getAcceptableLikelyhood());
        requiredSafeSearch.setViolence(config.getViolence().getAcceptableLikelyhood());
    }

    @Override
    public Result check(final Tweet tweet) {
        Tweet t = tweet;

        do {
            Result r = checkImages(tweet, t);

            if (r.isTerminal()) {
                return r;
            } else {
                t = t.getRetweetedTweet();
            }
        } while (config.isCheckRetweeted() && null != t);

        LOG.debug("Tweet(id:{}): No terminal decision found -> NOTHING_DEFINITE",
                tweet.getId());
        return Result.NOTHING_DEFINITE;
    }

    private Result checkImages(final Tweet tweet, final Tweet t) {
        final List<MediaTweetEntry> mtes = Stream.of(t.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .collect(Collectors.toList());

        if (mtes.isEmpty()) {
            LOG.debug("Tweet(id:{}): Tweet(id:{}) has no photos -> NOTHING_DEFINITE",
                    tweet.getId(),
                    t.getId());
            return Result.NOTHING_DEFINITE;
        }

        final List<String> imageUrlStrings = mtes.stream().map(this::getImageUrlString).collect(Collectors.toList());
        final Map<String, ImageContentAnalysis> visionAnalysis;

        try {
            visionAnalysis = GoogleVisionCache.INSTANCE.getCachedOrLoad(imageUrlStrings.stream());
        } catch (final IOException ex) {
            LOG.warn(String.format(
                    "Tweet(id:%s): Tweet(id:%s) failed analysation of its photos -> REJECTED",
                    tweet.getId(),
                    t.getId()),
                    ex);
            return Result.REJECTED;
        }

        for (final Map.Entry<String, ImageContentAnalysis> entry : visionAnalysis.entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }

            if (null != entry.getValue().getAnalysisError()) {
                LOG.info("Tweet(id:{}): Tweet(id:{}) photo \"{}\" failed analysation \"{}\" -> REJECTED",
                        tweet.getId(),
                        t.getId(),
                        entry.getValue().getAnalysisError(),
                        entry.getKey());
                return Result.REJECTED;
            }

            final String analysisResult = diff(entry.getValue().getSafeSearch(), requiredSafeSearch);

            if (!analysisResult.isEmpty()) {
                LOG.info("Tweet(id:{}): Tweet(id:{}) photo \"{}\" is not compliant to configuration {} -> REJECTED",
                        tweet.getId(),
                        t.getId(),
                        entry.getKey(),
                        analysisResult);
                return Result.REJECTED;
            }
        }

        return Result.NOTHING_DEFINITE;
    }

    private String getImageUrlString(final MediaTweetEntry mte) {
        return MTE_SIZE_TO_URL_FUNCTIONS
                .getOrDefault(
                        mte.getSizes().keySet().stream().max(Comparator.naturalOrder()).orElse(Integer.MAX_VALUE),
                        this::unsupportedSize)
                .apply(mte);
    }

    private String unsupportedSize(final MediaTweetEntry mte) {
        throw new IllegalArgumentException("Illegal value");
    }

    private String diff(final ImageContentAnalysis.SafeSearch actual, final ImageContentAnalysis.SafeSearch required) {
        return Map.<String, Function<ImageContentAnalysis.SafeSearch, GoogleLikelihood>>of(
                "adult", ImageContentAnalysis.SafeSearch::getAdult,
                "medical", ImageContentAnalysis.SafeSearch::getMedical,
                "racy", ImageContentAnalysis.SafeSearch::getRacy,
                "spoof", ImageContentAnalysis.SafeSearch::getSpoof,
                "violence", ImageContentAnalysis.SafeSearch::getViolence)
                .entrySet().stream()
                .filter(e -> e.getValue().apply(actual).compareTo(e.getValue().apply(required)) > 0)
                .map(e
                        -> String.format(
                        "%s: %s > %s",
                        e.getKey(),
                        e.getValue().apply(actual),
                        e.getValue().apply(required)))
                .collect(Collectors.joining("; "));
    }

    /**
     * Implementation of {@link FilterStep.Factory} creating
     * {@link ImageContentFilterStep}.
     */
    public static final class FactoryImpl implements FilterStep.Factory {

        @Override
        public Class<Tweet> getDomainObjectClass() {
            return Tweet.class;
        }

        @Override
        public Class<ImageContentFilterStep> getFilterStepClass() {
            return ImageContentFilterStep.class;
        }

        @Override
        public ImageContentFilterStep create(final FilterChainSettings.FilterStepDefinition filterStepDefinition) {
            return new ImageContentFilterStep(filterStepDefinition.getConfig(Config.class));
        }
    }

    /**
     * POJO used to configure {@link ImageContentFilterStep}.
     */
    public static final class Config {

        private boolean checkRetweeted = false;
        private SafeTypeConfig adult = new SafeTypeConfig();
        private SafeTypeConfig medical = new SafeTypeConfig();
        private SafeTypeConfig racy = new SafeTypeConfig();
        private SafeTypeConfig spoof = new SafeTypeConfig(GoogleLikelihood.UNLIKELY);
        private SafeTypeConfig violence = new SafeTypeConfig();

        /**
         * Returns a boolean flag controlling whether for a retweet the
         * retweeted Tweet is also checked.
         *
         * @return boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked
         */
        public boolean isCheckRetweeted() {
            return checkRetweeted;
        }

        /**
         * Sets the boolean flag controlling whether for a retweet the retweeted
         * Tweet is also checked.
         *
         * @param checkRetweeted the new value
         */
        public void setCheckRetweeted(final boolean checkRetweeted) {
            this.checkRetweeted = checkRetweeted;
        }

        public SafeTypeConfig getAdult() {
            return adult;
        }

        public void setAdult(SafeTypeConfig adult) {
            this.adult = adult;
        }

        public SafeTypeConfig getMedical() {
            return medical;
        }

        public void setMedical(SafeTypeConfig medical) {
            this.medical = medical;
        }

        public SafeTypeConfig getRacy() {
            return racy;
        }

        public void setRacy(SafeTypeConfig racy) {
            this.racy = racy;
        }

        public SafeTypeConfig getSpoof() {
            return spoof;
        }

        public void setSpoof(SafeTypeConfig spoof) {
            this.spoof = spoof;
        }

        public SafeTypeConfig getViolence() {
            return violence;
        }

        public void setViolence(SafeTypeConfig violence) {
            this.violence = violence;
        }

        @Override
        public String toString() {
            return createToString(this, mapOf(
                    mapEntry("checkRetweeted", isCheckRetweeted()),
                    mapEntry("adult", getAdult()),
                    mapEntry("medical", getMedical()),
                    mapEntry("racy", getRacy()),
                    mapEntry("spoof", getSpoof()),
                    mapEntry("violence", getViolence())
            )) + " extends " + super.toString();
        }
    }

    public static final class SafeTypeConfig {

        private GoogleLikelihood acceptableLikelyhood = GoogleLikelihood.VERY_UNLIKELY;

        public SafeTypeConfig() {
        }

        public SafeTypeConfig(final GoogleLikelihood acceptableLikelyhood) {
            this.acceptableLikelyhood = acceptableLikelyhood;
        }

        public GoogleLikelihood getAcceptableLikelyhood() {
            return acceptableLikelyhood;
        }

        public void setAcceptableLikelyhood(final GoogleLikelihood acceptableLikelyhood) {
            this.acceptableLikelyhood = acceptableLikelyhood;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "acceptableLikelyhood", getAcceptableLikelyhood()
            )) + " extends " + super.toString();
        }
    }
}
