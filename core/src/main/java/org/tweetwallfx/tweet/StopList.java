/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
package org.tweetwallfx.tweet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vdurmont.emoji.EmojiParser;

/**
 *
 * @author MICHELB
 */
public final class StopList {

    //TODO: Init from file.
    //TODO: Add I18N support
    private StopList() {
    }

    private static final Set<String> stopList = new HashSet<>(
            Arrays.asList(
                    //twitter related
                    "rt", "http", "https",
                    //other
                    "has", "have", "do", "for", "are", "the", "and", "about", "use", "using", "between", "like", "them", "away", "throw",
                    "with", "here", "active", "see", "next", "will", "any", "off", "there", "while", "just", "all", "from", "got", "think", "nice",
                    "ask", "can", "you", "week", "some", "not", "didn", "isn", "per", "how", "show", "out", "but", "last", "your", "one", "should",
                    "now", "also", "done", "will", "become", "did", "what", "when", "let", "that", "this", "always", "where", "our", "his", "her", "of",
                    "attached", "don"));

    private static final Pattern trimPattern = Pattern.compile("(\\S+?)[.,!?:;´`']+"); //cut of bad word tails.
    
    public static final Predicate<String> IS_NOT_URL = Pattern.compile("http[s]?:.*").asPredicate().negate();   //url pattern
    
    public static final Pattern WORD_SPLIT = Pattern.compile("\\s+");    
    
    /**
     * Add a word to stop list.
     *
     * @param stopword to add.
     */
    public static void add(String stopword) {
        stopList.add(stopword);
    }

    /**
     * Add words to stop list.
     *
     * @param stopwords to add.
     */
    public static void add(String... stopwords) {
        Collections.addAll(stopList, stopwords);
    }

    /**
     * Check if the word is in stop list. Word is checked lowercase.
     *
     * @param word to be checked.
     * @return true if contained.
     */
    public static boolean notIn(String word) {
        return !stopList.contains(word.toLowerCase());
    }

    public static String trimTail(final String s) {
        Matcher matcher = trimPattern.matcher(s);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return s;
        }
    }
    
    public static String removeEmojis(final String s) {
        return EmojiParser.removeAllEmojis(s);
    }
    
}
