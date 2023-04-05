/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 TweetWallFX
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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class JsonDataConverterTest {

    @Test
    void convertFromInputStream() {
        var is = new ByteArrayInputStream("{\"one\":\"11\",\"tree\":false,\"two\":22}".getBytes(UTF_8));
        assertThat(JsonDataConverter.convertFromInputStream(is, TestData.class))
                .isEqualTo(new TestData("11", 22, false));
    }

    @Test
    void convertFromString() {
        assertThat(JsonDataConverter.convertFromString("{}", TestData.class))
                .isEqualTo(new TestData(null, null, null));
    }

    @Test
    void convertToString() {
       assertThat(JsonDataConverter.convertToString(new TestData("1", 2, true)))
               .isEqualTo("{\"one\":\"1\",\"tree\":true,\"two\":2}");
    }

    @Test
    void convertToBytes() {
        assertThat(JsonDataConverter.convertToBytes(new TestData("111", 222, false)))
                .isEqualTo("{\"one\":\"111\",\"tree\":false,\"two\":222}".getBytes(UTF_8));
    }

    public record TestData(String one, Integer two, Boolean tree) {
    }
}
