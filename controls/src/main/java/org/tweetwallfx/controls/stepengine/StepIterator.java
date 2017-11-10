/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
package org.tweetwallfx.controls.stepengine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.json.bind.JsonbBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jörg Michelberger
 */
public class StepIterator implements Iterator<Step> {

    private static final Logger LOGGER = LogManager.getLogger(StepIterator.class);
    private Step current = null;
    private int stateIndex = 0;
    private final List<Step> states = new ArrayList<>();

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class Builder {

        private final List<Step> steps = new ArrayList<>();

        public Builder addStep(String classname) {
            try {
                Object newInstance = Thread.currentThread().getContextClassLoader().loadClass(classname).getDeclaredConstructor().newInstance();
                if (newInstance instanceof Step) {
                    steps.add((Step) newInstance);
                } else {
                    LOGGER.error("Class cannot be cast to Step " + classname + ". Skipping adding the step.");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException ex) {
                LOGGER.error("Failure instatiating step for " + classname, ex);
            }
            return this;
        }

        public StepIterator build() {
            return new StepIterator(steps);
        }
    }

    public static StepIterator ofDefaultConfiguration() {
        StepIterator.Builder builder = new StepIterator.Builder();
        try (InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream("steps.json")) {
            StepEngineConfiguration stepEngineConfig = JsonbBuilder.create().fromJson(s, StepEngineConfiguration.class);
            stepEngineConfig.steps.forEach(className -> builder.addStep(className));
        } catch (IOException ex) {
            LOGGER.error("IO Problem loading steps description file", ex);
        }
        return builder.build();
    }

    public static StepIterator of(Step... steps) {
        return new StepIterator(Arrays.asList(steps));
    }

    public static StepIterator of(List<Step> steps) {
        return new StepIterator(steps);
    }

    private StepIterator(List<Step> states) {
        this.states.addAll(states);
    }

    void applyWith(Consumer<Step> consumer) {
        states.forEach(consumer);
    }

    public Step getCurrent() {
        return current;
    }

    public Step getNext() {
        int getIndex = stateIndex;
        if (getIndex == states.size()) {
            getIndex = 0;
        }
        return states.get(getIndex);
    }

    @Override
    public Step next() {
        if (stateIndex == states.size()) {
            //loop
            stateIndex = 0;
        }
        current = states.get(stateIndex++);
        return current;
    }
}
