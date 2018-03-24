package rocks.wallenius.joop.gui.classdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import rocks.wallenius.joop.gui.util.ClassMemberMapperUtil;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.model.entity.JoopClass;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ClassDiagramController implements Initializable {

    private WindowController parentController;

    private Group group;

    @FXML
    StackPane diagram;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new Group();
        diagram.getChildren().add(group);
    }

    public void draw() {
        group.getChildren().clear();

        int x = 0;
        int y = 0;

        for(JoopClass clazz : parentController.getClasses()) {

            Class loadedClass = clazz.getLoadedClass();

            group.getChildren().add(new UmlClass(x, y, clazz.getNameWithoutFileExtension(), ClassMemberMapperUtil.getFields(loadedClass), ClassMemberMapperUtil.getConstructors(loadedClass), ClassMemberMapperUtil.getMethods(loadedClass)));
            x += 150;
        }
    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

}