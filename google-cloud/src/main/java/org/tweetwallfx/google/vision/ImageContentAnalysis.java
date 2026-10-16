/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 TweetWallFX
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
import org.tweetwallfx.google.GoogleLikelihood;

import java.io.Serializable;
import java.util.List;

import static org.tweetwallfx.util.Nullable.nullable;

/**
 * An analysis result performed by Google Cloud API.
 *
 * @param analysisError the error that occurred during the analysis, may be
 * {@code null}
 * @param safeSearch the safe search result, may be {@code null}
 * @param texts the text entries detected in the image
 */
public record ImageContentAnalysis(
        AnalysisError analysisError,
        SafeSearch safeSearch,
        List<TextEntry> texts) implements Serializable {

    /**
     * Creates a new instance, replacing a {@code null} value with an empty list.
     */
    public ImageContentAnalysis {
        texts = nullable(texts);
    }

    /**
     * {@return an analysis created from the given annotate image response}
     *
     * @param air the annotate image response to convert
     */
    public static ImageContentAnalysis of(final AnnotateImageResponse air) {
        return new ImageContentAnalysis(
                AnalysisError.of(air.getError()),
                SafeSearch.of(air.getSafeSearchAnnotation()),
                air.getTextAnnotationsList().stream().map(TextEntry::of).toList()
        );
    }

    /**
     * {@return the immutable list of text entries}
     */
    @Override
    public List<TextEntry> texts() {
        return List.copyOf(texts);
    }

    /**
     * An error that occurred during the analysis of an image.
     *
     * @param message the error message
     * @param details the details of the error
     */
    public static record AnalysisError(
            String message,
            List<String> details) implements Serializable {

        /**
         * Creates a new instance, replacing a {@code null} value with an empty
         * list.
         */
        public AnalysisError {
            details = nullable(details);
        }

        private static AnalysisError of(final Status error) {
            if (null == error || error.getMessage().isBlank()) {
                return null;
            }

            return new AnalysisError(
                    error.getMessage(),
                    error.getDetailsList().stream().map(Object::toString).toList());
        }

        /**
         * {@return the immutable list of detail messages}
         */
        @Override
        public List<String> details() {
            return List.copyOf(details);
        }
    }

    /**
     * Safe search likelihoods of the categories evaluated by the Google Cloud
     * Vision API.
     *
     * @param adult likelihood that the image contains adult content
     * @param medical likelihood that the image contains medical content
     * @param racy likelihood that the image contains racy content
     * @param spoof likelihood that the image contains spoofed content
     * @param violence likelihood that the image contains violent content
     */
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

    /**
     * A text annotation detected in an image.
     *
     * @param description the detected text
     * @param score the confidence score of the detection
     * @param locations the locations of the detected text
     * @param locale the locale of the detected text
     */
    public static record TextEntry(
            String description,
            float score,
            List<LocationEntry> locations,
            String locale) implements Serializable {

        /**
         * Creates a new instance, replacing a {@code null} value with an empty
         * list.
         */
        public TextEntry {
            locations = nullable(locations);
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

        /**
         * {@return the immutable list of locations}
         */
        @Override
        public List<LocationEntry> locations() {
            return List.copyOf(locations);
        }
    }

    /**
     * A geographic location of a detected text.
     *
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
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
