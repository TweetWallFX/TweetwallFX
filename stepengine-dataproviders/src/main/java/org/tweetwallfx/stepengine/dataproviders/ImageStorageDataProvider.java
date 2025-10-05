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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.stepengine.api.DataProvider;

/**
 * DataProvider handling {@link ImageStorage} instances of possibly multiple
 * types.
 */
public interface ImageStorageDataProvider extends DataProvider {

    /**
     * Adds a new {@link ImageStorage} created with the given values.
     *
     * @param uc the {@link URLContent} containing the image data
     *
     * @param instant the time associated with the image (i.e. the time the
     * image was posted)
     *
     * @param additionalInfo additional info for the stored image
     */
    default void add(final URLContent uc, final Instant instant, final Map<String, Object> additionalInfo) {
        add(ImageStorage.builder(instant).from(uc).withAdditionalInfo(additionalInfo).build());
    }

    /**
     * Adds a new {@link ImageStorage} created with the given values.
     *
     * @param uc the {@link URLContent} containing the image data
     *
     * @param instant the time associated with the image (i.e. the time the
     * image was posted)
     */
    default void add(final URLContent uc, final Instant instant) {
        add(uc, instant, Map.of());
    }

    /**
     * Adds the given {@link ImageStorage} to the internal storage.
     *
     * @param imageStorage the new entry
     */
    void add(final ImageStorage imageStorage);

    /**
     * Returns the {@link Access} for the default category
     * ({@link ImageStorage#DEFAULT_CATEGORY}).
     *
     * @return the {@link Access}
     */
    default Access getAccess() {
        return getAccess(ImageStorage.DEFAULT_CATEGORY);
    }

    /**
     * Returns the {@link Access} for the requested category.
     *
     * @param category the category to access
     *
     * @return the {@link Access}
     */
    Access getAccess(final String category);

    /**
     * Returns the names of the categories that can be accessed with images.
     * When images have already been added then the categories of those images
     * are produced. Otherwise it will only contain
     * {@link ImageStorage#DEFAULT_CATEGORY}.
     *
     * @return the category names
     */
    Set<String> getCategories();

    /**
     * Data retriever of stored image data.
     */
    static interface Access {

        /**
         * Provides the count of stored images currently available in this data
         * provider.
         *
         * @return the count of stored images
         */
        int count();

        /**
         * Produces a list of the first {@code maxCount} stored images.
         *
         * The produced list will contain at most {@code maxCount} stored
         * images. In case more stored images are available than have been
         * requested then the first {@code maxCount} will be returned. When
         * there are fewer stored images than those available ones will be
         * returned.
         *
         * @param maxCount the maximum count of stored images to return (based
         * on availability)
         *
         * @return the produced list
         */
        List<ImageStorage> getImages(final int maxCount);
    }

    /**
     * Opinionated base implementation of ImageStorageDataProvider missing the
     * code determining of what data to load. This may e.g. be the content of
     * the MediaEntry elements of a Twitter message (i.e. the image posted via
     * the Twitter message).
     */
    static abstract class Base implements ImageStorageDataProvider {

        private static final Logger LOG = LoggerFactory.getLogger(Base.class);
        private final Map<String, SequencedSet<ImageStorage>> categorizedImageStorages = new ConcurrentHashMap<>();
        private final int maxCacheSize;

        /**
         * Creates a new instance with the given parameters.
         *
         * @param maxCacheSize the maximum number of entries being kept by
         */
        protected Base(final int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
        }

        @Override
        public final void add(final ImageStorage imageStorage) {
            final SequencedSet<ImageStorage> imageStorages = getImageStorages(imageStorage.getCategory());
            final int determinedMaxCacheSize = determineMaxCacheSize(imageStorage.getCategory());

            if (imageStorages.add(imageStorage)) {
                LOG.info("Added {}", imageStorage);

                while (determinedMaxCacheSize < imageStorages.size()) {
                    // remove last since comparator is sorting by DESC timestamp
                    // thus the oldest and thus smallest value is at the end
                    // and we keep the newest entries only
                    imageStorages.removeLast();
                }
            }
        }

        @Override
        public final Access getAccess(final String category) {
            return new Access() {
                private final SequencedSet<ImageStorage> imageStorages = getImageStorages(category);

                @Override
                public final int count() {
                    return imageStorages.size();
                }

                @Override
                public final List<ImageStorage> getImages(final int maxCount) {
                    return imageStorages.stream()
                            .limit(maxCount)
                            .collect(Collectors.toList()); // modifiable list
                }
            };
        }

        @Override
        public Set<String> getCategories() {
            return categorizedImageStorages.isEmpty()
                    ? Set.of(ImageStorage.DEFAULT_CATEGORY)
                    : Set.copyOf(categorizedImageStorages.keySet());
        }

        /**
         * Determines the max cache size for the cache of the given
         * {@code category}. By default every cache uses the same max size.
         *
         * @param category the category for the determination
         *
         * @return the determined max cache size for the category
         */
        protected int determineMaxCacheSize(final String category) {
            return maxCacheSize;
        }

        private final SequencedSet<ImageStorage> getImageStorages(final String category) {
            Objects.requireNonNull(category, "category must not be null");
            return categorizedImageStorages.computeIfAbsent(
                    category,
                    cat -> new ConcurrentSkipListSet<>(ImageStorage.BY_TIMESTAMP.reversed()));
        }
    }
}
