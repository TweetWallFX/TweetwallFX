/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 TweetWallFX
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
package org.tweetwallfx.google.vision;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Objects;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.tweetwallfx.google.GoogleLikelihood;
import org.tweetwallfx.google.vision.ImageContentAnalysis.AnalysisError;
import org.tweetwallfx.google.vision.ImageContentAnalysis.LocationEntry;
import org.tweetwallfx.google.vision.ImageContentAnalysis.SafeSearch;
import org.tweetwallfx.google.vision.ImageContentAnalysis.TextEntry;

/**
 */
public class ImageContentAnalysisTest {

    @Rule
    public final TestName testName = new TestName();

    @Before
    public void setUp() {
        System.out.println("######## " + testName.getMethodName() + " START");
    }

    @After
    public void tearDown() {
        System.out.println("######## " + testName.getMethodName() + " END");
    }

    @SuppressWarnings("unchecked")
    private static <T> T writeAndReadObject(final T original) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);

        try (final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed to write object: " + original, ex);
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        try (final ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read object", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to load class", ex);
        }
    }

    private void print(final Object object, final String name) {
        System.out.println(name + ".hashCode(): " + Objects.hashCode(object));
        System.out.println(name + ".identityHashCode: " + System.identityHashCode(object));

        if (object.getClass().isRecord()) {
            for (RecordComponent recordComponent : object.getClass().getRecordComponents()) {
                try {
                    System.out.println("name.field." + recordComponent.getName() + ": " + recordComponent.getAccessor().invoke(object));
                } catch (ReflectiveOperationException ex) {
                    throw new IllegalStateException("Failed to read record component value for " + recordComponent.getName());
                }
            }
        }

        System.out.println(name + ": " + object);
    }

    private void testSerialization(final Object original) {
        print(original, "original");
        final Object copy = writeAndReadObject(original);
        print(copy, "copy");
        MatcherAssert.assertThat(original, CoreMatchers.equalTo(copy));
    }

    @Test
    public void testSerializationAnalysisError() {
        testSerialization(new AnalysisError(
                "myMessage",
                List.of("alpha", "beta", "gamma")
        ));
    }

    @Test
    public void testSerializationImageContentAnalysis() {
        testSerialization(new ImageContentAnalysis(
                new AnalysisError(
                        "Yikes",
                        List.of("Nothing", "seems", "to", "work")
                ),
                new SafeSearch(
                        GoogleLikelihood.LIKELY,
                        GoogleLikelihood.UNLIKELY,
                        GoogleLikelihood.LIKELY,
                        GoogleLikelihood.LIKELY,
                        GoogleLikelihood.VERY_UNLIKELY
                ),
                List.of(
                        new TextEntry(
                                "desc1",
                                0.34f,
                                List.of(new LocationEntry(12.5d, -42d)),
                                "unknown"
                        )
                )
        ));
    }

    @Test
    public void testSerializationLocationEntry() {
        testSerialization(new LocationEntry(23d, -17d));
    }

    @Test
    public void testSerializationSafeSearch() {
        testSerialization(new SafeSearch(
                GoogleLikelihood.LIKELY,
                GoogleLikelihood.POSSIBLE,
                GoogleLikelihood.UNKNOWN,
                GoogleLikelihood.UNLIKELY,
                GoogleLikelihood.VERY_UNLIKELY));
    }

    @Test
    public void testSerializationTextEntry() {
        testSerialization(new TextEntry(
                "my text description",
                0.123f,
                List.of(new LocationEntry(12.3d, 23.95d)),
                "dummyLoc"));
    }
}
