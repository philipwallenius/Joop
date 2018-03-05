package rocks.wallenius.joop.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rocks.wallenius.joop.controller.GuiController;

/**
 *
 * Main Class
 *
 * Created by philipwallenius on 12/02/16.
 */
public class JoopApplication extends Application {

    private final static String WINDOW_TITLE = "JOOP";
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;

    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(WINDOW_TITLE);
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/joop.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("css/default_theme.css");
        scene.getStylesheets().add("css/java-keywords.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        GuiController controller = fxmlLoader.getController();
        controller.stop();
    }

}
