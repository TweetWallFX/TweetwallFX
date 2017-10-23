/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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
package org.tweetwallfx.threed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;


/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * @author José Pereda
 */
final class CLogOut {

    private StringProperty message = new SimpleStringProperty();

    private static CLogOut instance = null;

    private CLogOut() {
        final ByteArrayOutputStream stdStream = new ByteArrayOutputStream() {
            @Override
            public synchronized void flush() throws IOException {
                String theString = toString("UTF-8");

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

        
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("%m%n").build();
        Appender appender = WriterAppender.newBuilder().setLayout(layout).setTarget(new OutputStreamWriter(stdStream, StandardCharsets.UTF_8)).build();
        appender.start();
        config.addAppender(appender);        
        
        updateLoggers(appender, config);        
    }

    private void updateLoggers(final Appender appender, final Configuration config) {
        final Level level = null;
        final Filter filter = null;
        for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
            loggerConfig.addAppender(appender, level, filter);
        }
        config.getRootLogger().addAppender(appender, level, filter);
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
