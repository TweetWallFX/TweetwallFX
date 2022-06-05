/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2022 TweetWallFX
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
package org.tweetwallfx.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AdditionalConfigurationTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                arguments(
                    true,
                    Map.of(),
                    Map.of()
                ),
                arguments(
                    true,
                    Map.of("mykey", "myValue"),
                    Map.of("mykey", "myValue")
                ),
                arguments(
                    true,
                    Map.of("configuration",
                    Map.of("additionalConfigurationURLs", List.of(
                        new File("src/test/resources/urlLoadedConfiguration1.json").toURI().toString()))),
                        Map.of("configuration",
                        Map.of("additionalConfigurationURLs", List.of(
                        new File("src/test/resources/urlLoadedConfiguration1.json").toURI().toString())),
                        "loadedConfiguration",
                        "urlLoadedConfiguration1.json")
                ),
                arguments(
                    false,
                    Map.of("configuration",
                    Map.of("additionalConfigurationURLs", List.of(
                        new File("src/test/resources/notThere.json").toURI().toString()))),
                    Map.of()
                )
        );
    }

    @ParameterizedTest(name = "{1} -({0})> {2}")
    @MethodSource("parameters")
    void checkAdditionals(boolean successful, Map<String, Object> inputMap, Map<String, Object> resultMap) {
        try {
            final Method m = Configuration.class.getDeclaredMethod("mergeWithAdditionalConfigurations", Map.class);
            m.setAccessible(true);
            assertThat(m.invoke(null, inputMap)).isEqualTo(resultMap);
            if (!successful) {
                throw new AssertionError("Additional configuration loading did not fail when it should have");
            }
        } catch (final ReflectiveOperationException | RuntimeException re) {
            if (successful) {
                throw new AssertionError("Additional configuration loading failed when it shouldn't have", re);
            }
        }
    }
}
