/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 TweetWallFX
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

import java.util.Set;
import static org.tweetwallfx.util.Nullable.nullable;

/**
 * POJO for reading Settings concerning the usage of Google APIs.
 *
 * @param featureTypes the feature types to be requested from the Cloud Vision
 * API
 */
public record CloudVisionSettings(
        Set<FeatureType> featureTypes) {

    /**
     * Creates a new instance, replacing a {@code null} value with an empty set.
     */
    public CloudVisionSettings {
        featureTypes = nullable(featureTypes);
    }

    /**
     * {@return the immutable set of requested feature types}
     */
    @Override
    public Set<FeatureType> featureTypes() {
        return Set.copyOf(featureTypes);
    }

    /**
     * Feature types that can be requested from the Google Cloud Vision API.
     */
    public enum FeatureType {

        /**
         * Suggests a crop region for the image.
         */
        CROP_HINTS,
        /**
         * Performs dense text detection and OCR on a document.
         */
        DOCUMENT_TEXT_DETECTION,
        /**
         * Detects faces within the image.
         */
        FACE_DETECTION,
        /**
         * Detects general image properties such as the dominant colors.
         */
        IMAGE_PROPERTIES,
        /**
         * Detects broad categories of content within the image.
         */
        LABEL_DETECTION,
        /**
         * Detects geographic landmarks within the image.
         */
        LANDMARK_DETECTION,
        /**
         * Detects brand logos within the image.
         */
        LOGO_DETECTION,
        /**
         * Detects and localizes objects within the image.
         */
        OBJECT_LOCALIZATION,
        /**
         * Detects explicit content using safe search.
         */
        SAFE_SEARCH_DETECTION,
        /**
         * Detects and performs OCR on text within the image.
         */
        TEXT_DETECTION,
        /**
         * Detects web references and entities for the image.
         */
        WEB_DETECTION,
    }
}
