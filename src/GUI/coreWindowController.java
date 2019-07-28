package GUI;

import com.jfoenix.controls.*;

import core.luceneSearchEngine;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.*;

public class coreWindowController{
    private luceneSearchEngine lucene = new luceneSearchEngine();
    private static IndexReader indexReader;
    @FXML
    private JFXButton search_btn ;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXListView<String> searchView;
    @FXML
    private JFXCheckBox chbx_name;
    @FXML
    private JFXCheckBox chbx_stars;
    @FXML
    private JFXCheckBox chbx_categories;
    @FXML
    private JFXCheckBox chbx_reviewStars;
    @FXML
    private JFXCheckBox chbx_reviewCount;
    @FXML
    private JFXCheckBox chbx_reviewText;
    @FXML
    private JFXCheckBox chbx_tipText;

    private static String searchTerms;
    private ObservableList<Document> results = FXCollections.observableArrayList();
    private ArrayList<String> searchFields = new ArrayList<>(Arrays.asList("categories"));

    public coreWindowController(){}

    //auth kaleitai apo to FXML arxeio sthn onAction
    @FXML
    protected void searchActionHandler(ActionEvent event) throws IOException
    {
        //TODO exoun thema lg ta checkboxes. Den dilonentai kathe fora
        if (event.getSource() == search_btn)
        {
            searchTerms = searchField.getText();
            System.out.println("search Terms: " + searchTerms);

            luceneSearch(searchFields, searchTerms);
            searchField.setText(""); //clear field
        }else{
            searchFields = getCheckBoxValues(event);
        }
    }


    //TODO na valw asterakia san eikonidio aristera kai na taxinomo me vash ta stars. Alla tha prepei na alla3w to ObservableList se kt allo.
    private void populateSearchResultsListView(List<Document> docs) {

        if(!searchView.getItems().isEmpty()){cleanListView();}

        for(int i=0; i<docs.size(); i++) {
            Document d = docs.get(i);
            searchView.getItems().add(constructMessages(d));
        }
    }

    private String constructMessages(Document d){
        String name = d.get("name");
        String stars = d.get("stars");

        String listViewMessage = stars + ' ' + name;

        return listViewMessage;
    }

    //TODO prepei h anazhthsh na sundiazei ta queries ama einai panw apo ena
    //TODO na se allo packet
    private void luceneSearch(ArrayList<String> inField, String searchTerms) {
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<List<Document>> future = executor.submit(new Callable<List<Document>>() {
            @Override
            public List<Document> call() throws Exception {

                System.out.println("mphka");
                List<Document> docs = lucene.search(inField,searchTerms);
                for(int i=0; i<docs.size();++i)
                {
                    System.out.println(docs.get(i).get("name"));
                }
                return docs;
            }
        });
        executor.shutdown();
        try {
                populateSearchResultsListView(future.get());
            //System.out.println("result is: " + future.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

    }

    private void cleanListView() {
        searchView.getItems().setAll();
    }

    private ArrayList<String> getCheckBoxValues(ActionEvent value){
        ArrayList<String> values = new ArrayList<>(7);
        if(value.getSource() == chbx_name) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_stars) {
            values.add("stars");
            System.out.println(value);
        }else if(value.getSource() == chbx_categories) {
            values.add("categories");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewStars) {
            values.add("review_stars");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewCount) {
            values.add("review_count");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewText) {
            values.add("review_text");
            System.out.println(value);
        }else if(value.getSource() == chbx_tipText) {
            values.add("tip_text");
            System.out.println(value);
        }
        return values;
    }

}
