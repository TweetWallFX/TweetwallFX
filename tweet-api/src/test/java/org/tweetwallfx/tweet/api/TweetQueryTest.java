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
package org.tweetwallfx.tweet.api;

import static org.junit.Assert.*;
import org.junit.Test;

public class TweetQueryTest {

    @Test
    public void queryIsNullByDefault() {
        assertNull(null, new TweetQuery()
                .getQuery());
    }

    @Test
    public void queryIsNullWhenProducedByNullParameterOnly() {
        final String q = null;
        assertNull(new TweetQuery()
                .query(q)
                .getQuery());
    }

    @Test
    public void queryIsNullWhenProducedByNullParametersOnly() {
        final String q = null;
        assertNull(new TweetQuery()
                .query(q, q, q)
                .getQuery());
    }

    @Test
    public void queryWorksWhenProducedByOneValidString() {
        final String q = "@Devoxx";
        assertEquals(q, new TweetQuery()
                .query(q)
                .getQuery());
    }

    @Test
    public void queryWorksWhenProducedByOneValidAndSomeInvalidString() {
        final String q = "@Devoxx";
        assertEquals(q, new TweetQuery()
                .query(null, q, null)
                .getQuery());
    }

    @Test
    public void queryWorksWhenProducedByTwoValidString() {
        assertEquals("@Devoxx @Java", new TweetQuery()
                .query("@Devoxx", "@Java")
                .getQuery());
    }

    @Test
    public void queryFromWithNull() {
        assertNull(TweetQuery.queryFrom(null));
    }

    @Test
    public void queryFromWithValidStream() {
        assertEquals("from:@Devoxx", TweetQuery.queryFrom("@Devoxx"));
    }

    @Test
    public void queryToWithNull() {
        assertNull(TweetQuery.queryTo(null));
    }

    @Test
    public void queryToWithValidStream() {
        assertEquals("to:@Devoxx", TweetQuery.queryTo("@Devoxx"));
    }

    @Test
    public void queryFilterLinks() {
        assertEquals(TweetQuery.QUERY_FILTER_LINKS, "Filter:Links");
    }

    @Test
    public void queryFilterMedia() {
        assertEquals(TweetQuery.QUERY_FILTER_MEDIA, "Filter:media");
    }

    @Test
    public void queryFilterOrWithNullParam() {
        assertNull(TweetQuery.queryOR((String) null));
    }

    @Test
    public void queryFilterOrWithNullParams() {
        assertNull(TweetQuery.queryOR(null, null, null));
    }

    @Test
    public void queryFilterOrWithOneValidString() {
        assertEquals("@Devoxx", TweetQuery.queryOR("@Devoxx"));
    }

    @Test
    public void queryFilterOrWithOneValidStringAndNulls() {
        assertEquals("@Devoxx", TweetQuery.queryOR(null, "@Devoxx", null));
    }

    @Test
    public void queryFilterOrWithTwoValidStringAndNulls() {
        assertEquals("( @Devoxx OR @Java )", TweetQuery.queryOR(null, "@Devoxx", "@Java", null));
    }
}
