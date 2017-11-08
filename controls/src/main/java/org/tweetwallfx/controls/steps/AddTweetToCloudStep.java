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
package org.tweetwallfx.controls.steps;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.controls.dataprovider.TagCloudDataProvider;
import org.tweetwallfx.controls.dataprovider.TweetDataProvider;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.tweet.StopList;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.UrlTweetEntry;
import org.tweetwallfx.tweet.api.entry.UserMentionTweetEntry;

public class AddTweetToCloudStep extends AbstractStep {

    private static final Logger LOGGER = LogManager.getLogger(AddTweetToCloudStep.class);

    @Override
    public Duration preferredStepDuration(final MachineContext context) {
        return Duration.ZERO;
    }

    @Override
    public void doStep(final MachineContext context) {
        Tweet tweetInfo = context.getDataProvider(TweetDataProvider.class).getTweet();
        String text = tweetInfo.getTextWithout(UrlTweetEntry.class)
                .getTextWithout(MediaTweetEntry.class)
                .getTextWithout(UserMentionTweetEntry.class)
                .get()
                .replaceAll("[.,!?:´`']((\\s+)|($))", " ")
                .replaceAll("['“”‘’\"()]", " ");
        Set<Word> tweetWords = StopList.WORD_SPLIT.splitAsStream(text)
                .map(StopList::trimTail) //no bad word tails
                .filter(l -> l.length() > 2) //longer than 2 characters
                .filter(StopList.IS_NOT_URL) // no url or part thereof
                //                .filter(StopList::notIn) //not in stoplist
                .map(l -> new Word(l, 0.1)) //convert to Word
                .collect(Collectors.toSet());                   //collect
        List<Word> words = context.getDataProvider(TagCloudDataProvider.class).getWords();
        tweetWords.removeAll(words);

        LOGGER.info("Adding words to cloud dataset for rendering: " + tweetWords);

        context.getDataProvider(TagCloudDataProvider.class).setAdditionalTweetWords(words);
        context.proceed();
    }
}
