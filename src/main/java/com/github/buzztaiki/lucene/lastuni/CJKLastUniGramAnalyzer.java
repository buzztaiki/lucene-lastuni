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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;


/**
 * An {@link Analyzer} that tokenizes text with {@link StandardTokenizer},
 * normalizes content with {@link CJKWidthFilter}, folds case with
 * {@link LowerCaseFilter}, forms bigrams of CJK with {@link CJKBigramFilter},
 * split last bigram terms with {@link CJKLastUniGramFilter}, and filters
 * stopwords with {@link StopFilter}.
 *
 * <p>An example as below:
 * <pre>
 * CJKLastUniGramAnalyzer queryAnalyzer = new CJKLastUniGramAnalyzer(Version.LUCENE_CURRENT, false);
 * CJKLastUniGramAnalyzer indexAnalyzer = new CJKLastUniGramAnalyzer(Version.LUCENE_CURRENT, true);
 * </pre>
 */
public final class CJKLastUniGramAnalyzer extends StopwordAnalyzerBase {
    private final boolean tokenizeLastUni;

    /**
     * Builds an analyzer which removes words in {@link CJKAnalyzer#getDefaultStopSet()}.
     *
     * @param matchVersion
     *          lucene compatibility version
     * @param tokenizeLastUni
     *          flag to tokenize last charcter. set this to false when search
     */
    public CJKLastUniGramAnalyzer(Version matchVersion, boolean tokenizeLastUni){
        this(matchVersion, CJKAnalyzer.getDefaultStopSet(), tokenizeLastUni);
    }

    /**
     * Builds an analyzer with the given stop words
     *
     * @param matchVersion
     *          lucene compatibility version
     * @param stopwords
     *          a stopword set
     * @param tokenizeLastUni
     *          flag to tokenize last charcter. set this to false when search
     */
    public CJKLastUniGramAnalyzer(Version matchVersion, CharArraySet stopwords, boolean tokenizeLastUni){
        super(matchVersion, stopwords);
        this.tokenizeLastUni = tokenizeLastUni;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new StandardTokenizer(matchVersion, reader);
        TokenStream result = new CJKWidthFilter(source);
        result = new LowerCaseFilter(matchVersion, result);
        result = new CJKBigramFilter(result);
        result = new CJKLastUniGramFilter(result, tokenizeLastUni);
        return new TokenStreamComponents(source, new StopFilter(matchVersion, result, stopwords));
    }
}
