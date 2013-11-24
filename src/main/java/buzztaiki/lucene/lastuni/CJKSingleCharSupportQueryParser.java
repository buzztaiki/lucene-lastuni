package buzztaiki.lucene.lastuni;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * A QueryParser that support {@link CJKSingleCharQuery}.
 *
 * @see CJKSingleCharQuery
 * @see QueryParser
 * @todo use flexible parser?
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
