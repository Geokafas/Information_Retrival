package GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import core.luceneSearchEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import models.Form;
import org.apache.lucene.index.IndexReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class coreWindowController{
    private luceneSearchEngine lucene = new luceneSearchEngine();
    private static IndexReader indexReader;
    @FXML
    private JFXButton search_btn ;

    @FXML
    private JFXTextField searchField;

    @FXML
    private ListView<Form> searchView;

    @FXML
    private JFXCheckBox doBoolQuery;

    @FXML
    private StackPane stackedPane;

    @FXML
    private TabPane storeDetailPane;

    @FXML
    private JFXTextField cellTitle;

    @FXML
    private JFXTextArea cellReviewText;

    @FXML
    private JFXTextArea cellTipText;

    @FXML
    private JFXButton backToSearch ;


    private ObservableList<Form> storeObservableList;

    private static String searchTerms;

    protected Node searchPage;
    protected Node detailsPage;


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
        searchFields.add("categories");

        if (event.getSource() == search_btn)
        {

            searchTerms = searchField.getText();
            System.out.println("search Terms: " + searchTerms);

            luceneSearch(searchFields, searchTerms);
            searchField.setText(""); //clear field
        }

        if(event.getSource()==doBoolQuery){
            if (doBoolQuery.isSelected()){
                setBooleanQueryView(true);
            }else if (!doBoolQuery.isSelected()){
                setBooleanQueryView(false);
            }
        }

        if (event.getSource() == backToSearch) {
            backToSearchPane();
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

        searchView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + searchView.getSelectionModel().getSelectedItem().getStore().getBusiness_id());
                loadDetails(docs.get(0));
                changePage();
            }
        });

        searchView.setCellFactory(storeListView -> {
            return new StoreListViewCardController();
        });

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

    private void setBooleanQueryView(boolean flag){
        if(flag){
            searchField.setText("name: categories: ");
        }
        else {
            searchField.setText("");
        }
    }

    public void changePage() {
        searchPage = stackedPane.getChildren().get(1);
        stackedPane.getChildren().remove(1);
    }

    public void backToSearchPane(){
        detailsPage  = stackedPane.getChildren().get(0);
        stackedPane.getChildren().set(0,detailsPage);
        stackedPane.getChildren().add(1,searchPage);
    }

    public void loadDetails(Form form){
        cellTitle.setText(form.getStore().getStoreName());
        cellReviewText.setText(form.getReviews().get(0).getReviewText());
        for(int i =1; i<form.getReviews().size(); i++) {
            cellReviewText.appendText(form.getReviews().get(i).getReviewText());
        }
        cellReviewText.setText("tip text");
//        for(int i =1; i<form.getTips().size(); i++) {
//            cellReviewText.appendText(form.getTips().get(i).getTipText());
//        }
    }

}
