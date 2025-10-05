/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 TweetWallFX
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InputStream;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.image.Image;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.util.Nullable;
import org.tweetwallfx.util.ToString;
import org.tweetwallfx.util.image.ExifData;

/**
 * Storage of a timestamped, optionally categorized, {@link Image} based on
 * binary data. The image contained within is rotated and flipped in accordance
 * with the image's EXIF data.
 */
public final class ImageStorage {

    /**
     * Key name for the category value in {@link #getAdditionalInfo()}.
     */
    public static final String KEY_CATEGORY = "category";

    /**
     * Default category value.
     */
    public static final String DEFAULT_CATEGORY = "DEFAULT";

    /**
     * Comparator sorting the {@link ImageStorage} instances based on the
     * natural sorting of their {@link #getTimestamp() timestamp} values.
     */
    public static final Comparator<ImageStorage> BY_TIMESTAMP = Comparator
            .comparing(ImageStorage::getTimestamp);

    private final Instant timestamp;
    private final Image image;
    private final String digest;
    private final Map<String, Object> additionalInfo;
    private final List<ExifData> exifDatas;

    /**
     * Creates an ImageStorage instance based on the given parameters.
     *
     * @param builder the builder containing the required data for the instance
     * creation
     */
    private ImageStorage(final Builder builder) {
        this.timestamp = Objects.requireNonNull(builder.timestamp, "timestamp must not be null");
        this.image = new Image(Objects.requireNonNull(builder.inputStream, "inputStream must not be null"));
        this.digest = Objects.requireNonNull(builder.digest, "digest must not be null");
        this.additionalInfo = builder.additionalInfo;
        this.exifDatas = builder.exifDatas;
    }

    /**
     * Returns the timestamp associated with the {@link Image}.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the {@link Image}.
     *
     * @return the {@link Image}
     */
    @SuppressFBWarnings
    public Image getImage() {
        return image;
    }

    /**
     * Returns the additional infos for this {@link ImageStorage} instance.
     *
     * @return the additional infos
     */
    public Map<String, Object> getAdditionalInfo() {
        return Nullable.nullable(additionalInfo);
    }

    /**
     * Returns the EXIF Data of the stored image if it has any.
     *
     * @return the EXIF Data
     */
    public List<ExifData> getExifDatas() {
        return Nullable.nullable(exifDatas);
    }

    /**
     * Returns the category of this storage. The value is retrieved from
     * {@link #getAdditionalInfo()} via the key {@link #KEY_CATEGORY}. If none
     * is present then {@link #DEFAULT_CATEGORY} is returned.
     *
     * @return the category
     */
    @SuppressWarnings("unchecked")
    public String getCategory() {
        return (String) additionalInfo.getOrDefault(KEY_CATEGORY, DEFAULT_CATEGORY);
    }

    /**
     * Attemps to locate an EXIF data entry by {@code directory} and
     * {@code tagName}.
     *
     * @param directory the name of the directory
     *
     * @param tagName the name of the tag
     *
     * @return an {@link Optional} wrapping the located value or an empty
     * {@link Optional} if none was found
     */
    public Optional<String> locateOptionalExifData(final String directory, final String tagName) {
        return ExifData.locateOptional(exifDatas, directory, tagName);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(getTimestamp());
        hash = 97 * hash + Objects.hashCode(digest);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ImageStorage other
                && Objects.equals(this.getTimestamp(), other.getTimestamp())
                && Objects.equals(this.digest, other.digest);
    }

    @Override
    public String toString() {
        return ToString.createToString(
                this,
                Map.of(
                        "timestamp", getTimestamp(),
                        "digest", digest,
                        "additionalInfo", getAdditionalInfo()
                ),
                true);
    }

    /**
     * Creates a new instance of {@link Builder} using the required
     * {@code timestamp} parameter as a starting value.
     *
     * @param timestamp the timestamp associated with the image
     *
     * @return the created {@link Builder}
     */
    public static Builder builder(final Instant timestamp) {
        return new Builder(timestamp);
    }

    /**
     * A builder of {@link ImageStorage} instances.
     */
    public static final class Builder {

        private final Instant timestamp;
        private InputStream inputStream;
        private String digest;
        private Map<String, Object> additionalInfo = Map.of();
        private List<ExifData> exifDatas = List.of();

        private Builder(final Instant timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
        }

        /**
         * Uses the current state of this {@link Builder} instance to create a
         * new instance of {@link ImageStorage}
         *
         * @return the created {@link ImageStorage} instance
         */
        public ImageStorage build() {
            return new ImageStorage(this);
        }

        /**
         * Configures the image data source to be the given {@code inputStream}.
         *
         * @param inputStream the image data source
         *
         * @return this builder instance
         */
        public Builder from(final InputStream inputStream) {
            this.inputStream = Objects.requireNonNull(inputStream, "inputStream must not be null");
            return this;
        }

        /**
         * Uses the {@link URLContent} to configures the
         * {@link #from(java.io.InputStream) image data source}, {@link #withExifTags(java.io.InputStream) EXIF data}
         * and the {@link #withDigest(java.lang.String) digest} using
         * {@link URLContent#getInputStream()} and {@link URLContent#digest()}
         * respectifely.
         *
         * @param urlc the {@link URLContent}
         *
         * @return this builder instance
         */
        public Builder from(final URLContent urlc) {
            return from(urlc.getInputStream())
                    .withExifTags(urlc.getInputStream())
                    .withDigest(urlc.digest());
        }

        /**
         * Configures the additional infos for the {@link ImageStorage}
         * instance.
         *
         * @param additionalInfo the additional info
         *
         * @return this builder instance
         */
        @SuppressWarnings("unchecked")
        public Builder withAdditionalInfo(final Map<String, Object> additionalInfo) {
            this.additionalInfo = Configuration.mergeMap(
                    // ensure category is set to default value
                    Map.of(KEY_CATEGORY, DEFAULT_CATEGORY),
                    // possibly overwrite it with the additionalInfo parameter
                    Nullable.nullable(additionalInfo));
            return this;
        }

        /**
         * Configures the digest of the image data source.
         *
         * @param digest the digest
         *
         * @return this builder instance
         */
        public Builder withDigest(final String digest) {
            this.digest = digest;
            return this;
        }

        /**
         * Attempts to read EXIF Data from the given stream and stores it for
         * evaluation.
         *
         * Note that the {@link InputStream inputStream} is consumed eagerly.
         *
         * @param inputStream the image data source
         *
         * @return this builder instance
         */
        public Builder withExifTags(final InputStream inputStream) {
            this.exifDatas = ExifData.readFrom(inputStream);
            return this;
        }
    }
}
