package models;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import models.Form;

import java.io.IOException;

public class StoreListViewCell extends ListCell<Form> {

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
    protected void updateItem(Form form, boolean b) {
        super.updateItem(form, b);
        if(b || form == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                loadFXML();
            }

            cellTitle.setText(form.getStore().getStoreName());
            cellReviewText.setText(form.getBoldText());
            cellTipText.setText(form.getBoldText());

            setText(null);
            setGraphic(cell);
        }
    }
}
