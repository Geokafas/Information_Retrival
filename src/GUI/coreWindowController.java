package GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import core.luceneSearchEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class coreWindowController implements Initializable {
    private luceneSearchEngine lucene = new luceneSearchEngine();
    private static IndexReader indexReader;
    @FXML
    private JFXButton search_btn ;

    @FXML
    private JFXTextField searchField;

    @FXML
    private ListView<Store> searchView;

    private ObservableList<Store> storeObservableList;

    private static String searchTerms;

    public coreWindowController(){}

    //auth kaleitai apo to FXML arxeio sthn onAction
    @FXML
    protected void searchActionHandler(ActionEvent event) throws IOException
    {
        ArrayList<String> searchFields = new ArrayList<String>();
        //TODO den douleuei opws perimena isws prepei na ginei boolen query. Na valw kai mia special anazhthsh pou tha psaxnei sto kathena 3exwrista
        searchFields.add("name");
        searchFields.add("review_text");
        searchFields.add("tip_text");
        searchFields.add("categories");

        if (event.getSource() == search_btn)
        {

            searchTerms = searchField.getText();
            System.out.println("search Terms: " + searchTerms);

            luceneSearch(searchFields, searchTerms);
            searchField.setText(""); //clear field
        }
    }


    //TODO na valw asterakia san eikonidio aristera kai na taxinomo me vash ta stars. Alla tha prepei na alla3w to ObservableList se kt allo.
    private void populateSearchResultsListView(List<Document> docs) {

        //if(!searchView.getItems().isEmpty()){cleanListView();}
        storeObservableList = FXCollections.observableArrayList();
        for(int i=0; i<docs.size(); i++) {
            Document d = docs.get(i);
            String name = d.get("name");
            int stars = Integer.valueOf(d.get("stars"));
            String review_text = d.get("review_text");
            String tip_text = d.get("tip_text");
            System.out.println("Name: " + name + " stars: " + stars);
            storeObservableList.add(new Store(name,review_text,tip_text,stars));
            searchView.setItems(storeObservableList);
            //searchView.setCellFactory(storeListView -> new StoreListViewCell());
            //searchView.getItems().add(VBox);
        }
    }


    //TODO prepei h anazhthsh na sundiazei ta queries ama einai panw apo ena
    //TODO na se allo packet
    private void luceneSearch(ArrayList<String> inField, String searchTerms) {
        ExecutorService executor = Executors.newCachedThreadPool();

        //called from a different thread so the system wont be unresponsive

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

    @Override
    public void initialize(URL location, ResourceBundle resources){
        System.out.println("INITIALIZED");
//        searchView.setItems(storeObservableList);
        searchView.setCellFactory(storeListView -> new StoreListViewCell());
    }

    private void cleanListView() {
        searchView.getItems().setAll();
    }

}
