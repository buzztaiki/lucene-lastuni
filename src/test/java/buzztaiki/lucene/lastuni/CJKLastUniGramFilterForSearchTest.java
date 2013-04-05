package buzztaiki.lucene.lastuni;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.StringReader;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class CJKLastUniGramFilterForSearchTest extends BaseTokenStreamTestCase {
    public void testSingleWord() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あいうえお")), false);
        assertTokenStreamContents(ts,
            new String[]{"あい", "いう", "うえ", "えお"},
            new int[]   {0,      1,      2,      3},
            new int[]   {2,      3,      4,      5},
            5
        );
    }
    public void testMultiWord() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あい う えお")), false);
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "う", "えお"},
            new int[]   {0,      1,    3,    5},
            new int[]   {2,      2,    4,    7},
            7
        );
    }

    public void testSingleChar() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あ")), false);
        assertTokenStreamContents(ts,
            new String[]{"あ"},
            new int[]   {0},
            new int[]   {1},
            1
        );
    }

    public void testSingleToken() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あい")), false);
        assertTokenStreamContents(ts,
            new String[]{"あい"},
            new int[]   {0},
            new int[]   {2},
            2
        );
    }

    public void testAsciiAndCJK() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あいabcうえお")), false);
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "abc", "うえ", "えお"},
            new int[]   {0,      1,    2,     5,      6},
            new int[]   {2,      2,    5,     7,      8},
            8
        );
    }

    public void testSingleTokenTwice() throws Exception {
        TokenStream ts = new CJKLastUniGramFilter(new CJKTokenizer(new StringReader("あい うえ")), false);
        assertTokenStreamContents(ts,
            new String[]{"あい", "い", "うえ"},
            new int[]   {0,      1,    3},
            new int[]   {2,      2,    5},
            5
        );
    }

}
