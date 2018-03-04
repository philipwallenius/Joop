package rocks.wallenius.joop.controller;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.Tab;

import java.util.List;

/**
 * Created by philipwallenius on 23/05/2016.
 */
public class ClassViewController {

    private StackPane canvas;
    private Group group;
    private List<JoopClass> classes;

    ClassViewController(List<JoopClass> classes) {
        group = new Group();
        this.classes = classes;
    }

    void drawClassDiagram() {

        group.getChildren().clear();

        int x = 0;
        int y = 0;
        int w = 100;
        int h = 100;
        for(JoopClass clazz : classes) {
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
