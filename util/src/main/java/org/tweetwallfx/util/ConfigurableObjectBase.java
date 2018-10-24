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
package org.tweetwallfx.util;

import java.util.Collections;
import java.util.Map;

/**
 * Configurable base object with the possibility to get configuration data in
 * its raw form (via {@link #getConfig()}) or as a type safe object (via
 * {@link #getConfig(java.lang.Class)}).
 */
public abstract class ConfigurableObjectBase {

    private Map<String, Object> config;

    /**
     * Gets the objects configuration data in its raw form.
     *
     * @return the objects configuration data in its raw form
     */
    public final Map<String, Object> getConfig() {
        return null == config
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(config);
    }

    /**
     * Gets the objects configuration data via a type safe object.
     *
     * @param <T> the type of the type safe representation of the configuration
     * data
     *
     * @param typeClass the class of the type safe representation of the
     * configuration data
     *
     * @return the objects configuration data via a type safe object
     */
    public final <T> T getConfig(final Class<T> typeClass) {
        return JsonDataConverter.convertFromObject(getConfig(), typeClass);
    }

    /**
     * Sets the objects configuration data.
     *
     * @param config the configuration data
     */
    public final void setConfig(final Map<String, Object> config) {
        this.config = config;
    }
}
