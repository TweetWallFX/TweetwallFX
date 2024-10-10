/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 TweetWallFX
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
package org.tweetwallfx.stepengine.api.config;

import java.time.temporal.ChronoUnit;
import org.tweetwallfx.util.JsonDataConverter;

/**
 * Abstract step configuration providing stepDuration as config option.
 */
public class AbstractConfig {

    private long stepDuration = 1;
    private ChronoUnit stepDurationUnit = ChronoUnit.MILLIS;

    public long getStepDuration() {
        return stepDuration;
    }

    public ChronoUnit getStepDurationUnit() {
        return stepDurationUnit;
    }

    public void setStepDuration(long stepDuration) {
        this.stepDuration = stepDuration;
    }

    public void setStepDurationUnit(ChronoUnit stepDurationUnit) {
        this.stepDurationUnit = stepDurationUnit;
    }

    public java.time.Duration stepDuration() {
        return java.time.Duration.of(getStepDuration(), getStepDurationUnit());
    }

    @Override
    public String toString() {
        return super.toString() + JsonDataConverter.convertToString(this);
    }
}
