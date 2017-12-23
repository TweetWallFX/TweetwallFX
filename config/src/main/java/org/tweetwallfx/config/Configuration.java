/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwall.util.JsonDataConverter;
import static org.tweetwall.util.ToString.*;

/**
 * Configuration store of data enabling influence into the configuration of the
 * Application.
 */
public final class Configuration {

    private static final String CONFIG_FILENAME = System.getProperty("org.tweetwall.config.fileName", "tweetwallConfig.json");
    private static final Path[] CONFIG_FILE_PATHS = new Path[]{
        Paths.get(System.getProperty("user.home"), CONFIG_FILENAME),
        Paths.get("etc", CONFIG_FILENAME),
        Paths.get(CONFIG_FILENAME)};
    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);
    private static final Map<String, ConfigurationConverter> CONVERTERS;

    static {
        LOGGER.info("loading configurations data converters");
        final Map<String, List<ConfigurationConverter>> converters = StreamSupport
                .stream(ServiceLoader.load(ConfigurationConverter.class).spliterator(), false)
                .collect(Collectors.groupingBy(ConfigurationConverter::getResponsibleKey));

        // ensure there are no conflicting ConfigurationConverter registered for a specific key
        converters.entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .findAny()
                .ifPresent(e -> {
                    throw new IllegalArgumentException("At most one ConfigurationConverter may be registered to convert configuration data but the following ConfigurationConverters are registered: " + e.getValue());
                });

        CONVERTERS = converters.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }

    private static final Configuration INSTANCE = new Configuration();

    private final Map<String, Object> configurationData = new HashMap<>();

    private Configuration() {
        updateConfigurationData();
    }

    private void updateConfigurationData() {
        final Map<String, Object> configData = loadConfigurationData();

        LOGGER.info("Configurations:");
        configData.entrySet()
                .stream()
                .map(e -> String.format("'%s' -> '%s'", String.valueOf(e.getKey()), String.valueOf(e.getValue())))
                .forEach(LOGGER::info);

        configurationData.clear();
        configurationData.putAll(configData);
    }

    private static Map<String, Object> loadConfigurationData() {
        LOGGER.info("loading configurations data");
        Map<String, Object> result = Collections.emptyMap();

        for (Map<String, Object> map : Stream.concat(
                loadConfigurationDataFromClasspath(),
                loadConfigurationDataFromFilesystem()).collect(Collectors.toList())) {
            result = mergeMap(result, map);
        }

        return convertConfigData(result);
    }

    private static Stream<Map<String, Object>> loadConfigurationDataFromClasspath() {
        LOGGER.info("Searching for configuration files in path '/" + CONFIG_FILENAME + "'");
        Stream<Map<String, Object>> result = Stream.empty();

        try {
            final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(CONFIG_FILENAME);

            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                LOGGER.info("Found config file: " + url);

                try (final InputStream is = url.openStream()) {
                    result = Stream.concat(result, Stream.of(readConfiguration(is, "Classpath entry '" + url.toExternalForm() + '\'')));
                }
            }
        } catch (final IOException ioe) {
            throw new RuntimeException("Error loading configuration data from classpath '/" + CONFIG_FILENAME + "'", ioe);
        }

        return result;
    }

    private static Stream<Map<String, Object>> loadConfigurationDataFromFilesystem() {
        LOGGER.info("Searching for configuration files at paths: " + Arrays.toString(CONFIG_FILE_PATHS));

        return Arrays.stream(CONFIG_FILE_PATHS)
                .filter(Files::isRegularFile)
                .peek(p -> LOGGER.info("Found config override file: " + p.toAbsolutePath()))
                .map(p -> {
                    try (InputStream is = Files.newInputStream(p)) {
                        return readConfiguration(is, "File '" + p.toString() + "'");
                    } catch (IOException ioe) {
                        throw new RuntimeException("Error loading configuration data from " + p, ioe);
                    }
                });
    }

    private static Map<String, Object> readConfiguration(final InputStream input, final String dataSourceIdentification) {
        try {
            final Map<String, Object> result = cast(JsonDataConverter.convertFromInputStream(input, Map.class));

            convertConfigData(result);

            return result;
        } catch (final Throwable t) {
            throw new RuntimeException(dataSourceIdentification + " either does not contain a valid JSONObject or has an invalid structure!", t);
        }
    }

    private static Map<String, Object> convertConfigData(final Map<String, Object> input) {
        // convert configuration data
        final Map<String, Object> result = new HashMap<>(input);

        CONVERTERS.entrySet()
                .stream()
                .peek(ce -> LOGGER.info("Processing key '" + ce.getKey() + "' with ConfigurationConverter '" + ce.getValue() + "'"))
                .filter(e -> input.containsKey(e.getKey()))
                .forEach(e -> result.put(e.getKey(), JsonDataConverter.convertFromObject(input.get(e.getKey()), e.getValue().getDataClass())));

        return result;
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the Object stored under the {@code param} in the configuration
     * data map. An entry must exist for the provided {@code param} otherwise a
     * {@link NullPointerException} is thrown.
     *
     * @param param the name of the requested entry
     *
     * @return the value of the requested entry
     *
     * @throws NullPointerException in case the configuration data map does not
     * contain a value for the requested {@code param}
     */
    public Object getConfig(final String param) {
        return Objects.requireNonNull(
                configurationData.get(param),
                "Configuration for '" + param + "' does not exist");
    }

    /**
     * Returns the Object stored under the {@code param} in the configuration
     * data map. If no value exists for the {@code param} the provided
     * {@code defaultValue} is returned.
     *
     * @param param the name of the requested entry
     *
     * @param defaultValue the value to return should no entry exist for the
     * {@code param}
     *
     * @return the value of the requested entry
     */
    public Object getConfig(final String param, final Object defaultValue) {
        return configurationData.getOrDefault(param, defaultValue);
    }

    /**
     * Returns the typesafe Object stored under the {@code param} in the
     * configuration data map. An entry must exist for the provided
     * {@code param} otherwise a {@link NullPointerException} is thrown.
     *
     * @param <T> the type of the requested value
     *
     * @param param the name of the requested entry
     *
     * @param paramClass the type class of the requested value
     *
     * @return the value of the requested entry
     *
     * @throws NullPointerException in case the configuration data map does not
     * contain a value for the requested {@code param}
     */
    public <T> T getConfigTyped(final String param, final Class<T> paramClass) {
        return cast(getConfig(param));
    }

    /**
     * Returns the typesafe Object stored under the {@code param} in the
     * configuration data map. If no value exists for the {@code param} the
     * provided {@code defaultValue} is returned.
     *
     * @param <T> the type of the requested value
     *
     * @param param the name of the requested entry
     *
     * @param paramClass the type class of the requested value
     *
     * @param defaultValue the value to return should no entry exist for the
     * {@code param}
     *
     * @return the value of the requested entry
     */
    public <T> T getConfigTyped(final String param, final Class<T> paramClass, final Object defaultValue) {
        return cast(getConfig(param, defaultValue));
    }

    /**
     * Returns an {@link Optional} object containing the value stored under the
     * provided {@code param} key. Should no entry exist for the {@code param}
     * en empty {@link Optional} is returned.
     *
     * @param param the name of the requested entry
     *
     * @return Optional containg the value of an existing entry for the
     * {@code param} or an empty Optional
     */
    public Optional<Object> getConfigOptional(final String param) {
        return Optional.ofNullable(getConfig(param, null));
    }

    /**
     * Returns an {@link Optional} object containing the typesafe value stored
     * under the provided {@code param} key. Should no entry exist for the
     * {@code param} en empty {@link Optional} is returned.
     *
     * @param <T> the type of the requested value
     *
     * @param param the name of the requested entry
     *
     * @param paramClass the type class of the requested value
     *
     * @return Optional containg the value of an existing entry for the
     * {@code param} or an empty Optional
     */
    public <T> Optional<T> getConfigTypedOptional(final String param, final Class<T> paramClass) {
        return Optional.ofNullable(getConfigTyped(param, paramClass, null));
    }

    @Override
    public String toString() {
        return createToString(this, map(
                "configurationData", configurationData
        ));
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(final Object obj) {
        return (T) obj;
    }

    private static Map<String, Object> mergeMap(final Map<String, Object> previous, final Map<String, Object> next) {
        Objects.requireNonNull(next, "Parameter next must not be null!");

        if (null == previous || previous.isEmpty()) {
            return next;
        }

        return Stream.concat(previous.keySet().stream(), next.keySet().stream())
                .sorted()
                .distinct()
                .collect(Collectors.toMap(Function.identity(), key -> mergeValue(key, previous.get(key), next.get(key))));
    }

    private static Object mergeValue(final String key, final Object previous, final Object next) {
        if (null == previous) {
            return Objects.requireNonNull(next, key + " Parameter next must not be null!");
        } else if (null == next) {
            return Objects.requireNonNull(previous, key + " Parameter previous must not be null!");
        }

        final Class<?> pClass = previous.getClass();
        final Class<?> nClass = next.getClass();

        if (pClass.getName().startsWith("java.lang.") || nClass.getName().startsWith("java.lang.")) {
            return next;

        } else if (Map.class
                .isInstance(previous) && Map.class
                .isInstance(next)) {
            return mergeMap(cast(previous), cast(next));
        } else {
            throw new UnsupportedOperationException("Merging type " + pClass + " with " + nClass + " is not supported!");
        }
    }
}
