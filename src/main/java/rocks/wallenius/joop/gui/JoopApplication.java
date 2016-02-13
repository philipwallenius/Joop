package rocks.wallenius.joop.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by philipwallenius on 12/02/16.
 */
public class JoopApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Window(primaryStage);
    }

}
