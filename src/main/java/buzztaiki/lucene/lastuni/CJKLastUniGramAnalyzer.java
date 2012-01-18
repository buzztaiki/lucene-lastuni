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

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.util.Version;


/**
 * An {@link Analyzer} that tokenizes text with {@link CJKTokenizer} and
 * filters with {@link CJKLastUniGramFilter} and {@link StopFilter}
 *
 */
public final class CJKLastUniGramAnalyzer extends StopwordAnalyzerBase {
    /**
     * Builds an analyzer which removes words in {@link CJKAnalyzer#getDefaultStopSet()}.
     */
    public CJKLastUniGramAnalyzer(Version matchVersion) {
        this(matchVersion, CJKAnalyzer.getDefaultStopSet());
    }
  
    /**
     * Builds an analyzer with the given stop words
     * 
     * @param matchVersion
     *          lucene compatibility version
     * @param stopwords
     *          a stopword set
     */
    public CJKLastUniGramAnalyzer(Version matchVersion, Set<?> stopwords){
        super(matchVersion, stopwords);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        CJKTokenizer source = new CJKTokenizer(reader);
        return new TokenStreamComponents(
            source,
            new StopFilter(matchVersion, new CJKLastUniGramFilter(new CJKTokenizer(reader)), stopwords));
    }
}
