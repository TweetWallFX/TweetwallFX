/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 TweetWallFX
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
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.xml.bind.DatatypeConverter;
import java.util.Arrays;

public record URLContent(
        byte[] data,
        String digest) implements Serializable {

    private static final Logger LOG = LogManager.getLogger();

    public URLContent(
            final byte[] data,
            final String digest) {
        this.data = Arrays.copyOf(data, data.length);
        this.digest = digest;
    }

    public static URLContent of(final InputStream in) throws IOException {
        LOG.debug("Loading content from: {}", in);
        InputStream in2 = in;
        try {
            in2 = new DigestInputStream(in, MessageDigest.getInstance("md5"));
        } catch (NoSuchAlgorithmException ex) {
            LOG.warn("Failed to create digest for {}", in, ex);
        }
        final byte[] d = readFully(in2);
        final String digest = in2 instanceof DigestInputStream dis
                ? DatatypeConverter.printHexBinary(dis.getMessageDigest().digest())
                : null;
        LOG.info("MD5: {}", digest);

        return new URLContent(d, digest);
    }

    public static URLContent of(final String urlString) throws IOException {
        return of(new URL(urlString).openStream());
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

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }
}
