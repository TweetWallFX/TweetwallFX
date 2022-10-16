/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 TweetWallFX
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetwallfx.util.testcase.RunnableTestCase;
import org.tweetwallfx.stepengine.api.DataProvider;

/**
 * Testcase checking that all registered {@link DataProvider.Factory} instances
 * are loadable.
 */
public class DataProviderFactoryLoadable implements RunnableTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(DataProviderFactoryLoadable.class);

    @Override
    public void execute() {
        for (final DataProvider.Factory o : ServiceLoader.load(DataProvider.Factory.class)) {
            LOG.info("loaded {}", o.getClass());
        }
    }
}
