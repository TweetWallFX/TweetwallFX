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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.config.Configuration;
import org.tweetwallfx.config.TweetwallSettings;

/**
 * StopList consists of a list containing twitter related stop words,
 * a list containing custom stop words and a stop list which is language
 * dependant. The language dependent list is loaded from resource file.
 * 
 * @author MICHELB
 */
public final class StopList {
    private static final Logger LOG = LogManager.getLogger(StopList.class);
    //TODO: Init from file.
    //TODO: Add I18N support
    {
        //extract Hashtags from complex query and add to StopList
        String searchText = Configuration.getInstance().getConfigTyped(TweetwallSettings.CONFIG_KEY, TweetwallSettings.class).getQuery();
        final Matcher m = Pattern.compile("#[\\S]+").matcher(searchText);
        while (m.find()) {
            StopList.add(m.group(0));
        }
    }
    
    private StopList() {
    }

    private static final Set<String> twitterList = new HashSet<>(
            Arrays.asList(
                    //twitter related
                    "rt", "http", "https"));
    private static final Set<String> customList = new HashSet<>();
    private static final Set<String> stopList = readStopListResource("stoplist.list");

    private static final Pattern trimPattern = Pattern.compile("(\\S+?)[.,!?:;´`']+"); //cut of bad word tails.
    
    public static final Predicate<String> IS_NOT_URL = Pattern.compile("http[s]?:.*").asPredicate().negate();   //url pattern
    
    public static final Pattern WORD_SPLIT = Pattern.compile("\\s+");    
    
    /**
     * Add a word lowercase to stop list.
     *
     * @param stopword to add.
     */
    public static void add(String stopword) {
        customList.add(stopword.toLowerCase());
    }

    /**
     * Add words lowercase to stop list.
     *
     * @param stopwords to add.
     */
    public static void add(String... stopwords) {
        Arrays.stream(stopwords).map(String::toLowerCase).forEach(customList::add);
    }

    /**
     * Check if the word is in stop list. Word is checked lowercase.
     *
     * @param word to be checked.
     * @return true if contained.
     */
    public static boolean notIn(String word) {
        String lowerWord = word.toLowerCase();
        return !twitterList.contains(lowerWord) && 
               !stopList.contains(lowerWord) && 
               !customList.contains(lowerWord);
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
    
    private static Set<String> readStopListResource(String resourceName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            return reader.lines().map(String::trim).filter((s) -> !s.isEmpty()).collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.error("Unable to load stoplist resource: " + resourceName, e);
        }
        return Collections.emptySet();
    }
}
