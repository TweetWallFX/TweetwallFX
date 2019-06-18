/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 TweetWallFX
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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * String Comparator comparing the string based on its parts.
 * <p>
 * Parts are evaluated in the order they occur in the input strings and only
 * same index parts are compared against each other. A Part either contains only
 * digits - as identified by {@link Character#isDigit(char)} - or any charcter
 * other than a digit.
 * <p>
 * If both parts in a parts pair contain numbers their number values are
 * compared - by virtue of {@link BigDecimal#compareTo(java.math.BigDecimal)}.
 * Otherwise a regular string comparison is performed. If a parts comparison
 * evaluates to zero then the next pair of parts is evaluated until either side
 * no longer has any parts to compare.
 * <p>
 * A string length comparison acts as a last way to identify string that is
 * longer than the other.
 */
public final class StringNumberComparator implements Comparator<String> {

    private static final long serialVersionUID = 8726482374926293877L;

    public static final Comparator<String> INSTANCE = new StringNumberComparator();

    private StringNumberComparator() {
        // prevent instantiation
    }

    @Override
    public int compare(final String string1, final String string2) {
        final Iterator<String> iterator1 = new StringNumberPartIterator(string1);
        final Iterator<String> iterator2 = new StringNumberPartIterator(string2);

        while (iterator1.hasNext() && iterator2.hasNext()) {
            final String part1 = iterator1.next();
            final String part2 = iterator2.next();
            final int result;

            if (isNumber(part1) && isNumber(part2)) {
                result = Long.valueOf(part1).compareTo(Long.valueOf(part2));
            } else {
                result = part1.compareTo(part2);
            }

            if (0 != result) {
                return result;
            }
        }

        return Integer.compare(string1.length(), string2.length());
    }

    private static boolean isNumber(final String string) {
        return Character.isDigit(string.charAt(0));
    }

    private static class StringNumberPartIterator implements Iterator<String> {

        private final String string;
        private int index = 0;

        private StringNumberPartIterator(final String string) {
            this.string = string;
        }

        @Override
        public boolean hasNext() {
            return index < string.length();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("End of string was reached");
            }

            final boolean isDigit = Character.isDigit(string.charAt(index));
            int partIndexEnd = index + 1;

            while (partIndexEnd < string.length()) {
                if (isDigit ^ Character.isDigit(string.charAt(partIndexEnd))) {
                    break;
                }
                partIndexEnd++;
            }

            final String result = string.substring(index, partIndexEnd);
            index = partIndexEnd;
            return result;
        }
    }
}
