/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 TweetWallFX
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
package org.tweetwallfx.util.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import java.util.Optional;

/**
 * A storage of a singe EXIF data entry.
 */
public record ExifData(String directory, String name, int type, String value, String description) {

    private static final Logger LOG = LoggerFactory.getLogger(ExifData.class);

    public static Optional<String> locateOptional(final List<ExifData> exifData, final String directory, final String tagName) {
        return exifData.stream()
                .filter(ed -> directory.equals(ed.directory()))
                .filter(ed -> tagName.equals(ed.name()))
                .map(ExifData::value)
                .findFirst();
    }

    /**
     * Attempts to read an entire set of EXIF meta data from the image contained
     * within the given {@code inputStream}.
     *
     * @param inputStream the {@link InputStream} to read from
     *
     * @return the read EXIF data entries or an empty list if none could be read
     */
    public static List<ExifData> readFrom(final InputStream inputStream) {
        final Metadata metadata;

        try (InputStream is = inputStream) {
            metadata = ImageMetadataReader.readMetadata(is);
        } catch (final IOException | ImageProcessingException ioe) {
            LOG.error("Failed to read EXIF Data", ioe);
            return List.of();
        }

        return StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                .flatMap(
                        d -> d.getTags().stream().map(
                                t -> new ExifData(
                                        t.getDirectoryName(),
                                        t.getTagName(),
                                        t.getTagType(),
                                        d.getString(t.getTagType()),
                                        t.getDescription()
                                )
                        )
                )
                .toList();
    }
}
