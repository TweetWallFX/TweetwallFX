/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 TweetWallFX
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

import com.vdurmont.emoji.EmojiParser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Emojify {

    @SuppressWarnings({"NarrowCalculation", "StringSplitter"})
    private static String convert(String unicodeStr) {
        if (unicodeStr.isEmpty()) {
            return unicodeStr;
        }
        List<String> parts = new ArrayList<String>();
        int c;
        int p = 0;
        for (int i = 0; i < unicodeStr.length(); i++) {
            c = unicodeStr.charAt(i);
            if (p > 0) {
                parts.add(Integer.toString(0x10000 + ((p - 0xD800) << 10) + (c - 0xDC00), 16));
                p = 0;
            } else if (0xD800 <= c && c <= 0xDBFF) {
                p = c;
            } else {
                parts.add(Integer.toString(c, 16));
                p = 0;
            }
        }
        String dashedLogic = parts.stream().collect(Collectors.joining("-"));
        return dashedLogic;
    }

    public static List<Object> tokenizeStringToTextAndEmoji(final String message) {
        var emojiCandidates = new ArrayList<EmojiParser.UnicodeCandidate>();
        EmojiParser.parseFromUnicode(message, new EmojiParser.EmojiTransformer() {
            @Override
            public String transform(EmojiParser.UnicodeCandidate unicodeCandidate) {
                emojiCandidates.add(unicodeCandidate);
                return "";
            }
        });
        var objects = new ArrayList<Object>();
        int startIndex = 0;
        for (com.vdurmont.emoji.EmojiParser.UnicodeCandidate unicodeCandidate : emojiCandidates) {
            String text = message.substring(startIndex, unicodeCandidate.getEmojiStartIndex());
            if (!text.isEmpty()) {
                objects.add(text);
            }
            startIndex = unicodeCandidate.getFitzpatrickEndIndex();
            String hex = convert(unicodeCandidate.getEmoji().getUnicode());
            objects.add(new Twemoji(hex));
        }

        if (startIndex < message.length()) {
            objects.add(message.substring(startIndex, message.length()));
        }

        return objects;
    }
}
