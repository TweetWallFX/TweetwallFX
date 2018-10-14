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
package org.tweetwallfx.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class URLContent implements Externalizable {

    private static final Logger LOG = LogManager.getLogger(URLContent.class);
    private static final long serialVersionUID = 1L;
    private byte[] data;

    public URLContent(final InputStream in) throws IOException {
        LOG.debug("Loading content from: {}", in);
        data = readFully(in);
    }

    public URLContent(final String urlString) throws IOException {
        this(new URL(urlString).openStream());
    }

    public URLContent() {
        data = new byte[0];
    }

    private static byte[] readFully(final InputStream in) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];

        int read;
        while ((read = in.read(buffer)) > -1) {
            bout.write(buffer, 0, read);
        }

        return bout.toByteArray();
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final int size = in.readInt();
        data = new byte[size];
        final int read = in.read(data);

        if (read != size) {
            throw new IOException("Unexpected amount of data read. Expected " + size + ", got " + read);
        }
    }
}
