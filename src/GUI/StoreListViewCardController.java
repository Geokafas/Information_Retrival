package GUI;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import models.Form;

import java.io.IOException;

public class StoreListViewCardController extends ListCell<Form>{

    private FXMLLoader fxmlLoader;

    @FXML
    private Pane card;

    @FXML
    private JFXTextField cardTitle;

    @FXML
    private TextFlow cardText;

    private void loadFXML() {

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("listCard.fxml"));
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

            cardTitle.setText(form.getStore().getStoreName());
            cardText.getChildren().setAll(constructBoldText(form.getBoldText()));

            setText(null);
            setGraphic(card);
        }
    }

    private TextFlow constructBoldText(String text) {
        Text t = new Text();
        Text b  = new Text();
        Text t1  = new Text();

        String[] temp = text.split("<b>");
        t.setText(temp[0]);
        String[] temp1 = temp[1].split("</b>");
        b.setText(temp1[0]);
        t1.setText(temp1[1]);

        //stylize text
        b.setStyle("-fx-font-weight: bold");
        t.setStyle("-fx-font-size: 18");
        t1.setStyle("-fx-font-size: 18");
        TextFlow tf = new TextFlow(t,b,t1);

        return tf;
    }

}
