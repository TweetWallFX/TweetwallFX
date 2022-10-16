/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.conference.api;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

final class Util {

    private static final AtomicReference<ConferenceClient> CACHED_CLIENT = new AtomicReference<>(null);

    static ConferenceClient getClient() {
        ConferenceClient client = CACHED_CLIENT.get();

        if (null == client) {
            Iterator<ConferenceClient> clients = ServiceLoader.load(ConferenceClient.class).iterator();

            if (!clients.hasNext()) {
                throw new IllegalStateException("No ConferenceClient instances found!");
            }

            client = clients.next();

            if (clients.hasNext()) {
                throw new IllegalStateException("In addition to ConferenceClient " + client + " the ConferenceClient " + clients.next() + " was found!");
            }

            CACHED_CLIENT.set(client);
        }

        return client;
    }
}
