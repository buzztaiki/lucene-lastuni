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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * Tokenize last CJK words as uni-gram.
 * <p>
 * Example: "C1C2C3C4 java C5C6" will be segmented to: "C1C2" "C2C3" "C3C4" "C4" "java" "C5C6" "C6".
 * </p>
 */
public final class CJKLastUniGramFilter extends TokenFilter {
    private static enum State {
        INHERIT, UNIGRAM, LAST
    }

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    private char[] lastBuffer;
    private State state;
    private int lastStart;
    private int lastEnd;
    private String lastType;
    private boolean tokenizeLastUni;

    public CJKLastUniGramFilter(TokenStream input, boolean tokenizeLastUni) {
        super(input);
        _reset();
        this.tokenizeLastUni = tokenizeLastUni;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (state == State.LAST) {
            return false;
        }
        if (state == State.UNIGRAM) {
            state = State.INHERIT;
            clearAttributes();
            termAtt.copyBuffer(lastBuffer, 0, lastEnd - lastStart);
            offsetAtt.setOffset(lastStart, lastEnd);
            typeAtt.setType(lastType);
        } else {
            boolean cont = input.incrementToken();
            char[] buffer = termAtt.buffer().clone();
            int start = offsetAtt.startOffset();
            int end = offsetAtt.endOffset();
            String type = typeAtt.type();

            if (lastType.equals(CJKBigramFilter.DOUBLE_TYPE)
                    && lastEnd - lastStart >= 2
                    && (!cont && tokenizeLastUni || start >= lastEnd)) {
                state = (cont ? State.UNIGRAM : State.LAST);
                clearAttributes();
                termAtt.copyBuffer(lastBuffer, lastEnd-lastStart-1, 1);
                offsetAtt.setOffset(lastEnd-1, lastEnd);
                typeAtt.setType(CJKBigramFilter.DOUBLE_TYPE);
                setLastValues(buffer, start, end, type);
            } else {
                if (!cont) {
                    return false;
                }
                state = State.INHERIT;
                setLastValues(buffer, start, end, type);
            }
        }
        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        _reset();
    }

    private void setLastValues(char[] buffer, int start, int end, String type) {
        lastBuffer = buffer;
        lastStart = start;
        lastEnd = end;
        lastType = type;
    }

    private void _reset() {
        state = State.INHERIT;
        setLastValues(null, -1, -1, "");
    }
}
