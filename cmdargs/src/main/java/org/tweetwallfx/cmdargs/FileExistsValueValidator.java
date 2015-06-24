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
package org.tweetwallfx.cmdargs;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;
import java.io.File;

/**
 * Parameter value validator for files that checks if the declared
 * {@link java.io.File} exists. If that is not the case then a
 * {@link com.beust.jcommander.ParameterException} will be thrown.
 *
 * @author martin
 */
public class FileExistsValueValidator implements IValueValidator<File> {

    @Override
    public void validate(String string, File t) throws ParameterException {
        if (null == t) {
            throw new ParameterException(string + " is not set!");
        } else if (!t.exists()) {
            throw new ParameterException(t.getAbsolutePath() + " does not exist!");
        }
    }
}
