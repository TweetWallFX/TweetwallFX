/*
 * The MIT License
 *
 * Copyright 2014-2015 eFX - TweetWallFX
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
package org.tweetwallfx.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author MICHELB
 */
public class StopList {

    //TODO: Init from file.
    //TODO: Add I18N support
    private StopList() {
    }

    private static final List<String> stopList = new ArrayList<>(
            Arrays.asList("http", "https", "has", "have", "do", "for", "are", "the", "and",
                    "with", "here", "active", "see", "next", "will", "any", "off", "there", "while", "just", "all", "from", "got", "think", "nice",
                    "ask", "can", "you", "week", "some", "not", "didn", "isn", "per", "how", "show", "out", "but", "last", "your", "one", "should",
                    "now", "also", "done", "will", "become", "did", "what", "when", "let", "that", "this", "always", "where", "our"));

    public static boolean add(String stopword) {
        return stopList.add(stopword);
    }

    public static boolean contains(String string) {
        return stopList.contains(string);
    }
}
