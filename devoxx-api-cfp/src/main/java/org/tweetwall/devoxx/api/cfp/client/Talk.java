/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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
package org.tweetwall.devoxx.api.cfp.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

/**
 * Describes a talk.
 */
public class Talk extends ObjectWithLinksBase {

    /**
     * Unique ID for this talk, used everywhere, in our printed program, on the
     * web site, etc.
     */
    private String id;

    /**
     * Title of this talk
     */
    private String title;

    /**
     * The audience level
     */
    private String audienceLevel;

    /**
     * talk format (translated to French or English, depending on your
     * "Accept-language" Header)
     */
    private String talkType;

    /**
     * short description of this proposal, might use markdown syntax
     */
    private String summary;

    /**
     * HTML version, markdown transformed to HTML
     */
    private String summaryAsHtml;

    /**
     * "en" if the presentation is in English, "fr" if it's in French, etc.
     */
    private String lang;

    /**
     * The track label (translated to French or English, depending on your
     * "Accept-language" Header)
     */
    @JsonAlias({"track", "trackId"})
    private String track;

    /**
     * The proposal content tags, always 3 tags defined
     */
    private List<Tag> tags;

    /**
     * an array of links to each speaker, with its full name
     */
    private List<SpeakerReference> speakers;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAudienceLevel() {
        return audienceLevel;
    }

    public void setAudienceLevel(final String audienceLevel) {
        this.audienceLevel = audienceLevel;
    }

    public String getTalkType() {
        return talkType;
    }

    public void setTalkType(final String talkType) {
        this.talkType = talkType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getSummaryAsHtml() {
        return summaryAsHtml;
    }

    public void setSummaryAsHtml(final String summaryAsHtml) {
        this.summaryAsHtml = summaryAsHtml;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(final String lang) {
        this.lang = lang;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(final String track) {
        this.track = track;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(final List<Tag> tags) {
        this.tags = tags;
    }

    public List<SpeakerReference> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(final List<SpeakerReference> speakers) {
        this.speakers = speakers;
    }
    
    public boolean hasCompleteInformation() {
        return !getLinkStream(Link.Type.TALK).findAny().isPresent();
    }
    
    public Talk reload() {
        if (hasCompleteInformation()) {
            return this;
        } else {
            return getLinkStream(Link.Type.TALK).findAny().get().call(Talk.class);
        }
    }

    @Override
    public String toString() {
        return "Talk{"
                + "\n    id=" + id
                + "\n    title=" + title
                + "\n    audienceLevel=" + audienceLevel
                + "\n    talkType=" + talkType
                + "\n    summary=" + summary
                + "\n    summaryAsHtml=" + summaryAsHtml
                + "\n    lang=" + lang
                + "\n    track=" + track
                + "\n    tags=" + Helper.convertCollectionForToString(tags)
                + "\n    speakers=" + Helper.convertCollectionForToString(speakers)
                + "\n    (( hasCompleteInformation=" + hasCompleteInformation() + " ))"
                + "\n} extends " + super.toString();
    }
}
