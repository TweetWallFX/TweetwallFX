/*
 * The MIT License
 *
 * Copyright 2015-2018 TweetWallFX
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
package org.tweetwallfx.tweet.api;

import java.util.Arrays;
import java.util.Objects;

public final class TweetFilterQuery {

    private int count = 0;
    private long[] follow = null;
    private String[] track = null;
    private double[][] locations = null;
    private String[] language = null;
    private String filterLevel = null;

    /**
     * Sets count.
     *
     * @param count Indicates the number of previous statuses to stream before
     * transitioning to the live stream.
     * @return this instance
     */
    public TweetFilterQuery count(int count) {
        this.count = count;
        return this;
    }

    public int getCount() {
        return count;
    }

    /**
     * Sets follow.
     *
     * @param follow Specifies the users, by ID, to receive public tweets from.
     * @return this instance
     */
    public TweetFilterQuery follow(long[] follow) {
        this.follow = copy(follow);
        return this;
    }

    public long[] getFollow() {
        return copy(follow);
    }

    /**
     * Sets track.
     *
     * @param track Specifies keywords to track.
     * @return this instance
     */
    public TweetFilterQuery track(String[] track) {
        this.track = copy(track);
        return this;
    }

    public String[] getTrack() {
        return copy(track);
    }

    /**
     * Sets locations.
     *
     * @param locations Specifies the locations to track. 2D array
     * @return this instance
     */
    public TweetFilterQuery locations(double[][] locations) {
        this.locations = copy(locations);
        return this;
    }

    public double[][] getLocations() {
        return copy(locations);
    }

    /**
     * Sets language.
     *
     * @param language Specifies languages to track.
     * @return this instance
     */
    public TweetFilterQuery language(String[] language) {
        this.language = copy(language);
        return this;
    }

    public String[] getLanguage() {
        return copy(language);
    }

    /**
     * The filter level limits what tweets appear in the stream to those with a
     * minimum filter_level attribute value.
     *
     * @param filterLevel one of either none, low, or medium.
     * @return this instance
     */
    public TweetFilterQuery filterLevel(String filterLevel) {
        this.filterLevel = filterLevel;
        return this;
    }

    public String getFilterLevel() {
        return filterLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TweetFilterQuery that = (TweetFilterQuery) o;

        return this.count == that.count
                && Arrays.equals(follow, that.follow)
                && Arrays.equals(track, that.track)
                && Arrays.equals(language, that.language)
                && Objects.equals(filterLevel, that.filterLevel);
    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + Arrays.hashCode(follow);
        result = 31 * result + Objects.hash((Object[]) track);
        result = 31 * result + Arrays.hashCode(language);
        result = 31 * result + Objects.hashCode(filterLevel);
        return result;
    }

    @Override
    public String toString() {
        return "FilterQuery{"
                + "count=" + count
                + ", follow=" + Arrays.toString(follow)
                + ", track=" + Arrays.toString(track)
                + ", locations=" + (locations == null ? null : Arrays.asList(locations))
                + ", language=" + Arrays.toString(language)
                + ", filter_level=" + filterLevel
                + '}';
    }

    private static <T> T[] copy(final T[] ts) {
        return null == ts ? null : Arrays.copyOf(ts, ts.length);
    }

    private static long[] copy(final long[] ts) {
        return null == ts ? null : Arrays.copyOf(ts, ts.length);
    }

    private static double[] copy(final double[] ts) {
        return null == ts ? null : Arrays.copyOf(ts, ts.length);
    }

    private static double[][] copy(final double[][] tss) {
        if (null == tss) {
            return null;
        } else {
            final double[][] res = new double[tss.length][];
            for (int i = 0; i < tss.length; i++) {
                res[i] = copy(tss[i]);
            }
            return res;
        }
    }
}
