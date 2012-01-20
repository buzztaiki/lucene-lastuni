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

import java.io.IOException;

import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

/**
 * Creates new instances of {@link CJKLastUniGramFilter} for Solr.
 *
 * <p>An example as below:
 * <pre>
 * &lt;fieldType name="text_cjk" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.CJKTokenizerFactory"/&gt;
 *     &lt;filter class="buzztaiki.lucene.lastuni.CJKLastUniGramFilterFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;
 * </pre>
 */
public final class CJKLastUniGramFilterFactory extends BaseTokenFilterFactory {
  public CJKLastUniGramFilter create(TokenStream in) {
      if (!(in instanceof CJKTokenizer)) {
          throw new IllegalArgumentException("Intput token stream should be CJKTokenizer");
      }
      return new CJKLastUniGramFilter((CJKTokenizer) in);
  }
}
