package rocks.wallenius.joop.controller;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;

/**
 * Created by philipwallenius on 23/05/2016.
 */
public class ObjectViewController {

    private StackPane canvas;
    private Group group;

    void setCanvas(StackPane canvas) {
        this.canvas = canvas;
        canvas.getChildren().add(group);
    }

}
