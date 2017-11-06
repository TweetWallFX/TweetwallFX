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
package org.tweetwall.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.data.MapEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import static org.tweetwall.util.ToString.*;

public class ToStringTest {

    @Rule
    public TestName testName = new TestName();
    private static final String KEY1 = "one";
    private static final Object VALUE1 = 1L;
    private static final String KEY2 = "two";
    private static final Object VALUE2 = 2d;
    private static final String KEY3 = "drei";
    private static final Object VALUE3 = 3;
    private static final String KEY4 = "octa";
    private static final Object VALUE4 = Short.valueOf("8");
    private static final String KEY5 = "vier";
    private static final Object VALUE5 = "quatro";
    private static final String KEY6 = "hexa";
    private static final Object VALUE6 = "Sextet";
    private static final String KEY7 = "hepta";
    private static final Object VALUE7 = "seven dwarfs";
    private static final String KEY8 = "octo";
    private static final Object VALUE8 = "2^3";
    private static final String KEY9 = "nine";
    private static final Object VALUE9 = "3^1";
    private static final String KEY10 = "deca";
    private static final Object VALUE10 = "1001";

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Test
    public void testMapKeyValue_2args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1)
        );
    }

    @Test
    public void testMapKeyValue_4args() {
        final Map<String, Object> result = map(
                KEY1, VALUE1,
                KEY2, VALUE2);

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1),
                MapEntry.entry(KEY2, VALUE2)
        );
    }

    @Test
    public void testMapKeyValue_6args() {
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
    public void testMapKeyValue_8args() {
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
    public void testMapKeyValue_10args() {
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
    public void testMapEntry_Null() {
        final Map<String, Object> result = mapOf((Map.Entry<String, Object>[]) null);

        assertThat(result).isEmpty();
    }

    @Test
    public void testMapEntry_0args() {
        final Map<String, Object> result = mapOf();

        assertThat(result).isEmpty();
    }

    @Test
    public void testMapEntry_1arg() {
        final Map<String, Object> result = mapOf(
                mapEntry(KEY1, VALUE1));

        assertThat(result).containsExactly(
                MapEntry.entry(KEY1, VALUE1)
        );
    }

    @Test
    public void testMapEntry_2args() {
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
    public void testToStringParamsMapNull() {
        final String ts = createToString(this, null);
        assertThat(ts).isEqualTo("ToStringTest");
    }

    @Test
    public void testToStringParamsMapEmpty() {
        final String ts = createToString(this, Collections.emptyMap());
        assertThat(ts).isEqualTo("ToStringTest");
    }

    @Test
    public void testToStringParamsMapSimpleEntries1() {
        final String string = createToString(this, Collections.singletonMap(
                KEY1, VALUE1
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    one: 1"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapSimpleEntries2() {
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
    public void testToStringParamsMapSimpleEntries3() {
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
    public void testToStringParamsMapObjectEntry() {
        final String string = createToString(this, map("flag", true,
                "object", new TSImpl(map("one", 1, "false", false))
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    object: TSImpl {"
                + "\n        one: 1,"
                + "\n        false: false"
                + "\n    }"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapListEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "list", Arrays.asList("one", 2, false, "quatro")
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    list: ["
                + "\n        one,"
                + "\n        2,"
                + "\n        false,"
                + "\n        quatro"
                + "\n    ]"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapSetEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "set", new TreeSet<>(Arrays.asList(
                        "one",
                        "quatro")
                )
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    set: ["
                + "\n        one,"
                + "\n        quatro"
                + "\n    ]"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapArrayEntry() {
        final String string = createToString(this, map(
                "flag", true,
                "array", new Object[]{
                    "one", 2,
                    false, "quatro"
                }
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    array: ["
                + "\n        one,"
                + "\n        2,"
                + "\n        false,"
                + "\n        quatro"
                + "\n    ]"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapMapEntry() {
        final String string = createToString(this, map("flag", true,
                "map", map(
                        "one", 1,
                        "two", 2
                )
        ));

        System.out.println(string);
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    map: {"
                + "\n        one: 1,"
                + "\n        two: 2"
                + "\n    }"
                + "\n}");
    }

    @Test
    public void testToStringParamsMapMapEntry2() {
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
        assertThat(string).isEqualTo("ToStringTest {"
                + "\n    flag: true,"
                + "\n    map: {"
                + "\n        simple: 1,"
                + "\n        map: {"
                + "\n            uno: one,"
                + "\n            due: two"
                + "\n        },"
                + "\n        object: TSImpl {"
                + "\n            eins: 0.1,"
                + "\n            zwei: 0.2,"
                + "\n            null: null"
                + "\n        },"
                + "\n        emptyArray: [],"
                + "\n        emptyList: [],"
                + "\n        emptySet: [],"
                + "\n        emptyMap: {}"
                + "\n    }"
                + "\n}");
    }

    private static class TSImpl {

        private final Map<String, Object> params;

        public TSImpl(Map<String, Object> params) {
            this.params = params;
        }

        @Override
        public String toString() {
            return createToString(this, params);
        }
    }
}
