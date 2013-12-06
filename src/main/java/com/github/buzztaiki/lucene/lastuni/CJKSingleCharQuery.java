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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.PrefixQuery;

/**
 * A query that matches CJK single character.
 *
 * @see CJKSingleCharSupportQueryParser
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
