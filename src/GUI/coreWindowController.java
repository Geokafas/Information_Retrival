package GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import core.luceneSearchEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.Form;
import models.StoreListViewCell;
import org.apache.lucene.index.IndexReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class coreWindowController {
    private luceneSearchEngine lucene = new luceneSearchEngine();
    private static IndexReader indexReader;
    @FXML
    private JFXButton search_btn ;

    @FXML
    private JFXTextField searchField;

    @FXML
    private ListView<Form> searchView;

    private ObservableList<Form> storeObservableList;

    private static String searchTerms;

    public coreWindowController(){}

    //auth kaleitai apo to FXML arxeio sthn onAction
    @FXML
    protected void searchActionHandler(ActionEvent event)
    {
        ArrayList<String> searchFields = new ArrayList<String>();
        //TODO den douleuei opws perimena isws prepei na ginei boolen query. Na valw kai mia special anazhthsh pou tha psaxnei sto kathena 3exwrista
        searchFields.add("name");
        searchFields.add("review_text");
        searchFields.add("tip_text");
//        searchFields.add("categories");

        if (event.getSource() == search_btn)
        {

            searchTerms = searchField.getText();
            System.out.println("search Terms: " + searchTerms);

            luceneSearch(searchFields, searchTerms);
            searchField.setText(""); //clear field
        }
    }


    //TODO na valw asterakia san eikonidio aristera kai na taxinomo me vash ta stars. Alla tha prepei na alla3w to ObservableList se kt allo.
    private void populateSearchResultsListView(List<Form> docs) {
        //to obervableList kapws sundeete me to listview kai mporw na ftiaxnw ena tetio antikeimeno kai na to kanw set sto serchView list

        if (!searchView.getItems().isEmpty()) {
            cleanListView();
        }

        storeObservableList = FXCollections.observableArrayList();
        storeObservableList.addAll(docs);

        searchView.setItems(storeObservableList);
        searchView.setCellFactory(storeListView -> new StoreListViewCell());
    }

    private void luceneSearch(ArrayList<String> inField, String searchTerms) {
        ExecutorService executor = Executors.newCachedThreadPool();
        //called from a different thread so the system wont be unresponsive
        Future<List<Form>> future = executor.submit(new Callable<List<Form>>() {
            @Override
            public List<Form> call() throws Exception {

                List<Form> docs = lucene.search(inField,searchTerms);
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

}
