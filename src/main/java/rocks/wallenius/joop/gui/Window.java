package rocks.wallenius.joop.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by philipwallenius on 12/02/16.
 */
public class Window {

    private final static String WINDOW_TITLE = "JOOP";
    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 600;

    private Stage primaryStage;
    private CodeView codeView;
    private ClassView classView;
    private ObjectView objectView;

    public Window(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(WINDOW_TITLE);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/joop.fxml"));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("css/default_theme.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
