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
import java.util.Optional;
import static org.tweetwall.devoxx.api.cfp.client.impl.RestCallHelper.*;
import static org.tweetwall.util.ToString.*;

/**
 * POJO for a Speaker with the following fields:
 *
 * <ul>
 * <li>uuid*: Unique String identifier</li>
 * <li>bio*: plain text with Markdown syntax, to descibe speaker's profile</li>
 * <li>bioAsHtml*: bio as HTML</li>
 * <li>firstName*: First name</li>
 * <li>lastName*: Family name</li>
 * <li>avatarURL: URL to a photo</li>
 * <li>company: Company name</li>
 * <li>blog: Company name</li>
 * <li>twitter: Twitter handle</li>
 * <li>lang: Company name</li>
 * <li>acceptedTalks: an array of links to each accepted talk presented by this
 * speaker</li>
 * </ul> {@literal *}: indicate a mandatory field, cannot be null or blank
 */
public class Speaker extends ObjectWithLinksBase {

    /**
     * Unique String identifier.
     * <b>MANDATORY</b>
     */
    private String uuid;

    /**
     * Plain text with Markdown syntax, to descibe speaker's profile
     * <b>MANDATORY</b>
     */
    private String bio;

    /**
     * Bio as HTML
     * <b>MANDATORY</b>
     */
    private String bioAsHtml;

    /**
     * First name
     * <b>MANDATORY</b>
     */
    private String firstName;

    /**
     * Family name
     * <b>MANDATORY</b>
     */
    private String lastName;

    /**
     * URL to a photo
     */
    private String avatarURL;

    /**
     * Company name
     */
    private String company;

    /**
     * URL to a BLOG
     */
    private String blog;

    /**
     * Twitter handle
     */
    private String twitter;

    private String lang;

    /**
     * array of links to each accepted talk presented by this speaker
     */
    private List<Talk> acceptedTalks;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(final String bio) {
        this.bio = bio;
    }

    public String getBioAsHtml() {
        return bioAsHtml;
    }

    public void setBioAsHtml(final String bioAsHtml) {
        this.bioAsHtml = bioAsHtml;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(final String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(final String blog) {
        this.blog = blog;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(final String twitter) {
        this.twitter = twitter;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(final String lang) {
        this.lang = lang;
    }

    public List<Talk> getAcceptedTalks() {
        return null == acceptedTalks
                ? Collections.emptyList()
                : Collections.unmodifiableList(acceptedTalks);
    }

    public void setAcceptedTalks(final List<Talk> acceptedTalks) {
        this.acceptedTalks = acceptedTalks;
    }

    public boolean hasCompleteInformation() {
        return !getLinkStream(Link.Type.SPEAKER).findAny().isPresent();
    }

    public Optional<Speaker> reload() {
        if (hasCompleteInformation()) {
            return Optional.of(this);
        } else {
            return readOptionalFrom(getLinkStream(Link.Type.SPEAKER).findAny().get().getHref(), Speaker.class);
        }
    }

    @Override
    public String toString() {
        return createToString(this, mapOf(
                mapEntry("uuid", getUuid()),
                mapEntry("bio", getBio()),
                mapEntry("bioAsHtml", getBioAsHtml()),
                mapEntry("firstName", getFirstName()),
                mapEntry("lastName", getLastName()),
                mapEntry("avatarURL", getAvatarURL()),
                mapEntry("company", getCompany()),
                mapEntry("blog", getBlog()),
                mapEntry("twitter", getTwitter()),
                mapEntry("lang", getLang()),
                mapEntry("acceptedTalks", getAcceptedTalks()),
                mapEntry("hasCompleteInformation", hasCompleteInformation())
        )) + " extends " + super.toString();
    }
}
