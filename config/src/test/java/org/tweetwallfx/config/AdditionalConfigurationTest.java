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

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
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

@RunWith(value = Parameterized.class)
public class AdditionalConfigurationTest {

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
    public Map<String, Object> inputMap;

    @Parameterized.Parameter(2)
    public Map<String, Object> resultMap;

    @Parameterized.Parameters(name = "{1} -({0})> {2}")
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
            {
                true,
                Map.of(),
                Map.of()
            },
            {
                true,
                Map.of("mykey", "myValue"),
                Map.of("mykey", "myValue")
            },
            {
                true,
                Map.of("configuration",
                Map.of("additionalConfigurationURLs", List.of(
                new File("src/test/resources/urlLoadedConfiguration1.json").toURI().toString()))),
                Map.of("configuration",
                Map.of("additionalConfigurationURLs", List.of(
                new File("src/test/resources/urlLoadedConfiguration1.json").toURI().toString())),
                "loadedConfiguration",
                "urlLoadedConfiguration1.json")
            },
            {
                false,
                Map.of("configuration",
                Map.of("additionalConfigurationURLs", List.of(
                new File("src/test/resources/notThere.json").toURI().toString()))),
                Map.of()
            }
        });
    }

    @Test
    public void checkAdditionals() {
        try {
            final Method m = Configuration.class.getDeclaredMethod("mergeWithAdditionalConfigurations", Map.class);
            m.setAccessible(true);
            Assert.assertThat(
                    "Result of additional configuration loading failed!",
                    m.invoke(null, inputMap),
                    CoreMatchers.equalTo(resultMap));
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
