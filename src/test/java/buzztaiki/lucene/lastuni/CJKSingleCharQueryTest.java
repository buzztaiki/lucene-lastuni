package buzztaiki.lucene.lastuni;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.Version;

public class CJKSingleCharQueryTest extends LuceneTestCase {
    private IndexWriter newWriter(Directory dir, Analyzer analyzer) throws IOException {
        return new IndexWriter(dir,
            new IndexWriterConfig(TEST_VERSION_CURRENT, analyzer)
            .setOpenMode(OpenMode.CREATE));
    }

    private void addDoc(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        doc.add(newField("content", content, Field.Store.YES, Field.Index.ANALYZED));
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
        QueryParser qp = new CJKSingleCharSupportQueryParser(TEST_VERSION_CURRENT, "content", analyzer);
        qp.setDefaultOperator(QueryParser.AND_OPERATOR);
        qp.setAutoGeneratePhraseQueries(true);
        qp.setAllowLeadingWildcard(true);
        return qp;
    }

    private TopDocs search(Directory dir, Analyzer analyzer, String query) throws IOException, ParseException {
        IndexSearcher searcher = newSearcher(IndexReader.open(dir));
        try {
            QueryParser qp = newQueryParser(analyzer);
            Query q = qp.parse(query);
            return searcher.search(q, 10);
        } finally {
            searcher.close();
        }
    }


    private RAMDirectory dir;
    private CJKAnalyzer analyzer;
    private CJKLastUniGramAnalyzer uniAnalyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dir = new RAMDirectory();
        analyzer = new CJKAnalyzer(TEST_VERSION_CURRENT);
        uniAnalyzer = new CJKLastUniGramAnalyzer(TEST_VERSION_CURRENT);
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