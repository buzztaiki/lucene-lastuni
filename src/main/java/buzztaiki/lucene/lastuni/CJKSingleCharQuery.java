package buzztaiki.lucene.lastuni;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.PrefixQuery;

/**
 * A query that matches CJK single character.
 */
public final class CJKSingleCharQuery {
    private CJKSingleCharQuery() {}
    
    /**
     * Return true if term is CJK single character.
     */
    public static boolean allowed(Term term) {
        if (term.text().length() != 1) {
            return false;
        }
        char c = term.text().charAt(0);
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.BASIC_LATIN || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return false;
        }
        return Character.isLetter(c);
    }

    /**
     * Return query that searches CJK single character.
     * @throws IllegalArgumentException If term is not single CJK character.
     */
    public static Query of(Term term) throws IllegalArgumentException {
        if (!allowed(term)) {
            throw new IllegalArgumentException(term.text() + " is not allowed");
        }
        return new PrefixQuery(term);
    }
}
