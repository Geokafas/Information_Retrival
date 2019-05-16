package GUI;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class JFXyelper extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("coreWindow.fxml"));

        Parent root1 = fxmlLoader.load();

        stage.setMinHeight(700);
        stage.setMinWidth(919);
        stage.setTitle("test test dokimi");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public static void main(String[] args)  {
        launch(args);
    }
}
