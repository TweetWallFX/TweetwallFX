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
import org.tweetwallfx.util.ToString;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ConfigurationTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                arguments(
                    true,
                    null,
                    Collections.emptyMap(),
                    Collections.emptyMap()
                ),
                arguments(
                    true,
                    Collections.emptyMap(),
                    ToString.map("key", "value"),
                    ToString.map("key", "value")
                ),
                arguments(
                    true,
                    ToString.map("key", "value"),
                    ToString.map("Boolean", true),
                    ToString.map("key", "value", "Boolean", true)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Boolean", false),
                    ToString.map("Boolean", true),
                    ToString.map("key", "value", "Boolean", true)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Character", 'a'),
                    ToString.map("Character", 'c'),
                    ToString.map("key", "value", "Character", 'c')
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Double", Math.E),
                    ToString.map("Double", Math.PI),
                    ToString.map("key", "value", "Double", Math.PI)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Float", 3.2f),
                    ToString.map("Float", 3.7f),
                    ToString.map("key", "value", "Float", 3.7f)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Integer", 7),
                    ToString.map("Integer", 42),
                    ToString.map("key", "value", "Integer", 42)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Long", -23L),
                    ToString.map("Long", 314L),
                    ToString.map("key", "value", "Long", 314L)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "key2", new Object()),
                    ToString.map("key2", 42L),
                    ToString.map("key", "value", "key2", 42L)
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "Short", Short.valueOf("13")),
                    ToString.map("Short", Short.valueOf("37")),
                    ToString.map("key", "value", "Short", Short.valueOf("37"))
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "String", "Foo"),
                    ToString.map("String", "Baz"),
                    ToString.map("key", "value", "String", "Baz")
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "BigDecimal", BigDecimal.valueOf(9)),
                    ToString.map("BigDecimal", BigDecimal.valueOf(73)),
                    ToString.map("key", "value", "BigDecimal", BigDecimal.valueOf(73))
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "BigInteger", BigInteger.valueOf(13)),
                    ToString.map("BigInteger", BigInteger.valueOf(17)),
                    ToString.map("key", "value", "BigInteger", BigInteger.valueOf(17))
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "key2", ToString.map("a", 'a')),
                    ToString.map("key2", ToString.map("b", 2)),
                    ToString.map("key", "value", "key2", ToString.map("a", 'a', "b", 2))
                ),
                arguments(
                    false,
                    ToString.map("key", "value", "key2", ToString.map("a", 'a')),
                    ToString.map("key2", Collections.emptyList()),
                    null
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "key2", Collections.singletonList(13)),
                    ToString.map("key2", Collections.singletonList(12)),
                    ToString.map("key", "value", "key2", Collections.singletonList(12))
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "key2", Collections.singletonList(13)),
                    ToString.map("key2", Collections.singleton(12)),
                    ToString.map("key", "value", "key2", Collections.singleton(12))
                ),
                arguments(
                    true,
                    ToString.map("key", "value", "key2", Color.CYAN),
                    ToString.map("key2", Color.CYAN),
                    ToString.map("key", "value", "key2", Color.CYAN)
                )
        );
    }

    @ParameterizedTest(name = "{1} + {2} -({0})> {3}")
    @MethodSource("parameters")
    void checkMerge(boolean successful,
                    Map<String, Object> previousMap,
                    Map<String, Object> newMap,
                    Map<String, Object> resultMap) {
        try {
            assertThat(Configuration.mergeMap(previousMap, newMap))
                    .withFailMessage("Result of map merge failed!")
                    .isEqualTo(resultMap);
            if (!successful) {
                throw new AssertionError("Merging did not fail when it should have");
            }
        } catch (final RuntimeException re) {
            if (successful) {
                throw new AssertionError("Merging failed when it shouldn't have", re);
            }
        }
    }
}
