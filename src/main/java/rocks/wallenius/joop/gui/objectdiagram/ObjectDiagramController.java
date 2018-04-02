package rocks.wallenius.joop.gui.objectdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.gui.classdiagram.Constructor;
import rocks.wallenius.joop.gui.util.ClassStringFormatter;
import rocks.wallenius.joop.gui.util.ClassUmlMapperUtil;
import rocks.wallenius.joop.gui.util.ObjectUmlMapperUtil;
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

            final UmlObject umlObject = new UmlObject(object.getObject().getClass().getSimpleName(), object.getReference(), ObjectUmlMapperUtil.getProperties(object.getObject()));
            ContextMenu contextMenu = createContextMenu(object.getObject());

            umlObject.setOnContextMenuRequested(event -> {
                contextMenu.hide();
                contextMenu.show(umlObject, event.getScreenX(), event.getScreenY());
            });

            pane.getChildren().add(umlObject);
        }
    }

    private ContextMenu createContextMenu(Object object) {
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        for(Method method : ObjectUmlMapperUtil.getMethods(object)) {

            MenuItem item = new MenuItem(String.format("%s(%s)", method.getName(), ClassStringFormatter.formatParameters(method.getParameters())));
            item.setOnAction(event -> {
                System.out.println("Invoking method!");
            });
            menuItems.add(item);

        }

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

}
