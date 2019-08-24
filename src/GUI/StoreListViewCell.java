package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;

public class StoreListViewCell extends ListCell<Store> {

    @FXML
    private JTextField cellTitle;

    @FXML
    private JTextArea cellReviewText;

    @FXML
    private JTextArea cellTipText;

    private FXMLLoader fxmlLoader;

    @FXML
    private VBox cell;

    @Override
    protected void updateItem(Store store, boolean b) {

        super.updateItem(store, b);
        System.out.println("I am Called");
        if(b || store == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("listCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            cellTitle.setText("dwdw");
            cellReviewText.setText("dwdw");
            cellTipText.setText("dgg");

            setText(null);
            setGraphic(cell);
        }
    }
}
