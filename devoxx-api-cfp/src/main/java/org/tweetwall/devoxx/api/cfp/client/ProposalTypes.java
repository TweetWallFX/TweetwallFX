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
 * Show the list of ProposalTypes.
 */
public class ProposalTypes {

    /**
     * Description of content.
     */
    private String content;

    /**
     * All rooms.
     */
    private List<ProposalType> proposalTypes;

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public List<ProposalType> getProposalTypes() {
        return null == proposalTypes
                ? Collections.emptyList()
                : Collections.unmodifiableList(proposalTypes);
    }

    public void setProposalTypes(final List<ProposalType> proposalTypes) {
        this.proposalTypes = proposalTypes;
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "content", getContent(),
                "proposalTypes", getProposalTypes()
        )) + " extends " + super.toString();
    }
}
