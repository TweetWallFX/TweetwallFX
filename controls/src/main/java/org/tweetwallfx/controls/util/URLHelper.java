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
package org.tweetwallfx.controls.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Resolver of URLs.
 */
public class URLHelper {

    private static final Logger LOGGER = LogManager.getLogger(URLHelper.class);
    private static final int MAX_CACHE_SIZE = Integer.getInteger("org.tweetwall.urlResolver.cacheSize", 2048);
    private static final Map<String, String> UNSHORTENED_LINKS = new LinkedHashMap<>();
    private static final Pattern SLASH_SPLITTER = Pattern.compile("/");
    private static final Function<String, String> UNSHORT_LINK = wrapFunction(urlString -> {
        final URL url = new URL(urlString);

        URLConnection connection = url.openConnection();

        if (connection instanceof HttpURLConnection) {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int responseCode;

            try {
                responseCode = httpURLConnection.getResponseCode();
            } catch (final IOException ioe) {
                throw new URLResolvingException("Error resolving URL \"" + urlString + "\"", ioe);
            }

            if (IntStream.of(HttpURLConnection.HTTP_MOVED_PERM,
                    HttpURLConnection.HTTP_MOVED_TEMP,
                    HttpURLConnection.HTTP_SEE_OTHER,
                    HttpURLConnection.HTTP_NOT_MODIFIED).filter(i -> i == responseCode).findAny().isPresent()) {
                // redirected
                final String redirected = connection.getHeaderField("Location");

                if (null == redirected) {
                    return urlString;
                } else {
                    LOGGER.info("URL '" + urlString + "' is redirected to '" + redirected + "'");
                    return resolve(redirected);
                }
            }
        }

        return connection.getURL().toString();
    });

    private URLHelper() {
        // prevent instantiation
    }

    /**
     * It follows HTTP URL redirects and returns the last URL that is no longer
     * redirected. If no URL redirect takes place then the original URL is
     * returned.
     *
     * @param urlString The URL (in String form) to resolve
     *
     * @return the resolved URL
     */
    public static String resolve(final String urlString) {
        final String resolvedURL;

        try {
            resolvedURL = UNSHORTENED_LINKS.computeIfAbsent(urlString, UNSHORT_LINK);
        } catch (final URLResolvingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return encodeURL(urlString);
        }

        if (urlString.equals(resolvedURL)) {
            LOGGER.info("URL: '" + urlString + "' was not redirected");
        } else {
            LOGGER.info("URL: '" + urlString + "' was resolved as '" + resolvedURL + "'");
        }

        if (MAX_CACHE_SIZE > UNSHORTENED_LINKS.size()) {
            UNSHORTENED_LINKS.remove(UNSHORTENED_LINKS.entrySet().iterator().next().getKey());
        }

        return encodeURL(resolvedURL);
    }

    private static String encodeURL(final String urlString) {
        final String protocol = urlString.substring(0, urlString.indexOf("//") + 2);

        try {
            final String encodedURL = protocol + SLASH_SPLITTER.splitAsStream(urlString.substring(protocol.length()))
                    .map(wrapFunction(part -> URLEncoder.encode(part, "UTF-8")))
                    .collect(Collectors.joining("/"));

            LOGGER.info("encodedURL of '" + urlString + "' is '" + encodedURL + "'");
            return encodedURL;
        } catch (final RuntimeException ex) {
            LOGGER.warn("Encoding URL to UTF-8 failed. URL='" + urlString + "'", ex);
            return urlString;
        }
    }

    private static <T, R> Function<T, R> wrapFunction(final ExceptionalFunction<T, R> exceptionalFunction) {
        return t -> exceptionalFunction.apply(t);
    }

    private static interface ExceptionalFunction<T, R> extends Function<T, R> {

        @Override
        public default R apply(final T t) {
            try {
                return applyImpl(t);
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        public R applyImpl(final T t) throws Exception;
    }

    private static class URLResolvingException extends RuntimeException {

        private static final long serialVersionUID = 8732632342398562397L;

        public URLResolvingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
