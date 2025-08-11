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
package org.tweetwallfx.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;
import static org.tweetwallfx.util.ToString.mapEntry;
import static org.tweetwallfx.util.ToString.mapOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;

import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;

class ToStringTest {

    private static final String KEY1 = "one";
    private static final Object VALUE1 = 1L;
    private static final String KEY2 = "deux";
    private static final Object VALUE2 = 2d;
    private static final String KEY3 = "drei";
    private static final Object VALUE3 = 3;
    private static final String KEY4 = "cuatro";
    private static final Object VALUE4 = Short.valueOf("4");
    private static final String KEY5 = "quinque";
    private static final Object VALUE5 = "0x101";

    @Test
    void testMapKeyValue_2args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1)
        );
    }

    @Test
    void testMapKeyValue_4args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1,
                KEY2, VALUE2);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2)
        );
    }

    @Test
    void testMapKeyValue_6args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1,
                KEY2, VALUE2,
                KEY3, VALUE3);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2),
                MapEntry.entry(KEY3, VALUE3)
        );
    }

    @Test
    void testMapKeyValue_8args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1,
                KEY2, VALUE2,
                KEY3, VALUE3,
                KEY4, VALUE4);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2),
                MapEntry.entry(KEY3, VALUE3),
                MapEntry.entry(KEY4, VALUE4)
        );
    }

    @Test
    void testMapKeyValue_10args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1,
                KEY2, VALUE2,
                KEY3, VALUE3,
                KEY4, VALUE4,
                KEY5, VALUE5);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2),
                MapEntry.entry(KEY3, VALUE3),
                MapEntry.entry(KEY4, VALUE4),
                MapEntry.entry(KEY5, VALUE5)
        );
    }

    @Test
    void testMapEntry_Null() {
        final Map<String, Object> result = mapOf((Map.Entry<String, Object>[]) null);

        assertThat(result).isEmpty();
    }

    @Test
    void testMapEntry_0args() {
        final Map<String, Object> result = mapOf();

        assertThat(result).isEmpty();
    }

    @Test
    void testMapEntry_1arg() {
        final Map<String, Object> result = mapOf(
                mapEntry(KEY1, VALUE1));

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1)
        );
    }

    @Test
    void testMapEntry_2args() {
        final Map<String, Object> result = mapOf(
                mapEntry(KEY1, VALUE1),
                mapEntry(KEY2, VALUE2)
        );

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2)
        );
    }

    @Test
    void testToStringParamsMapNull() {
        final String ts = createToString(this, null);
        assertThat(ts).isEqualTo("ToStringTest");
    }

    @Test
    void testToStringParamsMapEmpty() {
        final String ts = createToString(this, Collections.emptyMap());
        assertThat(ts).isEqualTo("ToStringTest");
    }

    @Test
    void testToStringParamsMapSimpleEntries1() {
        final String string = createToString(this, Collections.singletonMap(
                KEY1, VALUE1
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    one: 1
                }""");
    }

    @Test
    void testToStringParamsMapSimpleEntries1WithNullSuper() {
        final String string = createToString(this, Collections.singletonMap(
                KEY1, VALUE1
        ), null);

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    one: 1
                }""");
    }

    @Test
    void testToStringParamsMapSimpleEntries2() {
        final String string = createToString(this, map(
                KEY1, VALUE1,
                KEY3, VALUE3
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    " + KEY1 + ": " + VALUE1 + ','
                + "\n    " + KEY3 + ": " + VALUE3
                + "\n}");
    }

    @Test
    void testToStringParamsMapSimpleEntries3() {
        final String string = createToString(this, map(
                KEY1, VALUE1,
                KEY3, VALUE3,
                KEY2, VALUE2
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    " + KEY1 + ": " + VALUE1 + ','
                + "\n    " + KEY3 + ": " + VALUE3 + ','
                + "\n    " + KEY2 + ": " + VALUE2
                + "\n}");
    }

    @Test
    void testToStringParamsMapObjectEntry() {
        final String string = createToString(this, map("flag", true,
                "object", new TSImpl(map("one", 1, "false", false))
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    object: TSImpl {
                        one: 1,
                        false: false
                    }
                }""");
    }

    @Test
    void testToStringParamsMapListEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "list", Arrays.asList("one", 2, false, "quatro")
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    list: [
                        one,
                        2,
                        false,
                        quatro
                    ]
                }""");
    }

    @Test
    void testToStringParamsMapSetEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "set", new TreeSet<>(Arrays.asList(
                        "one",
                        "quatro")
                )
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    set: [
                        one,
                        quatro
                    ]
                }""");
    }

    @Test
    void testToStringParamsMapArrayEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "array", new Object[]{
                    "one", 2,
                    false, "quatro"
                }
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    array: [
                        one,
                        2,
                        false,
                        quatro
                    ]
                }""");
    }

    @Test
    void testToStringParamsMapMapEntry() {
        final String string = createToString(this, map("flag", true,
                "map", map(
                        "one", 1,
                        "two", 2
                )
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    map: {
                        one: 1,
                        two: 2
                    }
                }""");
    }

    @Test
    void testToStringParamsMapMapEntrySingleLine() {
        final String string = createToString(this, map("flag", true,
                "map", map(
                        "one", 1,
                        "two", 2
                )
        ), "HelloWorld", true);

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {flag: true, map: {one: 1, two: 2}} extends HelloWorld");
    }

    @Test
    void testToStringParamsMapMapEntry2() {
        final String string = createToString(this, map("flag", true,
                "map", mapOf(
                        mapEntry("simple", 1),
                        mapEntry("map", map(
                                "uno", "one",
                                "due", "two"
                        )),
                        mapEntry("object", new TSImpl(
                                map(
                                        "eins", Double.parseDouble("0.1"),
                                        "zwei", Double.parseDouble("0.2"),
                                        "null", null
                                )
                        )),
                        mapEntry("emptyArray", new Object[0]),
                        mapEntry("emptyList", Collections.emptyList()),
                        mapEntry("emptySet", Collections.emptySet()),
                        mapEntry("emptyMap", Collections.emptyMap())
                )
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("""
                ToStringTest {
                    flag: true,
                    map: {
                        simple: 1,
                        map: {
                            uno: one,
                            due: two
                        },
                        object: TSImpl {
                            eins: 0.1,
                            zwei: 0.2,
                            null: null
                        },
                        emptyArray: [],
                        emptyList: [],
                        emptySet: [],
                        emptyMap: {}
                    }
                }""");
    }

    private static class TSImpl {

        private final Map<String, Object> params;

        private TSImpl(Map<String, Object> params) {
            this.params = params;
        }

        @Override
        public String toString() {
            return createToString(this, params);
        }
    }
}
