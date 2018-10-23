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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CJKLastUniGramFilterForSearchTest extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

    @Override
    public void setUp() throws Exception {
	super.setUp();
	analyzer = new Analyzer() {
	    @Override
	    protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new StandardTokenizer();
		return new TokenStreamComponents(source, new CJKLastUniGramFilter(new CJKBigramFilter(source), false));
	    }
	};
    }

    @Override
    public void tearDown() throws Exception {
	analyzer.close();
	super.tearDown();
    }

    public void testSingleWord() throws Exception {
	assertAnalyzesTo(analyzer, "あいうえお",
            new String[]{"あい", "いう", "うえ", "えお"},
            new int[]   {0,      1,      2,      3},
            new int[]   {2,      3,      4,      5}
        );
    }
    public void testMultiWord() throws Exception {
	assertAnalyzesTo(analyzer, "あい う えお",
            new String[]{"あい", "い", "う", "えお"},
            new int[]   {0,      1,    3,    5},
            new int[]   {2,      2,    4,    7}
        );
    }

    public void testSingleChar() throws Exception {
        assertAnalyzesTo(analyzer, "あ",
            new String[]{"あ"},
            new int[]   {0},
            new int[]   {1}
        );
    }

    public void testSingleToken() throws Exception {
        assertAnalyzesTo(analyzer, "あい",
            new String[]{"あい"},
            new int[]   {0},
            new int[]   {2}
        );
    }

    public void testAsciiAndCJK() throws Exception {
        assertAnalyzesTo(analyzer, "あいabcうえお",
            new String[]{"あい", "い", "abc", "うえ", "えお"},
            new int[]   {0,      1,    2,     5,      6},
            new int[]   {2,      2,    5,     7,      8}
        );
    }

    public void testSingleTokenTwice() throws Exception {
        assertAnalyzesTo(analyzer, "あい うえ",
            new String[]{"あい", "い", "うえ"},
            new int[]   {0,      1,    3},
            new int[]   {2,      2,    5}
        );
    }

}
