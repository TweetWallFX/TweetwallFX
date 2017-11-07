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
package org.tweetwall.devoxx.api.cfp.client.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestCallHelper {

    private static final Logger LOGGER = LogManager.getLogger(RestCallHelper.class);

    private RestCallHelper() {
        // prevent instantiation
    }

    private static Client getClient() {
        return ClientBuilder.newClient();
    }

    private static String getHttpsUrl(final String url) {
        if (url.startsWith("http:")) {
            return url.replaceAll("^http:", "https:");
        } else {
            return url;
        }
    }

    private static Response getResponse(final String url, final Map<String, Object> queryParameters) {
        LOGGER.info("Calling URL: " + url + " with query parameters: " + queryParameters);
        WebTarget webTarget = getClient().target(getHttpsUrl(url));

        if (null != queryParameters && !queryParameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();

                if (value instanceof Object[]) {
                    webTarget = webTarget.queryParam(key, Object[].class.cast(value));
                } else if (value instanceof Collection) {
                    webTarget = webTarget.queryParam(key, ((Collection<?>) value).toArray());
                } else {
                    webTarget = webTarget.queryParam(key, value);
                }
            }
        }

        return webTarget
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

    public static Optional<Response> getOptionalResponse(final String url, final Map<String, Object> queryParameters) {
        try {
            return Optional.of(getResponse(url, queryParameters));
        } catch (final ProcessingException pe) {
            LOGGER.error("Encountered ProcessingException while calling to '" + url + "'", pe);
            return Optional.empty();
        }
    }

    public static Optional<Response> getOptionalResponse(final String url) {
        return getOptionalResponse(url, Collections.emptyMap());
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final Class<T> typeClass) {
        return Optional.of(Objects.requireNonNull(response, "Parameter response must not be null!"))
                .filter(r -> Response.Status.OK.getStatusCode() == r.getStatus())
                .map(r -> r.readEntity(typeClass));
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final GenericType<T> genericType) {
        return Optional.of(Objects.requireNonNull(response, "Parameter response must not be null!"))
                .filter(r -> Response.Status.OK.getStatusCode() == r.getStatus())
                .map(r -> r.readEntity(genericType));
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Class<T> typeClass) {
        return readOptionalFrom(url, null, typeClass);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final Class<T> typeClass) {
        return getOptionalResponse(url, queryParameters)
                .flatMap(response -> readOptionalFrom(response, typeClass));
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final GenericType<T> genericType) {
        return readOptionalFrom(url, null, genericType);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final GenericType<T> genericType) {
        return getOptionalResponse(url, queryParameters)
                .flatMap(response -> readOptionalFrom(response, genericType));
    }
}
