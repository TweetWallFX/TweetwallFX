/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
package org.tweetwallfx.tweet.api;

import java.util.function.Consumer;

public interface TweetStream {

    /**
     * Called when receiving new Tweet.
     *
     * @param tweetConsumer the Consumer to be notified.
     */
    void onTweet(Consumer<Tweet> tweetConsumer);

    /**
     * Start consuming public statuses that match one or more filter predicates.
     * At least one predicate parameter, follow, locations, or track must be
     * specified. Multiple parameters may be specified which allows most clients
     * to use a single connection to the Streaming API. Placing long parameters
     * in the URL may cause the request to be rejected for excessive URL length.
     * The default access level allows up to 200 track keywords, 400 follow
     * userids and 10 1-degree location boxes. Increased access levels allow
     * 80,000 follow userids ("shadow" role), 400,000 follow userids ("birddog"
     * role), 10,000 track keywords ("restricted track" role), 200,000 track
     * keywords ("partner track" role), and 200 10-degree location boxes
     * ("locRestricted" role). Increased track access levels also pass a higher
     * proportion of statuses before limiting the stream.
     *
     * @param filterQuery Filter query
     */
    void filter(TweetFilterQuery filterQuery);
}
