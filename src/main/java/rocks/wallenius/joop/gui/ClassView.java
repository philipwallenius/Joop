package rocks.wallenius.joop.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

/**
 * Created by philipwallenius on 12/02/16.
 */
public class ClassView extends StackPane {

    public ClassView() {
        getStyleClass().add("class-view");
        Ellipse e = new Ellipse(10, 10, 10, 10);
        getChildren().add(e);
    }

}
