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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class CFPClientTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Test
    public void clientImplIsFound() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        Assert.assertNotNull(client);
        Assert.assertEquals(1, CFPClient.getClientStream().count());
    }

    @Test
    public void eventsAreRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        Assert.assertNotNull(client);

        final Events events = client.getEvents();
        System.out.println("events: " + events);
    }

    @Test
    public void eventIsRetrievable() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        Assert.assertNotNull(client);

        final Event event = client.getEvent();
        System.out.println("event: " + event);
    }
}
