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
package org.tweetwallfx.google.vision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.LocationInfo;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.rpc.Status;
import java.io.Serializable;
import java.util.List;
import org.tweetwallfx.google.GoogleLikelihood;
import static org.tweetwallfx.util.Nullable.nullable;

/**
 * An analysis result performed by Google Cloud API.
 */
public record ImageContentAnalysis(
        AnalysisError analysisError,
        SafeSearch safeSearch,
        List<TextEntry> texts) implements Serializable {

    public ImageContentAnalysis(
            final AnalysisError analysisError,
            final SafeSearch safeSearch,
            final List<TextEntry> texts) {
        this.analysisError = analysisError;
        this.safeSearch = safeSearch;
        this.texts = nullable(texts);
    }

    public static ImageContentAnalysis of(final AnnotateImageResponse air) {
        return new ImageContentAnalysis(
                AnalysisError.of(air.getError()),
                SafeSearch.of(air.getSafeSearchAnnotation()),
                air.getTextAnnotationsList().stream().map(TextEntry::of).toList()
        );
    }

    @Override
    public List<TextEntry> texts() {
        return List.copyOf(texts);
    }

    public static record AnalysisError(
            String message,
            List<String> details) implements Serializable {

        public AnalysisError(
                final String message,
                final List<String> details) {
            this.message = message;
            this.details = nullable(details);
        }

        private static AnalysisError of(final Status error) {
            if (null == error) {
                return null;
            }

            return new AnalysisError(
                    error.getMessage(),
                    error.getDetailsList().stream().map(Object::toString).toList());
        }

        @Override
        public List<String> details() {
            return List.copyOf(details);
        }
    }

    public static record SafeSearch(
            GoogleLikelihood adult,
            GoogleLikelihood medical,
            GoogleLikelihood racy,
            GoogleLikelihood spoof,
            GoogleLikelihood violence) implements Serializable {

        private static SafeSearch of(final SafeSearchAnnotation ssa) {
            if (null == ssa) {
                return null;
            }

            return new SafeSearch(
                    GoogleLikelihood.valueOf(ssa.getAdult().name()),
                    GoogleLikelihood.valueOf(ssa.getMedical().name()),
                    GoogleLikelihood.valueOf(ssa.getRacy().name()),
                    GoogleLikelihood.valueOf(ssa.getSpoof().name()),
                    GoogleLikelihood.valueOf(ssa.getViolence().name())
            );
        }
    }

    public static record TextEntry(
            String description,
            float score,
            List<LocationEntry> locations,
            String locale) implements Serializable {

        public TextEntry(
                final String description,
                final float score,
                final List<LocationEntry> locations,
                final String locale) {
            this.description = description;
            this.score = score;
            this.locations = nullable(locations);
            this.locale = locale;
        }

        private static TextEntry of(final EntityAnnotation ea) {
            return new TextEntry(
                    ea.getDescription(),
                    ea.getScore(),
                    ea.getLocationsList().stream()
                            .map(LocationEntry::of)
                            .toList(),
                    ea.getLocale()
            );
        }

        @Override
        public List<LocationEntry> locations() {
            return List.copyOf(locations);
        }
    }

    public static record LocationEntry(
            double latitude,
            double longitude) implements Serializable {

        private static LocationEntry of(final LocationInfo li) {
            return new LocationEntry(
                    li.getLatLng().getLatitude(),
                    li.getLatLng().getLongitude()
            );
        }
    }
}
