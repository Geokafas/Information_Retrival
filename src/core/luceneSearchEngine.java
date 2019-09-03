package core;

import models.Form;
import models.Review;
import models.Store;
import models.Tip;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class luceneSearchEngine {

    private Analyzer analyzer;
    private String indexPath = "D:/documents/intellijProjects/luceneResults";
    private final int hitsPerPage = 10;
    private List<String> businessIDsFound;
    private HashMap<String,ArrayList<Long>> reviewsIDsFound;
    private HashMap<String,ArrayList<Long>> tipsIDsFound;

    private dataBaseReader fetchFromdb;

    private ArrayList<Review> dbReviews;
    private ArrayList<Tip> dbTips;
    private ArrayList<Store> dbStores;

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

            Sort sort = new Sort(SortField.FIELD_SCORE,
                    new SortField("review_stars", SortField.Type.INT));

            topDocs = searcher.search(query, hitsPerPage,sort,true);
        }

        businessIDsFound = new ArrayList<>();
        reviewsIDsFound = new HashMap<>();
        tipsIDsFound = new HashMap<>();

        for (ScoreDoc top : topDocs.scoreDocs) {
            businessIDsFound.add(searcher.doc(top.doc).get("business_id"));
            //afou tha ta epistrefw ola ta reviews?? ti to thelw?
            //prepei na vrw kapws se pia tips/reviews exei vrei ta queries pou anazhthsa kai oxi mono se pio document! searchAfter??
            //reviewsIDsFound.put(searcher.doc(top.doc).get("business_id"), new ArrayList<>().add(Integer.parseInt(searcher.doc(top.doc).get("review_id"))));//pos in the array of the business with the id in the key field of the hash
            //tipsIDsFound.put(searcher.doc(top.doc).get("business_id"), Integer.parseInt(searcher.doc(top.doc).get("tips_id")));//pos in the array
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
            System.out.println(doc.doc + "\t" + searcher.doc(doc.doc).get("name")
                    + "\t" + searcher.doc(doc.doc).get("review_text"));
        }

        fetchFromdb = new dataBaseReader();
        ArrayList<Form> results = new ArrayList<>();
        ArrayList<Store> stores = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        ArrayList<Tip> tips = new ArrayList<>();

        //get review objects from db that antapokrinontai sta search resaults
        //gia ola ta business_id pou exei epistrepsei h anazhthsh, kane m antikeimena me tips reviews kai stores
        //gia na ta pusharw ston xrhth
        for(String id : businessIDsFound){
            for(int i=0; i<fetchFromdb.databaseBusinessesFetcher().size(); i++){
                if(fetchFromdb.databaseBusinessesFetcher().get(i).getBusiness_id().equals(id)) {
                    stores.add(fetchFromdb.databaseBusinessesFetcher().get(i));
                    String highlight = businessHighlight.get(id);
                    results.add(new Form(fetchFromdb.databaseBusinessesFetcher().get(i), highlight));
                }
            }
//            for(long r_id : reviewsIDsFound.get(id)){
//                reviews.add(fetchFromdb.databaseReviewsFetcher().get((int) r_id));
//            }
//            for(long t_id : tipsIDsFound.get(id)){
//                tips.add(fetchFromdb.databaseTipsFetcher().get((int) t_id));
//            }

        }
        return results;
    }

}
