/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 TweetWallFX
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
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Value class holding the content retrieved from a URL together with its
 * digest.
 */
public final class URLContent implements Serializable {

    private static final long serialVersionUID = 0L;
    private static final Logger LOG = LoggerFactory.getLogger(URLContent.class);

    /**
     * Represents an empty URL content value.
     */
    public static final URLContent NO_CONTENT = new URLContent(
            "/dev/null",
            new byte[0],
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" // SHA-256
            // "d41d8cd98f00b204e9800998ecf8427e" // MD5
    );

    /**
     * The URL string this content was retrieved from.
     */
    private final String urlString;
    /**
     * The raw content bytes.
     */
    private final byte[] data;
    /**
     * The digest of the content.
     */
    private final String digest;

    /**
     * Creates a new instance.
     *
     * @param urlString the URL string this content is retrieved from
     * @param data the raw content bytes
     * @param digest the digest of the content
     */
    public URLContent(final String urlString, final byte[] data, final String digest) {
        this.urlString = Objects.requireNonNull(urlString, "urlString must not be null");
        Objects.requireNonNull(data, "data must not be null");
        this.data = Arrays.copyOf(data, data.length);
        this.digest = Objects.requireNonNull(digest, "digest must not be null");
    }

    /**
     * Creates a new instance by consuming the given {@code InputStream}.
     *
     * @param urlString the URL string the content is retrieved from
     * @param in the stream to read the content from
     * @return the created URLContent
     * @throws IOException in case reading the stream fails
     */
    public static URLContent of(final String urlString, final InputStream in) throws IOException {
        LOG.debug("Loading content from: {}", in);
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        in.transferTo(bout);
        byte[] bytes = bout.toByteArray();
        String digest = "0000000000000000000000000000000000000000000000000000000000000000";
        try {
            digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            LOG.info("SHA-256: {}", digest);
        } catch (NoSuchAlgorithmException ex) {
            LOG.warn("Failed to create digest for {}", in, ex);
        }
        return new URLContent(urlString, bytes, digest);
    }

    /**
     * Creates a new instance by loading the content of the given URL string.
     *
     * @param urlString the URL string to load the content from
     * @return the created URLContent or {@link #NO_CONTENT} when no data was
     * found
     * @throws IOException in case loading the content fails
     */
    public static URLContent of(final String urlString) throws IOException {
        try (InputStream in = URI.create(urlString).toURL().openStream()) {
            return of(urlString, in);
        } catch (FileNotFoundException fne) {
            LOG.warn("No data found for {}", urlString, fne);
            return NO_CONTENT;
        }
    }

    /**
     * {@return the number of content bytes}
     */
    public int dataSize() {
        return data.length;
    }

    /**
     * {@return the digest of the content}
     */
    public String digest() {
        return digest;
    }

    /**
     * {@return the URL string this content was retrieved from}
     */
    public String urlString() {
        return urlString;
    }

    /**
     * {@return an InputStream over the content bytes}
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digest, Arrays.hashCode(data));
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof URLContent other
                && Objects.equals(digest, other.digest)
                && Arrays.equals(data, other.data);
    }
}
