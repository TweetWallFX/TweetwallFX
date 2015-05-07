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
package org.tweetwallfx.twitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * @author José Pereda
 */
public class CLogOut {

    private StringProperty message = new SimpleStringProperty();

    private static CLogOut instance = null;

    private CLogOut() {
        final ByteArrayOutputStream stdStream = new ByteArrayOutputStream() {
            @Override
            public synchronized void flush() throws IOException {
                String theString = toString();

                /* OK:
                 Establishing connection.
                 Twitter Stream consumer-1[Establishing connection]
                 Connection established.
                 Receiving status stream.
                 Twitter Stream consumer-1[Receiving stream]
                 *Received:{...}
                 Twitter Stream consumer-1[Disposing thread]

                 */

                /* WRONG:
                 Establishing connection.
                 Twitter Stream consumer-1[Establishing connection]
                 Exceeded connection limit for user
                 420
                 Waiting for 10000 milliseconds
                 Twitter Stream consumer-1[Disposing thread]

                 */
                Platform.runLater(() -> {
                    if (theString.startsWith("Establishing connection")) {
                        message.set("Establishing connection...\n Please, wait a few seconds");
                    } else if (theString.startsWith("Receiving status stream")) {
                        message.set("Receiving tweets!! \n Press stop button to stop the search");
                    } else if (theString.startsWith("Exceeded connection limit")) {
                        message.set("Exceeded connection limit...");
                    } else if (theString.startsWith("Waiting for ")) {
                        message.set(theString + " or press stop button to stop the search");
                    } else if (theString.contains("Disposing thread")) {
                        message.set("The search has finished");
                    }
                });
                System.out.print("***** " + theString);
                reset();
            }
        };

        Logger root = Logger.getRootLogger();
        WriterAppender writerAppender = new WriterAppender(new PatternLayout("%m%n"), stdStream);
        root.addAppender(writerAppender);
        writerAppender.setImmediateFlush(true);
    }

    public static CLogOut getInstance() {
        if (instance == null) {
            instance = new CLogOut();
        }
        return instance;
    }

    public StringProperty getMessages() {
        return message;
    }

}
