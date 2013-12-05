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

package buzztaiki.lucene.lastuni;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * A QueryParser that support {@link CJKSingleCharQuery}.
 *
 * @see CJKSingleCharQuery
 * @see QueryParser
 */
public class CJKSingleCharSupportQueryParser extends QueryParser {
    public CJKSingleCharSupportQueryParser(Version matchVersion, String f, Analyzer a) {
        super(matchVersion, f, a);
    }

    @Override
    public Query newTermQuery(Term term) {
        if (CJKSingleCharQuery.allowed(term)) {
            return CJKSingleCharQuery.of(term);
        } else {
            return super.newTermQuery(term);
        }
    }
}
