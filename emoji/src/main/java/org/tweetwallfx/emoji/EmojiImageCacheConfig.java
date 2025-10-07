/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 TweetWallFX
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
package org.tweetwallfx.emoji;

import java.util.Objects;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.ConfigurationConverter;

public record EmojiImageCacheConfig(
        String emojiImageBaseUrl) {

    /**
     * Configuration key under which the data for this Settings object is stored
     * in the configuration data map.
     */
    public static final String CONFIG_KEY = EmojiImageCache.class.getSimpleName();

    public EmojiImageCacheConfig {
        String bu = Objects.requireNonNullElse(emojiImageBaseUrl, "https://cdnjs.cloudflare.com/ajax/libs/twemoji/15.1.0/72x72");

        // ensure baseUrl ends with a '/'
        if (!bu.endsWith("/")) {
            bu += "/";
        }

        emojiImageBaseUrl = bu;
    }

    public static EmojiImageCacheConfig getInstance() {
        return Configuration.getInstance().getConfigTyped(CONFIG_KEY, EmojiImageCacheConfig.class);
    }

    /**
     * Service implementation converting the configuration data of the root key
     * {@link #CONFIG_KEY} into {@link EmojiImageCacheConfig}.
     */
    public static final class Converter implements ConfigurationConverter {

        @Override
        public String getResponsibleKey() {
            return CONFIG_KEY;
        }

        @Override
        public Class<?> getDataClass() {
            return EmojiImageCacheConfig.class;
        }
    }
}
