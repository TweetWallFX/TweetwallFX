/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
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

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class StringNumberComparatorTest {

    @Rule
    public TestName testName = new TestName();

    @Parameterized.Parameter(0)
    public int result;

    @Parameterized.Parameter(1)
    public String string1;

    @Parameterized.Parameter(2)
    public String string2;

    @Parameterized.Parameters(name = "{0} == compare({1}, {2})")
    public static List<Object[]> parameters() {
        return Arrays.asList(
                new Object[]{
                    0,
                    "A",
                    "A"
                },
                new Object[]{
                    0,
                    "Alpha",
                    "Alpha"
                },
                new Object[]{
                    -1,
                    "Alpha 0",
                    "Alpha 00"
                },
                new Object[]{
                    -1,
                    "Alpha 0",
                    "Alpha 0 "
                },
                new Object[]{
                    1,
                    "Beta 20",
                    "Beta 2"
                },
                new Object[]{
                    1,
                    "Beta 00020",
                    "Beta 2"
                }
        );
    }

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    @Test
    public void checkTestCase() throws Exception {
        assertEquals(result, StringNumberComparator.INSTANCE.compare(string1, string2));
    }
}
