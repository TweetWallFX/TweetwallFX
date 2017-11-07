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
import java.util.stream.Stream;
import static org.tweetwall.util.ToString.*;

/**
 * A POJO containing links to other POJOs.
 */
public abstract class ObjectWithLinksBase {

    /**
     * Links to the actual talk POJO and its speakers
     */
    private List<Link> links;

    public List<Link> getLinks() {
        return null == links
                ? Collections.emptyList()
                : Collections.unmodifiableList(links);
    }

    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    public Stream<Link> getLinkStream() {
        if (null == links) {
            return Stream.empty();
        } else {
            return links.stream();
        }
    }

    public Stream<Link> getLinkStream(final Link.Type linkType) {
        return getLinkStream().filter(link -> link.getType().equals(linkType));
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "links", getLinks()
        )) + " extends " + super.toString();
    }
}
