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
package org.tweetwallfx.stepengine.api.testcase;

import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.tweetwallfx.stepengine.api.Visualization;
import org.tweetwallfx.util.testcase.RunnableTestCase;

/**
 * Testcase checking that no two registered {@link Visualization.Factory} instances
 * produce the same Visualization type.
 */
public class VisualizationFactoryProducesUniqueVisualization implements RunnableTestCase {

    @Override
    public void execute() {
        StreamSupport.stream(ServiceLoader.load(Visualization.Factory.class).spliterator(), false)
               .collect(Collectors.groupingBy(sf -> sf.getVisualizationClass().getCanonicalName()))
                .entrySet().stream()
                .filter(e -> 1 != e.getValue().size())
                .forEach(e -> {
                    throw new IllegalArgumentException(String.format("At most one Visualization.Factory must be registered creating '%s'"
                            + ", but the following are registered: %s!", e.getKey(), e.getValue()));
                });
    }
}
