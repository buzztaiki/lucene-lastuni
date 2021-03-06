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

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.LuceneTestCase;

public class CJKSingleCharQueryTest extends LuceneTestCase {
    private IndexWriter newWriter(Directory dir, Analyzer analyzer) throws IOException {
        return new IndexWriter(dir,
            new IndexWriterConfig(analyzer)
            .setOpenMode(OpenMode.CREATE));
    }

    private void addDoc(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        doc.add(newTextField("content", content, Field.Store.YES));
        writer.addDocument(doc);
    }

    private void addDoc(Directory dir, Analyzer analyzer, String content) throws IOException {
        IndexWriter writer = newWriter(dir, analyzer);
        try {
            addDoc(writer, content);
            writer.commit();
        } finally {
            writer.close();
        }
    }

    private QueryParser newQueryParser(Analyzer analyzer) {
        // TODO: use flexible parser?
        QueryParser qp = new CJKSingleCharSupportQueryParser("content", analyzer);
        qp.setDefaultOperator(QueryParser.AND_OPERATOR);
        qp.setSplitOnWhitespace(true);
        qp.setAutoGeneratePhraseQueries(true);
        qp.setAllowLeadingWildcard(true);
        return qp;
    }

    private TopDocs search(Directory dir, Analyzer analyzer, String query) throws IOException, ParseException {
        IndexSearcher searcher = newSearcher(DirectoryReader.open(dir));
        try {
            QueryParser qp = newQueryParser(analyzer);
            Query q = qp.parse(query);
            return searcher.search(q, 10);
        } finally {
            searcher.getIndexReader().close();
        }
    }


    private Directory dir;
    private CJKLastUniGramAnalyzer analyzer;
    private CJKLastUniGramAnalyzer uniAnalyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dir = newDirectory();
        analyzer = new CJKLastUniGramAnalyzer(false);
        uniAnalyzer = new CJKLastUniGramAnalyzer(true);
    }

    @Override
    public void tearDown() throws Exception {
        dir.close();
        super.tearDown();
    }

    public void testあいう() throws Exception {
        addDoc(dir, uniAnalyzer, "あいう");
        assertEquals(1, search(dir, analyzer, "あいう").totalHits);
        assertEquals(1, search(dir, analyzer, "あ").totalHits);
        assertEquals(1, search(dir, analyzer, "い").totalHits);
        assertEquals(1, search(dir, analyzer, "う").totalHits);
    }

    public void testあいうえお() throws Exception {
        addDoc(dir, uniAnalyzer, "あいうえお");
        assertEquals(1, search(dir, analyzer, "あいう").totalHits);
        assertEquals(1, search(dir, analyzer, "あ").totalHits);
        assertEquals(1, search(dir, analyzer, "い").totalHits);
        assertEquals(1, search(dir, analyzer, "う").totalHits);
    }

    public void test文書B編集() throws Exception {
        addDoc(dir, uniAnalyzer, "文書B編集");
        assertEquals(1, search(dir, analyzer, "文書B編集").totalHits);
        assertEquals(1, search(dir, analyzer, "文書").totalHits);
        assertEquals(1, search(dir, analyzer, "文書B").totalHits);
        assertEquals(1, search(dir, analyzer, "編集").totalHits);
        assertEquals(1, search(dir, analyzer, "B編集").totalHits);
    }


    public void test柳_あいう() throws Exception {
        addDoc(dir, uniAnalyzer, "柳 あいう");
        assertEquals(1, search(dir, analyzer, "あいう").totalHits);
        assertEquals(1, search(dir, analyzer, "柳").totalHits);
    }

    public void test柳() throws Exception {
        addDoc(dir, uniAnalyzer, "柳");
        assertEquals(1, search(dir, analyzer, "柳").totalHits);
    }

    public void test梅森() throws Exception {
        addDoc(dir, uniAnalyzer, "梅森");
        assertEquals(1, search(dir, analyzer, "森").totalHits);
        assertEquals(0, search(dir, analyzer, "森?").totalHits);
        assertEquals(1, search(dir, analyzer, "森*").totalHits);
    }

    public void test森田() throws Exception {
        addDoc(dir, uniAnalyzer, "森田");
        assertEquals(1, search(dir, analyzer, "森").totalHits);
        assertEquals(1, search(dir, analyzer, "森?").totalHits);
        assertEquals(1, search(dir, analyzer, "森*").totalHits);
    }
}
