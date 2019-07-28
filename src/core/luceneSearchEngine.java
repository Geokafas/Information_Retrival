package core;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class luceneSearchEngine {
    private static StandardAnalyzer analyzer;
    private static String indexPath = "D:/documents/intellijProjects/luceneResults";
    private final int hitsPerPage = 10;

    public luceneSearchEngine(){
        makeSearchAnalyzer(); //initializes the analyzer
    }

    private void makeSearchAnalyzer(){
        analyzer = new StandardAnalyzer();
    }


    public List<Document> search(ArrayList<String> inField, String queryString) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException
    {
        //TODO edw na kalupsw kai alles periptwseis

        String querystr = !queryString.isEmpty() ? queryString : "l";
        Query query = null;
        if(inField.size()>0){
            for(int i=0; i<inField.size();i++){

                query = new QueryParser(inField.get(i), analyzer).parse(querystr);
            }
        }else {
            query = new QueryParser("name", analyzer).parse(querystr);
        }

        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, hitsPerPage);

        //this is an array of documents that resulted from the search process
        //like this SoreDoc[] hits = topDocs.scoreDocs;
        //and i map it to a List<>
        return Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
            try {
                //this is a document from the top scoring hits
                return searcher.doc(scoreDoc.doc); //scoreDoc.doc is the id of the document
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

//FOR TESTING PURPOSES
//    public static void main(String[] args) throws IOException, ParseException {
//        luceneSearchEngine l = new luceneSearchEngine();
//        List<Document> d = l.search("review_text", "pizza");
//        System.out.println(d);
//    }
}
