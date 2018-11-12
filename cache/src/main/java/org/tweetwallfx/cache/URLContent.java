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
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class URLContent implements Externalizable {

    private static final Logger LOG = LogManager.getLogger(URLContent.class);
    private static final long serialVersionUID = 1L;
    private static final byte[] NO_DATA = new byte[0];
    private byte[] data;
    private String digest;

    public URLContent(final InputStream in) throws IOException {
        LOG.debug("Loading content from: {}", in);
        InputStream in2 = in;
        try {
            in2 = new DigestInputStream(in, MessageDigest.getInstance("md5"));
        } catch (NoSuchAlgorithmException ex) {
            LOG.warn("Failed to create digest for {}", in, ex);
        }
        data = readFully(in2);
        digest = in2 instanceof DigestInputStream
                ? javax.xml.bind.DatatypeConverter.printHexBinary(((DigestInputStream) in2).getMessageDigest().digest())
                : null;
        LOG.info("MD5: {}", digest);
    }

    public URLContent(final String urlString) throws IOException {
        this(new URL(urlString).openStream());
    }

    public URLContent() {
        data = NO_DATA;
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

    public String getDigest() {
        return digest;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        // digest
        out.writeUTF(digest);
        // data
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        // digest
        digest = in.readUTF();
        // data
        final int size = in.readInt();
        data = new byte[size];
        in.readFully(data);
    }
}
