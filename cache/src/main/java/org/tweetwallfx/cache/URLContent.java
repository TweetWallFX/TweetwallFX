/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2023 TweetWallFX
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public record URLContent(
        byte[] data,
        String digest) implements Serializable {

    public static final URLContent NO_CONTENT = new URLContent(
            new byte[0],
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" // SHA-256
            // "d41d8cd98f00b204e9800998ecf8427e" // MD5
    );

    private static final Logger LOG = LoggerFactory.getLogger(URLContent.class);

    public URLContent(
            final byte[] data,
            final String digest) {
        this.data = Arrays.copyOf(data, data.length);
        this.digest = digest;
    }

    public static URLContent of(final InputStream in) throws IOException {
        LOG.debug("Loading content from: {}", in);
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        in.transferTo(bout);
        byte[] bytes = bout.toByteArray();
        String digest = null;
        try {
            digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            LOG.info("MD5: {}", digest);
        } catch (NoSuchAlgorithmException ex) {
            LOG.warn("Failed to create digest for {}", in, ex);
        }
        return new URLContent(bytes, digest);
    }

    public static URLContent of(final String urlString) throws IOException {
        try (InputStream in = new URL(urlString).openStream()) {
            return of(in);
        } catch (FileNotFoundException fne) {
            LOG.warn("No data found for {}", urlString, fne);
            return NO_CONTENT;
        }
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof URLContent other
                && Objects.equals(digest, other.digest)
                && Arrays.equals(data, other.data);
    }
}
