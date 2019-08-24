package core;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.awt.*;
import java.beans.Customizer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class luceneSearchEngine {
    private final boolean WORD = true;
    private final boolean PHRASE = true;

    private static StandardAnalyzer analyzer;

    private static String indexPath = "D:/documents/intellijProjects/luceneResults";
    private final int hitsPerPage = 10;

    public luceneSearchEngine(){
        makeSearchAnalyzer(); //initializes the analyzer
    }

    private void makeSearchAnalyzer(){
        analyzer = new StandardAnalyzer();
    }
    private Map<String, Analyzer> analyzerMap = new HashMap<>();

    public List<Document> search(ArrayList<String> inField, String intitialQueryString) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException
    {
        TopDocs topDocs;
        Directory indexDirectory;
        IndexReader indexReader;
        IndexSearcher searcher;
        QueryParser queryParser = null;
        Query query = null;

        //TODO edw na kalupsw kai alles periptwseis
        String searchStringQuery = preProcessQuery(intitialQueryString);

        if(inField.size()>0){
            //sto multifieldquery parse kanw ena lucene query me to opio mporw na psa3w se polla fields le3eis kai fraseis
            queryParser = new MultiFieldQueryParser(inField.toArray(new String[inField.size()]), analyzer);

            //etsi dhmiourgw to query mou(me vasei th sunta3h ths lucene) to opio stelnw sto search gia na psa3ei sto table me ta esteiatoria
            //query parser is designed to convert human-entered text to terms.
            query = queryParser.parse(searchStringQuery);

            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
            topDocs = searcher.search(query, hitsPerPage);

        }else {
            //default search field
            query = new QueryParser("name", analyzer).parse(searchStringQuery);
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
            topDocs = searcher.search(query, hitsPerPage);
        }



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

    private String preProcessQuery(String tempQuery){

        //check if it is word or phrase
        String[] testArray = tempQuery.split(" ");
        if(testArray.length > 1){ //then its a phrase

            String phrase = '"' + tempQuery + '"';
            System.out.println("testing THIS IS THE ARRAY MADE FROM STRING INPUT: " + phrase);

            return constructSearchQuery(testArray);

        }else { // then its a word
            return constructSearchQuery(tempQuery); //we will work with this from now on
        }
    }

    private String constructSearchQuery(String query){
        System.out.println("mphka sto contruct query gia le3eis");
        System.out.println("!printer: " + query);
        String finalQuery = null;
        String querystr = !query.isEmpty() ? query : "l";
        String wildcardQuery = querystr + '*'; //add wild card so words does not need to match precisely
        String fuzzyQuery = querystr + '~'; //fuzzy search will search for words with similar spelling. ~0.8 for further tuning

        //finalQuery = wildcardQuery;
        finalQuery = querystr;
        System.out.println("!printer: final query:  " + finalQuery);
        return finalQuery;
    }

    private String constructSearchQuery(String[] query) {
        String finalQuery = "";
        for(int i=0; i<query.length;i++){
            finalQuery = finalQuery + " " + query[i];
        }

        System.out.println("THIS IS THE ARRAY IN STRING FORMAT WITH PROXIMITY: " + finalQuery);

        return finalQuery+"";
    }

    private void mapAnalyzers()throws IOException{
        analyzerMap.put("categories", new StandardAnalyzer());

    }


//FOR TESTING PURPOSES
//    public static void main(String[] args) throws IOException, ParseException {
//        luceneSearchEngine l = new luceneSearchEngine();
//        List<Document> d = l.search("review_text", "pizza");
//        System.out.println(d);
//    }
}
