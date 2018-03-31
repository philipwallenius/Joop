package rocks.wallenius.joop.gui.objectdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.gui.classdiagram.UmlObject;
import rocks.wallenius.joop.model.entity.JoopObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ObjectDiagramController implements Initializable {

    private WindowController parentController;

    @FXML
    ScrollPane diagram;

    FlowPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pane = new FlowPane();
        pane.setHgap(20);
        pane.setVgap(20);
        pane.setPadding(new Insets(20, 20, 20, 20));
        diagram.setContent(pane);
    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

    public void clear() {
        pane.getChildren().clear();
    }

    public void addObjects(List<JoopObject> objects) {

        for(JoopObject object : objects){

            final UmlObject umlObject = new UmlObject(object.getObject().getClass().getSimpleName(), object.getInstanceName());
            // ContextMenu contextMenu = createContextMenu(object);

//            umlObject.setOnContextMenuRequested(event -> {
//                contextMenu.hide();
//                contextMenu.show(umlObject, event.getScreenX(), event.getScreenY());
//            });

            pane.getChildren().add(umlObject);
        }
    }

}
