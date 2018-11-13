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
package org.tweetwallfx.google.vision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.LocationInfo;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.rpc.Status;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.tweetwallfx.google.GoogleLikelihood;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * An analysis result performed by Google Cloud API.
 */
public final class ImageContentAnalysis implements Externalizable {

    private static final long serialVersionUID = 1L;
    private AnalysisError analysisError;
    private SafeSearch safeSearch;
    private List<TextEntry> texts;

    public ImageContentAnalysis() {
    }

    public ImageContentAnalysis(final AnnotateImageResponse air) {
        if (air.hasError()) {
            setAnalysisError(new AnalysisError(air.getError()));
        }

        setSafeSearch(SafeSearch.of(air.getSafeSearchAnnotation()));
        setTexts(air.getTextAnnotationsList().stream()
                .map(TextEntry::new)
                .collect(Collectors.toList()));
//        air.getTextAnnotationsList(); // TODO: add it

//        air.getCropHintsAnnotation(); // TODO: add it
//        air.getFaceAnnotationsList(); // TODO: add it
//        air.getFullTextAnnotation(); // TODO: add it
//        air.getImagePropertiesAnnotation(); // TODO: add it
//        air.getLabelAnnotationsList(); // TODO: add it
//        air.getLandmarkAnnotationsList(); // TODO: add it
//        air.getLocalizedObjectAnnotationsList(); // TODO: add it
//        air.getLogoAnnotationsList(); // TODO: add it
//        air.getWebDetection(); // TODO: add it
    }

    public AnalysisError getAnalysisError() {
        return analysisError;
    }

    public void setAnalysisError(final AnalysisError analysisError) {
        this.analysisError = analysisError;
    }

    public SafeSearch getSafeSearch() {
        return safeSearch;
    }

    public void setSafeSearch(final SafeSearch safeSearch) {
        this.safeSearch = safeSearch;
    }

    public List<TextEntry> getTexts() {
        return texts;
    }

