package core;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class luceneSearchEngine {
    private static StandardAnalyzer analyzer;
    private static String indexPath = "D:/documents/intellijProjects/luceneResults";

    public luceneSearchEngine(){
        makeSearchAnalyzer(); //initializes the analyzer
    }

    private void makeSearchAnalyzer(){
        analyzer = new StandardAnalyzer();
    }

    //TODO prepei na kanw enan tokenizer gia na diavazw ta arxeia me ta esteiatoria kai na ftiaxnw 3exoristes listes gia ta reviews tips kai oti allo xreiastei
    //TODO na diavazei polla arxeia kai na epistrefei ta documents pou periexoun auth t le3h
    public List<Document> search(String inField, String queryString) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException
    {
        Query query = new QueryParser(inField, analyzer).parse(queryString);
        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);

        return Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
            try {
                return searcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, ParseException {
        luceneSearchEngine l = new luceneSearchEngine();
        List<Document> d = l.search("review_text", "pizza");
        System.out.println(d);
    }
}
