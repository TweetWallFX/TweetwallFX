/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 TweetWallFX
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
 */
public record CloudVisionSettings(
        Set<FeatureType> featureTypes) {

    public CloudVisionSettings(
            final Set<FeatureType> featureTypes) {
        this.featureTypes = nullable(featureTypes);
    }

    @Override
    public Set<FeatureType> featureTypes() {
        return Set.copyOf(featureTypes);
    }

    public enum FeatureType {

        CROP_HINTS,
        DOCUMENT_TEXT_DETECTION,
        FACE_DETECTION,
        IMAGE_PROPERTIES,
        LABEL_DETECTION,
        LANDMARK_DETECTION,
        LOGO_DETECTION,
        OBJECT_LOCALIZATION,
        SAFE_SEARCH_DETECTION,
        TEXT_DETECTION,
        WEB_DETECTION,
    }
}
