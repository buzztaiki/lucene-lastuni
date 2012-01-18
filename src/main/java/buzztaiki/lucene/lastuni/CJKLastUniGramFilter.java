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
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

/**
 * Tokenize last CJK words as uni-gram.
 * <p>
 * Example: "C1C2C3C4 java C5C6" will be segmented to: "C1C2" "C2C3" "C3C4" "C4" "java" "C5C6" "C6".
 * </p>
 */
public final class CJKLastUniGramFilter extends TokenFilter {
    private static enum State {
        NONE, TOKENED, UNI, LAST
    }

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private char[] lastBuffer;
    private State state;
    private int lastStart;
    private int lastEnd;

    public CJKLastUniGramFilter(CJKTokenizer input) {
        super(input);
        state = State.NONE;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (state == State.LAST) {
            return false;
        }

        if (state == State.UNI) {
            state = State.NONE;
            clearAttributes();
            termAtt.copyBuffer(lastBuffer, 0, lastEnd - lastStart);
            offsetAtt.setOffset(lastStart, lastEnd);
        } else {
            boolean continuous = input.incrementToken();
            if (!continuous && state != State.TOKENED) {
                return false;
            }

            char[] buffer = termAtt.buffer().clone();
            int start = offsetAtt.startOffset();
            int end = offsetAtt.endOffset();
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(buffer[0]);
            if (!continuous
                    || ub == Character.UnicodeBlock.BASIC_LATIN
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || end - start == 1) {
                if (state == State.TOKENED) {
                    state = (continuous ? State.UNI : State.LAST);
                    clearAttributes();
                    termAtt.copyBuffer(lastBuffer, 1, 1);
                    offsetAtt.setOffset(lastStart + 1, lastStart + 2);
                } else {
                    state = State.NONE;
                }
                lastBuffer = buffer;
                lastStart = start;
                lastEnd = end;
            } else {
                state = State.TOKENED;
                lastBuffer = termAtt.buffer().clone();
                lastStart = start;
                lastEnd = end;
            }
        }
        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        state = State.NONE;
    }
}
