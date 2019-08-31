package GUI;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;

public class StoreListViewCell extends ListCell<Store> {

    @FXML
    private JFXTextField cellTitle;

    @FXML
    private JFXTextArea cellReviewText;

    @FXML
    private JFXTextArea cellTipText;

    private FXMLLoader fxmlLoader;

    @FXML
    private VBox cell;

    public void StoreListViewCell() {
        loadFXML();
    }

    private void loadFXML() {

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("listCell.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Store store, boolean b) {
        super.updateItem(store, b);
        if(b || store == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                loadFXML();
            }

            cellTitle.setText(store.getStoreName());
            cellReviewText.setText(store.getStoreReview());
            cellTipText.setText(store.getStoreTip());

            setText(null);
            setGraphic(cell);
        }
    }
}
