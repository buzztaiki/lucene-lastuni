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

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * Creates new instances of {@link CJKLastUniGramFilter} for Solr.
 *
 * <p>An example as below:
 * <pre>
 * &lt;fieldType name="text_cjk" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;
 *     &lt;filter class="solr.CJKWidthFilterFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *     &lt;filter class="solr.CJKBigramFilterFactory"
 *       han="true" hiragana="true"
 *       katakana="true" hangul="true" outputUnigrams="false" /&gt;
 *     &lt;filter class="buzztaiki.lucene.lastuni.CJKLastUniGramFilterFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;
 * </pre>
 */
public final class CJKLastUniGramFilterFactory extends TokenFilterFactory {
    public CJKLastUniGramFilterFactory(Map<String,String> args) {
        super(args);
    }

    @Override
    public CJKLastUniGramFilter create(TokenStream in) {
        return new CJKLastUniGramFilter(in);
    }
}
