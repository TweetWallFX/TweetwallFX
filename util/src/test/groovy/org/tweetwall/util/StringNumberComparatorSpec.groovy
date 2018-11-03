/*
 * The MIT License
 *
 * Copyright 2015-2018 TweetWallFX
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
package org.tweetwall.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StringNumberComparatorSpec extends Specification {
    
    def "#result == compare(#string1, #string2)"() {
        expect:
        result == StringNumberComparator.INSTANCE.compare(string1, string2)
        
        where:
        [result, string1, string2] << [
            [
                0,
                'A',
                'A',
            ],
            [
                0,
                'Alpha',
                'Alpha',
            ],
            [
                -1,
                'Alpha 0',
                'Alpha 00',
            ],
            [
                -1,
                'Alpha 0',
                'Alpha 0 ',
            ],
            [
                1,
                'Beta 20',
                'Beta 2',
            ],
            [
                1,
                'Beta 00020',
                'Beta 2',
            ],
        ]
    }
}
