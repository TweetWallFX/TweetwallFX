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

import java.util.Collections;
import java.util.List;
import static org.tweetwall.util.ToString.*;

/**
 * A conference is described by an eventCode, a label, a list of Locale, a
 * localisation and a list of links to extra resources such as the list of
 * speakers and the list of schedules.
 */
public class Event extends ObjectWithLinksBase {

    /**
     * An eventCode
     */
    private String eventCode;

    /**
     * A label
     */
    private String label;

    /**
     * A localisation
     */
    private String localisation;

    /**
     * S list of Locale
     */
    private List<String> locale;

    /**
     * A list of dates the event is heldd
     */
    private List<String> days;

    private List<String> proposalTypesId;

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(final String eventCode) {
        this.eventCode = eventCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(final String localisation) {
        this.localisation = localisation;
    }

    public List<String> getLocale() {
        return null == locale
                ? Collections.emptyList()
                : Collections.unmodifiableList(locale);
    }

    public void setLocale(final List<String> locale) {
        this.locale = locale;
    }

    public List<String> getDays() {
        return null == days
                ? Collections.emptyList()
                : Collections.unmodifiableList(days);
    }

    public void setDays(final List<String> days) {
        this.days = days;
    }

    public List<String> getProposalTypesId() {
        return null == proposalTypesId
                ? Collections.emptyList()
                : Collections.unmodifiableList(proposalTypesId);
    }

    public void setProposalTypesId(final List<String> proposalTypesId) {
        this.proposalTypesId = proposalTypesId;
    }

    @Override
    public String toString() {
        return createToString(this, mapOf(
                mapEntry("eventCode", getEventCode()),
                mapEntry("label", getLabel()),
                mapEntry("localisation", getLocalisation()),
                mapEntry("locale", getLocale()),
                mapEntry("days", getDays()),
                mapEntry("proposalTypesId", getProposalTypesId())
        )) + " extends " + super.toString();
    }
}
