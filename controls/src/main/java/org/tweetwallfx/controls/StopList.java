/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
