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
package org.tweetwallfx.config.testcase;

import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.tweetwallfx.config.ConfigurationConverter;
import org.tweetwallfx.util.testcase.RunnableTestCase;

/**
 * Testcase checking that no two registered {@link ConfigurationConverter}
 * instances are responsible for the same value via
 * {@link ConfigurationConverter#getResponsibleKey()}.
 */
public class ConfigurationKeyConverterUnique implements RunnableTestCase {

    @Override
    public void execute() throws Exception {
        StreamSupport.stream(ServiceLoader.load(ConfigurationConverter.class).spliterator(), false)
                .collect(Collectors.groupingBy(ConfigurationConverter::getResponsibleKey))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(e -> {
                    throw new IllegalArgumentException(String.format("At most one ConfigurationConverter may be registered to "
                            + "convert configuration data of a specific key, but the following ConfigurationConverters are "
                            + "registered for key '%s': %s", e.getKey(), e.getValue()));
                });
    }
}