    public void setTexts(final List<TextEntry> texts) {
        this.texts = texts;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "analysisError", getAnalysisError(),
                "safeSearch", getSafeSearch(),
                "texts", getTexts()
        )) + " extends " + super.toString();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        // analysisError
        out.writeObject(getAnalysisError());

        // safeSearch
        out.writeObject(getSafeSearch());

        // texts
        out.writeInt(getTexts().size());
        for (TextEntry text : getTexts()) {
            out.writeObject(text);
        }
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        // analysisError
        setAnalysisError((AnalysisError) in.readObject());

        // safeSearch
        setSafeSearch((SafeSearch) in.readObject());

        // texts
        final int size = in.readInt();
        setTexts(new ArrayList<>(size));
        for (int i = 0; i < size; i++) {
            getTexts().add((TextEntry) in.readObject());
        }
    }

    public static final class AnalysisError implements Externalizable {

        private static final long serialVersionUID = 1L;
        private String message;
        private List<String> details = Collections.emptyList();

        public AnalysisError() {
            // default constructor doing nothing
        }

        private AnalysisError(final Status error) {
            setMessage(error.getMessage());
            setDetails(error.getDetailsList().stream().map(Object::toString).collect(Collectors.toList()));
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(final List<String> details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "message", getMessage(),
                    "details", getDetails()
            )) + " extends " + super.toString();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            // message
            out.writeUTF(getMessage());

            // details
            out.writeInt(details.size());
            for (String detail : details) {
                out.writeUTF(detail);
            }
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            // message
            setMessage(in.readUTF());

            // details
            final int size = in.readInt();
            final List<String> dets = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                dets.add(in.readUTF());
            }
            setDetails(dets);
        }
    }

    public static final class SafeSearch implements Externalizable {

        private static final long serialVersionUID = 1L;
        private GoogleLikelihood adult;
        private GoogleLikelihood medical;
        private GoogleLikelihood racy;
        private GoogleLikelihood spoof;
        private GoogleLikelihood violence;

        public SafeSearch() {
            // default constructor doing nothing
        }

        private static SafeSearch of(final SafeSearchAnnotation ssa) {
            if (null == ssa) {
                return null;
            }

            final SafeSearch safeSearch = new SafeSearch();
            safeSearch.setAdult(GoogleLikelihood.valueOf(ssa.getAdult().name()));
            safeSearch.setMedical(GoogleLikelihood.valueOf(ssa.getMedical().name()));
            safeSearch.setRacy(GoogleLikelihood.valueOf(ssa.getRacy().name()));
            safeSearch.setSpoof(GoogleLikelihood.valueOf(ssa.getSpoof().name()));
            safeSearch.setViolence(GoogleLikelihood.valueOf(ssa.getViolence().name()));

            return safeSearch;
        }

        public GoogleLikelihood getAdult() {
            return adult;
        }

        public void setAdult(final GoogleLikelihood adult) {
            this.adult = adult;
        }

        public GoogleLikelihood getMedical() {
            return medical;
        }

        public void setMedical(final GoogleLikelihood medical) {
            this.medical = medical;
        }

        public GoogleLikelihood getRacy() {
            return racy;
        }

        public void setRacy(final GoogleLikelihood racy) {
            this.racy = racy;
        }

        public GoogleLikelihood getSpoof() {
            return spoof;
        }

        public void setSpoof(final GoogleLikelihood spoof) {
            this.spoof = spoof;
        }

        public GoogleLikelihood getViolence() {
            return violence;
        }

        public void setViolence(final GoogleLikelihood violence) {
            this.violence = violence;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "adult", getAdult(),
                    "medical", getMedical(),
                    "racy", getRacy(),
                    "spoof", getSpoof(),
                    "violence", getViolence()
            )) + " extends " + super.toString();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            // adult
            out.writeUTF(getAdult().name());

            // medical
            out.writeUTF(getMedical().name());

            // racy
            out.writeUTF(getRacy().name());

            // spoof
            out.writeUTF(getSpoof().name());

            // violence
            out.writeUTF(getViolence().name());
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            // adult
            setAdult(GoogleLikelihood.valueOf(in.readUTF()));

            // medical
            setMedical(GoogleLikelihood.valueOf(in.readUTF()));

            // racy
            setRacy(GoogleLikelihood.valueOf(in.readUTF()));

            // spoof
            setSpoof(GoogleLikelihood.valueOf(in.readUTF()));

            // violence
            setViolence(GoogleLikelihood.valueOf(in.readUTF()));
        }
    }

    public static final class TextEntry implements Externalizable {

        private static final long serialVersionUID = 1L;
        private String description;
        private float score;
        private List<LocationEntry> locations;
        private String locale;

        public TextEntry() {
            // default constructor doing nothing
        }

        public TextEntry(final EntityAnnotation ea) {
            this.description = ea.getDescription();
            this.locale = ea.getLocale();
            this.locations = ea.getLocationsList().stream()
                    .map(LocationEntry::new)
                    .collect(Collectors.toList());
            this.score = ea.getScore();
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }

        public float getScore() {
            return score;
        }

        public void setScore(final float score) {
            this.score = score;
        }

        public List<LocationEntry> getLocations() {
            return locations;
        }

        public void setLocations(final List<LocationEntry> locations) {
            this.locations = locations;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(final String locale) {
            this.locale = locale;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "description", getDescription(),
                    "score", getScore(),
                    "locale", getLocale(),
                    "locations", getLocations()
            )) + " extends " + super.toString();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            // description
            out.writeUTF(getDescription());

            // score
            out.writeFloat(getScore());

            // locale
            out.writeUTF(getLocale());

            // locations
            out.writeInt(getLocations().size());
            for (LocationEntry location : getLocations()) {
                out.writeObject(location);
            }
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            // description
            setDescription(in.readUTF());

            // score
            setScore(in.readFloat());

            // locale
            setLocale(in.readUTF());

            // locations
            int size = in.readInt();
            setLocations(new ArrayList<>(size));
            for (int i = 0; i < size; i++) {
                getLocations().add((LocationEntry) in.readObject());
            }
        }
    }

    public static final class LocationEntry implements Externalizable {

        private static final long serialVersionUID = 1L;
        private double latitude;
        private double longitude;

        public LocationEntry() {
            // default constructor doing nothing
        }

        public LocationEntry(final LocationInfo li) {
            this.latitude = li.getLatLng().getLatitude();
            this.longitude = li.getLatLng().getLongitude();
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(final double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(final double longitude) {
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "latitude", getLatitude(),
                    "longitude", getLongitude()
            )) + " extends " + super.toString();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            // latitude
            out.writeDouble(getLatitude());

            // longitude
            out.writeDouble(getLongitude());
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            // latitude
            setLatitude(in.readDouble());

            // longitude
            setLongitude(in.readDouble());
        }
    }
}
