package GUI;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JFXyelper extends Application {
    private AnchorPane coreWindowLayout;
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("coreWindow.fxml"));
        coreWindowLayout = fxmlLoader.load();

        // Give the controller access to the main application
        coreWindowController controller = fxmlLoader.getController();

        // Show the scene containing the coreWindow layout

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(919);
        primaryStage.setTitle("Yelpme");
        primaryStage.setScene(new Scene(coreWindowLayout));
        primaryStage.show();
    }


    public static void main(String[] args)  {
        launch(args);
    }
}
