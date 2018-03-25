package rocks.wallenius.joop.gui.objectdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.StackPane;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.gui.classdiagram.UmlClass;
import rocks.wallenius.joop.gui.classdiagram.UmlObject;
import rocks.wallenius.joop.gui.util.ClassMemberMapperUtil;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.JoopObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ObjectDiagramController implements Initializable {

    private WindowController parentController;

    private Group group;

    @FXML
    StackPane diagram;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new Group();
        diagram.getChildren().add(group);
    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

    public void clear() {
        group.getChildren().clear();
    }

    public void addObjects(List<JoopObject> objects) {
        List<UmlObject> umlObjects = new ArrayList<>();

        int x = 0;
        int y = 0;

        for(JoopObject object : objects){

            final UmlObject umlObject = new UmlObject(x, y, object.getObject().getClass().getSimpleName(), object.getInstanceName());
            // ContextMenu contextMenu = createContextMenu(object);

//            umlObject.setOnContextMenuRequested(event -> {
//                contextMenu.hide();
//                contextMenu.show(umlObject, event.getScreenX(), event.getScreenY());
//            });

            umlObjects.add(umlObject);
            x += 200;
        }

        group.getChildren().addAll(umlObjects);
    }

}
