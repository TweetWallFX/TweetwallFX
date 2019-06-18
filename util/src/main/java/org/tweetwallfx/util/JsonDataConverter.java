/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 TweetWallFX
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
package org.tweetwallfx.util;

import java.io.InputStream;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts data from an input into a typesafe object.
 */
public class JsonDataConverter {

    private static final Logger LOG = LogManager.getLogger(JsonDataConverter.class);
    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig()
            .setProperty("jsonb.fail-on-unknown-properties", true)
    );

    private JsonDataConverter() {
        // prevent instantiation
    }

    /**
     * Converts the {@code object} parameter into a POJO of the type
     * {@code typeClass}.
     *
     * @param <T> the type to convert into
     *
     * @param object the object to convert from
     *
     * @param typeClass the class of the type to convert into
     *
     * @return the converted object
     */
    public static <T> T convertFromObject(final Object object, final Class<T> typeClass) {
        return convertFromString(convertToString(object), typeClass);
    }

    /**
     * Converts the data from the {@code inputStream} parameter into a POJO of
     * the type {@code typeClass}.
     *
     * @param <T> the type to convert into
     *
     * @param inputStream the InputStream to read from
     *
     * @param typeClass the class of the type to convert into
     *
     * @return the converted object
     */
    public static <T> T convertFromInputStream(final InputStream inputStream, final Class<T> typeClass) {
        return JSONB.fromJson(inputStream, typeClass);
    }

    /**
     * Converts the {@code jsonString} parameter into a POJO of the type
     * {@code typeClass}.
     *
     * @param <T> the type to convert into
     *
     * @param jsonString the String to convert from
     *
     * @param typeClass the class of the type to convert into
     *
     * @return the converted object
     */
    public static <T> T convertFromString(final String jsonString, final Class<T> typeClass) {
        try {
            return JSONB.fromJson(jsonString, typeClass);
        } catch (final JsonbException je) {
            LOG.error("Failed to convert to " + typeClass + ": " + jsonString, je);
            throw je;
        }
    }

    /**
     * Converts the {@code object} parameter into a JSON String.
     *
     * @param object the object to convert
     *
     * @return the converted String
     */
    public static String convertToString(final Object object) {
        return JSONB.toJson(object);
    }
}
