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
package org.tweetwallfx.stepengine.api;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;

/**
 * Registry which holds and creates all {@link Visualization}s.
 *
 */
public class VisualizationRegistry {

    public static VisualizationRegistry INSTANCE = new VisualizationRegistry();
    private final Map<String, StepEngineSettings.VisualizationSetting> visualizationSettingsMap
            = Configuration.getInstance().getConfigTyped(StepEngineSettings.CONFIG_KEY, StepEngineSettings.class).getVisualizationSettings();

    private final Map<String, Visualization> visualizationCache = new ConcurrentHashMap<>();

    private VisualizationRegistry() {
    }

    /**
     * Returns the requested {@link Visualization}. In case the visualization is
     * already in the cache it is just returned, else the the visualization is
     * created using the associated factory.
     *
     * @param <V> the generic type of the visualization
     * @param identifier the identifier of the {@link Visualization}
     * @param visualizationClass the class to be used for type safety check
     * @return the requested {@link Visualization}
     */
    public <V extends Visualization> V getVisualization(String identifier, Class<V> visualizationClass) {
        Visualization vis = getVisualizationInternal(identifier);
        if (!visualizationClass.isInstance(vis)) {
            throw new IllegalArgumentException("Visualization identified by " + identifier + " is not of expected type " + visualizationClass.getName() + " but of type " + vis.getClass().getName());
        }
        return visualizationClass.cast(vis);
    }

    /**
     * Returns the {@link DataProvider}s required by a {@link Visualization}.
     * The definition of the required providers is done via the associated
     * {@link Visualization.Factory}
     *
     * @param identifier the identifier of the {@link Visualization}
     * @return the required {@link DataProvider}s
     */
    public Collection<Class<? extends DataProvider>> getRequiredDataProviders(String identifier) {
        StepEngineSettings.VisualizationSetting settings = visualizationSettingsMap.get(identifier);
        String visualizationClassName = settings.getVisualizationClassName();
        return getVisualizationFactory(identifier, visualizationClassName).getRequiredDataProviders(settings);
    }

    private Visualization getVisualizationInternal(String identifier) {
        return visualizationCache.computeIfAbsent(identifier, s -> {
            StepEngineSettings.VisualizationSetting settings = visualizationSettingsMap.get(s);
            String visualizationClassName = settings.getVisualizationClassName();
            return getVisualizationFactory(identifier, visualizationClassName).create(settings);
        });
    }

    private Visualization.Factory getVisualizationFactory(String identifier, String visualizationClassName) {
        return StreamSupport.stream(ServiceLoader.load(Visualization.Factory.class).spliterator(), false)
                .filter(factory -> visualizationClassName.equals(factory.getVisualizationClass().getName()))
                .findFirst().orElseThrow(()
                        -> new RuntimeException("Failed: No Visualization.Factory found for "
                        + identifier + " and " + visualizationClassName)
                );
    }
}
