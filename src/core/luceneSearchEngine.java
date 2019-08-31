package core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;


import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class luceneSearchEngine {

    private static Analyzer analyzer;
    private static String indexPath = "D:/documents/intellijProjects/luceneResults";
    private final int hitsPerPage = 10;
    public luceneSearchEngine(){
        makeSearchAnalyzer(); //initializes the analyzer
    }
    private void makeSearchAnalyzer(){
        this.analyzer = new StandardAnalyzer();
    }
    //private Map<String, Analyzer> analyzerMap = new HashMap<>();

    public List<Document> search(ArrayList<String> inField, String intitialQueryString) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException
    {
        TopDocs topDocs;
        Directory indexDirectory;
        IndexReader indexReader;
        IndexSearcher searcher;
        QueryParser queryParser;
        Query query;

        //String searchStringQuery = preProcessQuery(intitialQueryString);

        if(inField.size()>0){
            //sto multifieldquery parse kanw ena lucene query me to opio mporw na psa3w se polla fields le3eis kai fraseis
            queryParser = new MultiFieldQueryParser(inField.toArray(new String[inField.size()]), analyzer);
            queryParser.setDefaultOperator(QueryParser.Operator.AND);

            //etsi dhmiourgw to query mou(me vasei th sunta3h ths lucene) to opio stelnw sto search gia na psa3ei sto table me ta esteiatoria
            //query parser is designed to convert human-entered text to terms.
            query = queryParser.parse(intitialQueryString);
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
            topDocs = searcher.search(query, hitsPerPage);

        }else {
            //default search field
            query = new QueryParser("name", analyzer).parse(intitialQueryString);
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
            topDocs = searcher.search(query, hitsPerPage);
        }

        for (ScoreDoc top : topDocs.scoreDocs) {
            System.out.println((searcher.doc(top.doc).get("name")));
            System.out.println((searcher.doc(top.doc).get("review_text")));
        }

        String[] highlightFields = { "review_text", "tip_text", "name" };
        UnifiedHighlighter highlighter = new UnifiedHighlighter(searcher, analyzer);
        Map<String, String[]> fragments = highlighter.highlightFields(highlightFields, query, topDocs);
        for(Map.Entry<String, String[]> f : fragments.entrySet())
        {
            for (String text : f.getValue()) {
                //System.out.println("!!!THIS IS HIGHLIGHTED:  " + text);
            }
        }
        System.out.println("!This is the query:     " + query + " ");
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
