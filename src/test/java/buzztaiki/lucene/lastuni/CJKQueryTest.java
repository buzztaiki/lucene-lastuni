package buzztaiki.lucene.lastuni;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;


public class CJKQueryTest extends LuceneTestCase {
    private IndexWriter newWriter(Directory dir, Analyzer analyzer) throws IOException {
        return new IndexWriter(dir,
            new IndexWriterConfig(TEST_VERSION_CURRENT, analyzer)
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
        QueryParser qp = new QueryParser(
            TEST_VERSION_CURRENT,
            "content",
            analyzer);
        qp.setDefaultOperator(QueryParser.AND_OPERATOR);
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


    private RAMDirectory dir;
    private CJKLastUniGramAnalyzer analyzer;
    private CJKLastUniGramAnalyzer uniAnalyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dir = new RAMDirectory();
        analyzer = new CJKLastUniGramAnalyzer(TEST_VERSION_CURRENT, false);
        uniAnalyzer = new CJKLastUniGramAnalyzer(TEST_VERSION_CURRENT, true);
    }

    @Override
    public void tearDown() throws Exception {
        dir.close();
        super.tearDown();
    }

    public void testあいう() throws Exception {
        addDoc(dir, uniAnalyzer, "あいう");
        assertEquals(1, search(dir, analyzer, "あいう").totalHits);
    }

    public void testあいうえお() throws Exception {
        addDoc(dir, uniAnalyzer, "あいうえお");
        assertEquals(1, search(dir, analyzer, "あいう").totalHits);
        //assertEquals(1, search(dir, analyzer, "あ").totalHits);
        //assertEquals(1, search(dir, analyzer, "い").totalHits);
        //assertEquals(1, search(dir, analyzer, "う").totalHits);
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
        assertEquals(0, search(dir, analyzer, "森").totalHits);
        assertEquals(1, search(dir, analyzer, "森?").totalHits);
        assertEquals(1, search(dir, analyzer, "森*").totalHits);
    }
}
