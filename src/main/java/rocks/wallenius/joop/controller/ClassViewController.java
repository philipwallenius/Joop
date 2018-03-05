package rocks.wallenius.joop.controller;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import rocks.wallenius.joop.gui.diagram.UmlClass;
import rocks.wallenius.joop.model.entity.JoopClass;

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

        for(JoopClass clazz : classes) {
            group.getChildren().add(new UmlClass(x, y, clazz));
            x += 150;
        }
        
    }

    void setCanvas(StackPane canvas) {
        this.canvas = canvas;
        canvas.getChildren().add(group);
    }

}
