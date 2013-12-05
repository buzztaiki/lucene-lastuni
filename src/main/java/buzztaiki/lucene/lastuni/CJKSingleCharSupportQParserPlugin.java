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

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;

/**
 * [WIP] A Solr QueryParser Plugin that support {@link CJKSingleCharQuery}.
 */
public class CJKSingleCharSupportQParserPlugin extends LuceneQParserPlugin {
    public static String NAME = "cjksingle";

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return new LocalQParser(qstr, localParams, params, req);
    }
    
    private static class LocalQParser extends QParser {
        public LocalQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
            super(qstr, localParams, params, req);
        }
    
        @Override
        public Query parse() throws ParseException {
            String qstr = getString();

            String defaultField = getParam(CommonParams.DF);
            if (defaultField==null) {
                defaultField = getReq().getSchema().getDefaultSearchFieldName();
            }
            SolrQueryParser lparser = new LocalQueryParser(this, defaultField);

            // these could either be checked & set here, or in the SolrQueryParser constructor
            String opParam = getParam(QueryParsing.OP);
            if (opParam != null) {
                lparser.setDefaultOperator("AND".equals(opParam) ? QueryParser.Operator.AND : QueryParser.Operator.OR);
            } else {
                // try to get default operator from schema
                QueryParser.Operator operator = getReq().getSchema().getSolrQueryParser(null).getDefaultOperator();
                lparser.setDefaultOperator(null == operator ? QueryParser.Operator.OR : operator);
            }

            return lparser.parse(qstr);
        }
    }


    private static class LocalQueryParser extends SolrQueryParser {
        public LocalQueryParser(QParser parser, String defaultField) {
            super(parser, defaultField);
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

}
