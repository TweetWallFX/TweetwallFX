/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 TweetWallFX
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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tweetwallfx.cache.URLContent.NO_CONTENT;

class URLContentTest {
    static final String SERIALIZED_DATA = """
            rO0ABXNyACBvcmcudHdlZXR3YWxsZnguY2FjaGUuVVJMQ29udGVudAAAAAAAAAAAAgADWwAEZGF0YXQAAl\
            tCTAAGZGlnZXN0dAASTGphdmEvbGFuZy9TdHJpbmc7TAAJdXJsU3RyaW5ncQB+AAJ4cHVyAAJbQqzzF/gG\
            CFTgAgAAeHAAAAAWdGhpcyBpcyBzb21lIGRlbW8gZGF0YXQAQGYyODJiOTczMzdiZDY3ZWFmMjJlODE2ZD\
            g5MjM5ZTMxNDkxMGI1YmUwMDE5ZTNmZTRjNDE1ODk2MzQ2ODcyN2V0AAA=""";
    static final String DIGEST = "f282b97337bd67eaf22e816d89239e314910b5be0019e3fe4c4158963468727e";
    public static final byte[] TEST_DATA = "this is some demo data".getBytes(StandardCharsets.UTF_8);

    @Test
    void equals() throws IOException {
        assertThat(NO_CONTENT)
                .isEqualTo(new URLContent("", new byte[0], NO_CONTENT.digest()))
                .isEqualTo(URLContent.of("", InputStream.nullInputStream()));
    }

    @Test
    void serialization() throws IOException {
        URLContent content = URLContent.of("", new ByteArrayInputStream(TEST_DATA));
        ByteArrayOutputStream out= new ByteArrayOutputStream();
        try (ObjectOutputStream objOout= new ObjectOutputStream(out)){
            objOout.writeObject(content);
        }
        assertThat(content.digest()).isEqualTo(DIGEST);
        assertThat(content.getInputStream()).hasBinaryContent(TEST_DATA);
        assertThat(Base64.getEncoder().encodeToString(out.toByteArray())).isEqualTo(SERIALIZED_DATA);
    }

    @Test
    void deserialization() throws IOException, ReflectiveOperationException {
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(SERIALIZED_DATA)))) {
            URLContent content = (URLContent)objIn.readObject();

            assertThat(content.digest()).isEqualTo(DIGEST);
            assertThat(content.getInputStream()).hasBinaryContent(TEST_DATA);
        }
    }
}
