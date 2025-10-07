/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 TweetWallFX
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
package org.tweetwallfx.mqtt.config;

import java.util.Objects;
import java.util.UUID;

import org.tweetwallfx.config.ConfigurationConverter;

/**
 * POJO for reading Settings concerning the MQTT client.
 *
 * <p>
 * Param {@code debugEnabled} a flag indicating that the MQTT client is to
 * work in debug mode
 *
 * <p>
 * Param {@code brokerUrl} the broker connection URL
 *
 * <p>
 * Param {@code clientId} the MQTT client id for the connection
 * the extended mode
 *
 * <p>
 * Param {@code heartbeatSeconds} the MQTT heart beat delay time in seconds
 *
 * <p>
 * Param {@code auth} the Auth setting the MQTT client is to use in order
 * to connect with the broker
 */
public record MqttSettings(
        Boolean enabled,
        Boolean debugEnabled,
        String brokerUrl,
        String clientId,
        Integer heartbeatSeconds,
        Auth auth) {

    public MqttSettings {
        enabled = Objects.requireNonNullElse(enabled, true);
        debugEnabled = Objects.requireNonNullElse(debugEnabled, false);
        Objects.requireNonNullElse(brokerUrl, "tcp://127.0.0.1:1883");
        clientId = Objects.requireNonNullElse(clientId, UUID.randomUUID().toString());
        heartbeatSeconds = checkValue(Objects.requireNonNullElse(heartbeatSeconds, 5));
    }

    private Integer checkValue(Integer checkedValue) {
        if (checkedValue.intValue() < 1 ) {
            throw new IllegalArgumentException("heartbeatSeconds must be a positive value");
        }
        return checkedValue;
    }

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = "mqtt";

    /**
     * Service implementation converting the configuration data of the root key
     * {@link MqttSettings#CONFIG_KEY} into {@link MqttSettings}.
     */
    public static class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return MqttSettings.CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return MqttSettings.class;
        }
    }

    public static record Auth(
            String userName,
            String secret) {
    }
}
