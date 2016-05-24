package rocks.wallenius.joop.controller;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;

import java.util.List;

/**
 * Created by philipwallenius on 23/05/2016.
 */
public class ClassViewController {

    private StackPane canvas;
    private Group group;
    private Model model;

    ClassViewController(Model model) {
        group = new Group();
        this.model = model;
    }

    void drawClassDiagram() {

        group.getChildren().clear();
        List<CustomClass> classes = model.getClasses();
        int x = 0;
        int y = 0;
        int w = 100;
        int h = 100;
        for(CustomClass customClass : classes) {
            Rectangle r = new Rectangle(x, y, w, h);
            x += 150;
            group.getChildren().add(r);
        }
        
    }

    void setCanvas(StackPane canvas) {
        this.canvas = canvas;
        canvas.getChildren().add(group);
    }

}
