/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 TweetWallFX
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
package org.tweetwallfx.conference.spi.util;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.tweetwallfx.util.Stopwatch;

public class RestCallHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestCallHelper.class);

    private RestCallHelper() {
        // prevent instantiation
    }

    private static Client getClient() {
        return ClientBuilder.newClient();
    }

    private static String getHttpsUrl(final String url) {
        Objects.requireNonNull(url, "url parameter must not be null!");
        if (url.startsWith("http:")) {
            return url.replaceAll("^http:", "https:");
        } else {
            return url;
        }
    }

    private static WebTarget createWebTarget(final String url) {
        LOGGER.info("Calling URL: {}", url);
        return getClient().target(getHttpsUrl(url));
    }

    private static WebTarget addQueryParameters(final WebTarget webTarget, final Map<String, Object> queryParameters) {
        if (null == queryParameters || queryParameters.isEmpty()) {
            return webTarget;
        }

        LOGGER.info("Adding query parameters to {}: {}", webTarget, queryParameters);
        WebTarget wt = webTarget;

        for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            wt = switch (value) {
                case Object[] array ->
                    wt.queryParam(key, array);
                case Collection<?> collection ->
                    wt.queryParam(key, collection.toArray());
                default ->
                    wt.queryParam(key, value);
            };
        }

        return wt;
    }

    private static Response getResponse(final String url, final Map<String, Object> queryParameters) {
        WebTarget webTarget = addQueryParameters(createWebTarget(url), queryParameters);

        final Response response = Stopwatch.measure(
                () -> webTarget.request(MediaType.APPLICATION_JSON).get(),
                duration -> LOGGER.info("URI call to {} took {}", webTarget.getUri(), duration));
        LOGGER.info("Received Response: {}", response);

        return response;
    }

    public static Optional<Response> getOptionalResponse(final String url, final Map<String, Object> queryParameters) {
        try {
            return Optional.ofNullable(getResponse(url, queryParameters));
        } catch (final ProcessingException pe) {
            LOGGER.error("Encountered ProcessingException while calling to '" + url + "'", pe);
            return Optional.empty();
        }
    }

    public static Optional<Response> getOptionalResponse(final String url) {
        return getOptionalResponse(url, Collections.emptyMap());
    }

    private static Response postRequest(final String url, final Map<String, Object> queryParameters, Entity<?> entity) {
        WebTarget webTarget = addQueryParameters(createWebTarget(url), queryParameters);

        final Response response = Stopwatch.measure(
                () -> webTarget.request(MediaType.APPLICATION_JSON).post(entity),
                duration -> LOGGER.info("URI call to {} took {}", webTarget.getUri(), duration));
        LOGGER.info("Received Response: {}", response);

        return response;
    }

    public static Optional<Response> postOptionalResponse(final String url, final Map<String, Object> queryParameters, final Entity<?> entity) {
        try {
            return Optional.ofNullable(postRequest(url, queryParameters, entity));
        } catch (final ProcessingException pe) {
            LOGGER.error("Encountered ProcessingException while calling to '" + url + "'", pe);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final Class<T> typeClass) {
        return readOptionalFrom(response, typeClass, null);
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final Class<T> typeClass, final BinaryOperator<T> docCombiner) {
        return readOptionalFrom0(response, r -> r.readEntity(typeClass), docCombiner);
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final GenericType<T> genericType) {
        return readOptionalFrom(response, genericType, null);
    }

    public static <T> Optional<T> readOptionalFrom(final Response response, final GenericType<T> genericType, final BinaryOperator<T> docCombiner) {
        return readOptionalFrom0(response, r -> r.readEntity(genericType), docCombiner);
    }

    private static <T> Optional<T> readOptionalFrom0(final Response response, final Function<Response, T> docReader, final BinaryOperator<T> docCombiner) {
        Optional<Response> okResponse = Optional.of(Objects.requireNonNull(response, "Parameter response must not be null!"))
                .filter(r -> Response.Status.OK.getStatusCode() == r.getStatus());
        Optional<Link> nextLink = okResponse.map(r -> r.getLink("next"));

        if (nextLink.isPresent()) {
            Objects.requireNonNull(docCombiner, "Parameter documentCombiner is required as the response is paginated");
            Stream<Response> okResponses = okResponse.stream();

            while (nextLink.isPresent()) {
                LOGGER.debug("Pagination next link: {}", nextLink);
                okResponse = nextLink
                        .map(Link::getUri)
                        .map(Object::toString)
                        .flatMap(RestCallHelper::getOptionalResponse)
                        .filter(r -> Response.Status.OK.getStatusCode() == r.getStatus());
                okResponses = Stream.concat(okResponses, okResponse.stream());
                nextLink = okResponse.map(r -> r.getLink("next"));
            }

            return okResponses
                    .map(docReader)
                    .collect(Collectors.reducing(docCombiner));
        } else {
            return okResponse.map(docReader);
        }
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Class<T> typeClass) {
        return readOptionalFrom(url, typeClass, null);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Class<T> typeClass, final BinaryOperator<T> docCombiner) {
        return readOptionalFrom(url, null, typeClass, docCombiner);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final Class<T> typeClass) {
        return readOptionalFrom(url, queryParameters, typeClass, null);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final Class<T> typeClass, final BinaryOperator<T> docCombiner) {
        return getOptionalResponse(url, queryParameters)
                .flatMap(response -> readOptionalFrom(response, typeClass, docCombiner));
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final GenericType<T> genericType) {
        return readOptionalFrom(url, genericType, null);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final GenericType<T> genericType, final BinaryOperator<T> docCombiner) {
        return readOptionalFrom(url, null, genericType, docCombiner);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final GenericType<T> genericType) {
        return readOptionalFrom(url, queryParameters, genericType, null);
    }

    public static <T> Optional<T> readOptionalFrom(final String url, final Map<String, Object> queryParameters, final GenericType<T> genericType, final BinaryOperator<T> docCombiner) {
        return getOptionalResponse(url, queryParameters)
                .flatMap(response -> readOptionalFrom(response, genericType, docCombiner));
    }
}
