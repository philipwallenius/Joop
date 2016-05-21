package rocks.wallenius.joop.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * Main Class
 *
 * Created by philipwallenius on 12/02/16.
 */
public class JoopApplication extends Application {

    private final static String WINDOW_TITLE = "JOOP";
    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(WINDOW_TITLE);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/joop.fxml"));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("css/default_theme.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
