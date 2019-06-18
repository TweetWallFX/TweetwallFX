/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TweetWallFX
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

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.tweetwallfx.util.ToString;

@RunWith(value = Parameterized.class)
public class ConfigurationTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Parameterized.Parameter(0)
    public boolean successful;

    @Parameterized.Parameter(1)
    public Map<String, Object> previousMap;

    @Parameterized.Parameter(2)
    public Map<String, Object> newMap;

    @Parameterized.Parameter(3)
    public Map<String, Object> resultMap;

    @Parameterized.Parameters(name = "{1} + {2} -({0})> {3}")
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
            {
                true,
                null,
                Collections.emptyMap(),
                Collections.emptyMap()
            },
            {
                true,
                Collections.emptyMap(),
                ToString.map("key", "value"),
                ToString.map("key", "value")
            },
            {
                true,
                ToString.map("key", "value"),
                ToString.map("Boolean", true),
                ToString.map("key", "value", "Boolean", true)
            },
            {
                true,
                ToString.map("key", "value", "Boolean", false),
                ToString.map("Boolean", true),
                ToString.map("key", "value", "Boolean", true)
            },
            {
                true,
                ToString.map("key", "value", "Character", 'a'),
                ToString.map("Character", 'c'),
                ToString.map("key", "value", "Character", 'c')
            },
            {
                true,
                ToString.map("key", "value", "Double", Math.E),
                ToString.map("Double", Math.PI),
                ToString.map("key", "value", "Double", Math.PI)
            },
            {
                true,
                ToString.map("key", "value", "Float", 3.2f),
                ToString.map("Float", 3.7f),
                ToString.map("key", "value", "Float", 3.7f)
            },
            {
                true,
                ToString.map("key", "value", "Integer", 7),
                ToString.map("Integer", 42),
                ToString.map("key", "value", "Integer", 42)
            },
            {
                true,
                ToString.map("key", "value", "Long", -23L),
                ToString.map("Long", 314L),
                ToString.map("key", "value", "Long", 314L)
            },
            {
                true,
                ToString.map("key", "value", "key2", new Object()),
                ToString.map("key2", 42L),
                ToString.map("key", "value", "key2", 42L)
            },
            {
                true,
                ToString.map("key", "value", "Short", Short.valueOf("13")),
                ToString.map("Short", Short.valueOf("37")),
                ToString.map("key", "value", "Short", Short.valueOf("37"))
            },
            {
                true,
                ToString.map("key", "value", "String", "Foo"),
                ToString.map("String", "Baz"),
                ToString.map("key", "value", "String", "Baz")
            },
            {
                true,
                ToString.map("key", "value", "BigDecimal", BigDecimal.valueOf(9)),
                ToString.map("BigDecimal", BigDecimal.valueOf(73)),
                ToString.map("key", "value", "BigDecimal", BigDecimal.valueOf(73))
            },
            {
                true,
                ToString.map("key", "value", "BigInteger", BigInteger.valueOf(13)),
                ToString.map("BigInteger", BigInteger.valueOf(17)),
                ToString.map("key", "value", "BigInteger", BigInteger.valueOf(17))
            },
            {
                true,
                ToString.map("key", "value", "key2", ToString.map("a", 'a')),
                ToString.map("key2", ToString.map("b", 2)),
                ToString.map("key", "value", "key2", ToString.map("a", 'a', "b", 2)),
            },
            {
                false,
                ToString.map("key", "value", "key2", ToString.map("a", 'a')),
                ToString.map("key2", Collections.emptyList()),
                null
            },
            {
                true,
                ToString.map("key", "value", "key2", Collections.singletonList(13)),
                ToString.map("key2", Collections.singletonList(12)),
                ToString.map("key", "value", "key2", Collections.singletonList(12))
            },
            {
                true,
                ToString.map("key", "value", "key2", Collections.singletonList(13)),
                ToString.map("key2", Collections.singleton(12)),
                ToString.map("key", "value", "key2", Collections.singleton(12))
            },
            {
                true,
                ToString.map("key", "value", "key2", Color.CYAN),
                ToString.map("key2", Color.CYAN),
                ToString.map("key", "value", "key2", Color.CYAN)
            }
        });
    }

    @Test
    public void checkMerge() throws Exception {
        try {
            Assert.assertThat(
                    "Result of map merge failed!",
                    Configuration.mergeMap(previousMap, newMap),
                    CoreMatchers.equalTo(resultMap));
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
