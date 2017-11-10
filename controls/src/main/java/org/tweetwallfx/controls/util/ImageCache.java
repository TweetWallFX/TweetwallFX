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
package org.tweetwallfx.controls.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.scene.image.Image;
import static org.tweetwallfx.controls.util.URLHelper.*;

/**
 * @author Sven Reimers
 */
public class ImageCache {

    private final int maxSize;
    private final Map<String, Reference<Image>> cache = new HashMap<>();
    private final LinkedList<String> lru = new LinkedList<>();
    private final ImageCreator creator;

    public ImageCache(final ImageCreator creator, int maxSize) {
        this.creator = creator;
        this.maxSize = maxSize;
    }

    public ImageCache(final ImageCreator creator) {
        this(creator, 10);
    }

    public Image get(final String url) {
        Image image;
        Reference<Image> imageRef = cache.get(url);
        if (null == imageRef || (null == (image = imageRef.get()))) {
            image = creator.create(url);
            cache.put(url, new SoftReference<>(image));
            lru.addFirst(url);
        } else {
            if (!url.equals(lru.peekFirst())) {
                lru.remove(url);
                lru.addFirst(url);
            }
        }
        if (lru.size() > maxSize) {
            String oldest = lru.removeLast();
            cache.remove(oldest);
        }
        return image;
    }

    public static interface ImageCreator {

        Image create(final String url);
    }

    public static class DefaultImageCreator implements ImageCreator {

        @Override
        public Image create(final String url) {
            return new Image(resolve(url));
        }
    }

    public static class ProfileImageCreator implements ImageCreator {

        @Override
        public Image create(final String url) {
            return new Image(resolve(url), 64, 64, true, false);
        }
    }
}
