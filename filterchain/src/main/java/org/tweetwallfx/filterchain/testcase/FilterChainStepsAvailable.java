/*
 * The MIT License
 *
 * Copyright 2018 TweetWallFX
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
package org.tweetwallfx.filterchain.testcase;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.filterchain.FilterChain;
import org.tweetwallfx.filterchain.FilterChainSettings;
import org.tweetwallfx.filterchain.FilterChainSettings.FilterStepDefinition;
import org.tweetwallfx.filterchain.FilterStep;
import org.tweetwallfx.util.testcase.RunnableTestCase;

/**
 * Testcase checking that all {@link FilterStep}s configured for the
 * {@link FilterChain}s are creatable.
 */
public class FilterChainStepsAvailable implements RunnableTestCase {

    @Override
    public void execute() throws Exception {
        final Map<String, List<FilterStep.Factory>> filterStepFactories = StreamSupport
                .stream(ServiceLoader.load(FilterStep.Factory.class).spliterator(), false)
                .collect(Collectors.groupingBy(fsf -> fsf.getFilterStepClass().getCanonicalName()));
        final List<String> nonCreatableFilterSteps = Configuration.getInstance()
                .getConfigTyped(
                        FilterChainSettings.CONFIG_KEY,
                        FilterChainSettings.class)
                .getChains()
                .entrySet().stream()
                .map(Map.Entry::getValue)
                .map(FilterChainSettings.FilterChainDefinition::getFilterSteps)
                .flatMap(Collection::stream)
                .map(FilterStepDefinition::getStepClassName)
                .collect(Collectors.groupingBy(filterStepFactories::containsKey))
                .get(Boolean.FALSE);

        if (null != nonCreatableFilterSteps) {
            throw new IllegalStateException("FilterChainSettings has the following FilterSteps configured that are not creatable: " + nonCreatableFilterSteps);
        }
    }
}
