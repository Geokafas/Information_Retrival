package GUI;

import com.sun.javafx.tk.Toolkit;
import core.luceneSearchEngine;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class coreWindowController{
    private luceneSearchEngine lucene = new luceneSearchEngine();
    @FXML
    private Button search_btn ;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Document> searchView;
    @FXML
    private CheckBox chbx_name;
    @FXML
    private CheckBox chbx_stars;
    @FXML
    private CheckBox chbx_categories;
    @FXML
    private CheckBox chbx_reviewStars;
    @FXML
    private CheckBox chbx_reviewCount;
    @FXML
    private CheckBox chbx_reviewText;
    @FXML
    private CheckBox chbx_tipText;

    private static String searchTerms;
    private ObservableList<Document> results = FXCollections.observableArrayList();
    private ArrayList<String> searchFields;

    //auth kaleitai apo to FXML arxeio sthn onAction
    @FXML
    protected void searchActionHandler(ActionEvent event)
    {
        if (event.getSource() == search_btn)
        {
            searchTerms = searchField.getText();
            System.out.println("search Terms: " + searchTerms);
            //if no fiels is selected then default to reviewText
            if(searchFields.isEmpty()){
                searchFields.add("review_text");
            }
            luceneSearch(searchFields, searchTerms);
            searchField.setText(""); //clear field
        }else{
            searchFields = getCheckBoxValues(event);
        }
    }


    //TODO na valw asterakia san eikonidio aristera kai na taxinomo me vash ta stars. Alla tha prepei na alla3w to ObservableList se kt allo.
    private void populateSearchResultsTreeView(List<Document> docs) {
        results.addAll(docs);
        searchView.setItems(results);
    }


    //TODO prepei h anazhthsh na sundiazei ta queries ama einai panw apo ena
    private void luceneSearch(ArrayList<String> inField, String searchTerms){
        Task task = new Task(){
            @Override
            protected Object call() throws Exception {
                System.out.println("mphka");
                List<Document> docs = lucene.search(inField.get(0),searchTerms);
                System.out.println(docs);
                populateSearchResultsTreeView(docs);
                return null;
            }
        };
        new Thread(task).start();
    }

    private ArrayList<String> getCheckBoxValues(ActionEvent value){
        ArrayList<String> values = new ArrayList<>(7);
        if(value.getSource() == chbx_name) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_stars) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_categories) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewStars) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewCount) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_reviewText) {
            values.add("name");
            System.out.println(value);
        }else if(value.getSource() == chbx_tipText) {
            values.add("name");
            System.out.println(value);
        }
        return values;
    }

}
