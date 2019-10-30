/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 TweetWallFX
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
package org.tweetwallfx.devoxx.api.cfp.client;

import java.util.Objects;
import java.util.stream.Stream;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

/**
 * The "Link" profile object is a Devoxx BE object that defines a relationship
 * between 2 resources.
 */
public class Link {

    /**
     * Href is an absolute URL, that a client may follow to load this resource's
     * content.
     */
    private String href;

    /**
     * Rel is an absolute URL to a Profile definition for the target resource
     * designed by href.
     */
    private String rel;

    /**
     * Title is the link title.
     */
    private String title;

    /**
     * UUID of the link.
     */
    private String uuid;

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(final String rel) {
        this.rel = rel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Type getType() {
        return Type.getType(this);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                    "href", getHref(),
                    "rel", getRel(),
                    "title", getTitle(),
                    "type", getType(),
                    "uuid", getUuid()
            )) + " extends " + super.toString();
    }

    public enum Type {

        ALL_CONFERENCE("/api/profile/conferences"),
        All_SCHEDULE("/api/profile/schedules"),
        ALL_SPEAKERS("/api/profile/list-of-speakers"),
        ALL_TRACKS("/api/profile/track"),
        CONFERENCE("/api/profile/conference"),
        PROPOSAL_TYPES("/api/profile/proposalType"),
        SCHEDULE("/api/profile/schedule"),
        SPEAKER("/api/profile/speaker"),
        TALK("/api/profile/talk");

        private final String apiProfile;

        Type(final String refURL) {
            this.apiProfile = refURL;
        }

        public static Type getType(final Link link) {
            Objects.requireNonNull(link, "parameter link must not be null!");

            return Stream.of(values())
                    .filter(cfplt -> link.getRel().endsWith(cfplt.apiProfile))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("unable to determine Link Type for '" + link.getRel() + "'"));
        }
    }
}
