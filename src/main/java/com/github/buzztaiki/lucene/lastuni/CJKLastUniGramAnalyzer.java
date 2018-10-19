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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;


/**
 * An {@link Analyzer} that tokenizes text with {@link StandardTokenizer},
 * normalizes content with {@link CJKWidthFilter}, folds case with
 * {@link LowerCaseFilter}, forms bigrams of CJK with {@link CJKBigramFilter},
 * split last bigram terms with {@link CJKLastUniGramFilter}, and filters
 * stopwords with {@link StopFilter}.
 *
 * <p>An example as below:
 * <pre>
 * CJKLastUniGramAnalyzer queryAnalyzer = new CJKLastUniGramAnalyzer(false);
 * CJKLastUniGramAnalyzer indexAnalyzer = new CJKLastUniGramAnalyzer(true);
 * </pre>
 */
public final class CJKLastUniGramAnalyzer extends StopwordAnalyzerBase {
    private final boolean tokenizeLastUni;

    /**
     * Builds an analyzer which removes words in {@link CJKAnalyzer#getDefaultStopSet()}.
     *
     * @param tokenizeLastUni
     *          flag to tokenize last charcter. set this to false when search
     */
    public CJKLastUniGramAnalyzer(boolean tokenizeLastUni){
        this(CJKAnalyzer.getDefaultStopSet(), tokenizeLastUni);
    }

    /**
     * Builds an analyzer with the given stop words
     *
     * @param stopwords
     *          a stopword set
     * @param tokenizeLastUni
     *          flag to tokenize last charcter. set this to false when search
     */
    public CJKLastUniGramAnalyzer(CharArraySet stopwords, boolean tokenizeLastUni){
        super(stopwords);
        this.tokenizeLastUni = tokenizeLastUni;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new CJKWidthFilter(source);
        result = new LowerCaseFilter(result);
        result = new CJKBigramFilter(result);
        result = new CJKLastUniGramFilter(result, tokenizeLastUni);
        return new TokenStreamComponents(source, new StopFilter(result, stopwords));
    }
}
