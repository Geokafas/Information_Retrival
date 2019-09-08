package core;

import GUI.StoreListViewCellController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.Form;
import models.Review;
import models.Store;
import models.Tip;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class luceneSearchEngine {

    private Analyzer analyzer;
    private String indexPath = "indexerOutput";
    private final int hitsPerPage = 10;
    private List<String> businessIDsFound;
    private HashMap<String,ArrayList<Long>> reviewsIDsFound;
    private HashMap<String,ArrayList<Long>> tipsIDsFound;

    private dataBaseReader fetchFromdb;

    private ArrayList<Review> dbReviews;
    private ArrayList<Tip> dbTips;
    private ArrayList<Store> dbStores;

    //queries
    private boolean isBool = false;

    public luceneSearchEngine(){
        makeSearchAnalyzer(); //initializes the analyzer
    }
    private void makeSearchAnalyzer(){
        this.analyzer = new StandardAnalyzer();
    }

    public List<Form> search(ArrayList<String> inField, String intitialQueryString)
            throws IOException, ParseException
    {
        TopDocs topDocs;
        Directory indexDirectory;
        IndexReader indexReader;
        IndexSearcher searcher;
        QueryParser queryParser;
        Query query;

        //String searchStringQuery = preProcessQuery(intitialQueryString);

        if(inField.size()>1){
            //sto multifieldquery parse kanw ena lucene query me to opio mporw na psa3w se polla fields le3eis kai fraseis
            queryParser = new MultiFieldQueryParser(inField.toArray(new String[inField.size()]), analyzer);
            queryParser.setDefaultOperator(QueryParser.Operator.AND);

            //etsi dhmiourgw to query mou(me vasei th sunta3h ths lucene) to opio stelnw sto search gia na psa3ei sto table me ta esteiatoria
            //query parser is designed to convert human-entered text to terms.
            query = queryParser.parse(intitialQueryString);
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
            topDocs = searcher.search(queryProcess(query), hitsPerPage);

        }else {
            //default search field
            System.out.println("mphka sto ena");
            query = new QueryParser(inField.get(0), analyzer).parse(intitialQueryString);
            indexDirectory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(indexReader);
//            Sort sort = new Sort(SortField.FIELD_SCORE,
//                    new SortField("review_stars", SortField.Type.INT));

            topDocs = searcher.search(query, hitsPerPage);
        }

        businessIDsFound = new ArrayList<>();
        for (ScoreDoc top : topDocs.scoreDocs) {
            businessIDsFound.add(searcher.doc(top.doc).get("business_id"));
        }

        HashMap<String, String> businessHighlight = new HashMap<>();
        String[] highlightFields = { "review_text", "tip_text", "name" };
        UnifiedHighlighter highlighter = new UnifiedHighlighter(searcher, analyzer);
        Map<String, String[]> fragments = highlighter.highlightFields(highlightFields, query, topDocs);
        for(Map.Entry<String, String[]> f : fragments.entrySet())
        {
            int i = 0;
            for (String text : f.getValue()) {
                String businessID = businessIDsFound.get(i);
                if (businessHighlight.containsKey(businessID)) {
                    businessHighlight.put(businessID, businessHighlight.get(businessID) + "..." + text);
                } else {
                    businessHighlight.put(businessID, text);
                }
                i++;
            }
        }

        //this is an array of documents that resulted from the search process
        //like this SoreDoc[] hitsdataBaseReader = topDocs.scoreDocs;
        //and i map it to a List<>
//        final List<Document> collect = Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
//            try {
//                //this is a document from the top scoring hits
//                return searcher.doc(scoreDoc.doc); //scoreDoc.doc is the id of the document
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }).collect(Collectors.toList());
        for (ScoreDoc doc : topDocs.scoreDocs) {
            System.out.println("!!!!!!!   "+doc.doc + "\t" + searcher.doc(doc.doc).get("name"));
//            for(IndexableField f : searcher.doc(doc.doc).getFields()){
//                if (f.name().equals("review_text")){
//                    System.out.println("  !!!!!!!!!" + "\n"+f);
//                }
//            }
        }

        fetchFromdb = new dataBaseReader();


        //get review objects from db that antapokrinontai sta search resaults
        //gia ola ta business_id pou exei epistrepsei h anazhthsh, kane m antikeimena me tips reviews kai stores
        //gia na ta pusharw ston xrhth
        ArrayList<Form> results = new ArrayList<>();

        for(String id : businessIDsFound){

            Store store = null;
            ArrayList<Review> reviews = new ArrayList<>();
            ArrayList<Tip> tips = new ArrayList<>();
            String highlight = null;

            for(int i=0; i<fetchFromdb.databaseBusinessesFetcher().size(); i++){
                if(fetchFromdb.databaseBusinessesFetcher().get(i).getBusiness_id().equals(id)) {
                    store = fetchFromdb.databaseBusinessesFetcher().get(i);
                    highlight = businessHighlight.get(id);
                    //results.add(new Form(fetchFromdb.databaseBusinessesFetcher().get(i), highlight));
                }
            }
            ArrayList<Review> tempRev = fetchFromdb.databaseReviewsFetcher();
            System.out.println("REVIEWS SiZE "+tempRev.size());
            for(int i=0; i<tempRev.size(); i++) {
                if(tempRev.get(i).getBusiness_id().equals(id)) {
                    reviews.add(tempRev.get(i));
                }
            }
//            for(long t_id : tipsIDsFound.get(id)){
//                tips.add(fetchFromdb.databaseTipsFetcher().get((int) t_id));
//            }

            results.add(new Form(store, highlight, reviews));
            for (ScoreDoc doc : topDocs.scoreDocs) {
                Tip tip = new Tip(searcher.doc(doc.doc).get("tip_text"),(long) Integer.parseInt(searcher.doc(doc.doc).get("tip_id")),searcher.doc(doc.doc).get("business_id"),searcher.doc(doc.doc).get("date"));
                tips.add(tip);
                //System.out.println("!!!!!!!   " + doc.doc + "\t" + searcher.doc(doc.doc).get("tips"));
            }
        }
        return results;
    }

    private Query queryProcess(Query query){
        System.out.println("query :    "+query.toString());
        System.out.println("Type of query: " + query.getClass().getSimpleName());

        return query;
    }

}
