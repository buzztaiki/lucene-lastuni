/*
 * Copyright (C) 2012  Taiki Sugawara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.buzztaiki.lucene.lastuni;

import java.io.StringReader;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class CJKLastUniGramFilterTest extends BaseTokenStreamTestCase {
    private static CJKLastUniGramFilter newFilter(String str) {
        StandardTokenizer tokenizer = new StandardTokenizer();
	tokenizer.setReader(new StringReader(str));
        TokenStream ts = new CJKBigramFilter(tokenizer);
        return new CJKLastUniGramFilter(ts, true);
    }

    public void testSingleWord() throws Exception {
        TokenStream ts = newFilter("あいうえお");
        assertTokenStreamContents(ts,
            new String[]{"あい", "いう", "うえ", "えお", "お"},
            new int[]   {0,      1,      2,      3,      4},
            new int[]   {2,      3,      4,      5,      5},
            5
        );
    }
    public void testMultiWord() throws Exception {
        TokenStream ts = newFilter("あい う えお");
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "う", "えお", "お"},
            new int[]   {0,      1,    3,    5,      6},
            new int[]   {2,      2,    4,    7,      7},
            7
        );
    }

    public void testSingleChar() throws Exception {
        TokenStream ts = newFilter("あ");
        assertTokenStreamContents(ts,
            new String[]{"あ"},
            new int[]   {0},
            new int[]   {1},
            1
        );
    }

    public void testSingleToken() throws Exception {
        TokenStream ts = newFilter("あい");
        assertTokenStreamContents(ts,
            new String[]{"あい", "い"},
            new int[]   {0,      1},
            new int[]   {2,      2},
            2
        );
    }

    public void testAsciiAndCJK() throws Exception {
        TokenStream ts = newFilter("あいabcうえお");
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "abc", "うえ", "えお", "お"},
            new int[]   {0,      1, 2, 5, 6, 7},
            new int[]   {2,      2, 5, 7, 8, 8},
            8
        );
    }

    public void testSingleTokenTwice() throws Exception {
        TokenStream ts = newFilter("あい うえ");
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "うえ", "え"},
            new int[]   {0,      1,    3,      4},
            new int[]   {2,      2,    5,      5},
            5
        );
    }

}
