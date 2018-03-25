package rocks.wallenius.joop.gui.classdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import rocks.wallenius.joop.gui.util.ClassMemberMapperUtil;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.gui.util.ClassStringFormatter;
import rocks.wallenius.joop.model.entity.JoopClass;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    public void clear() {
        group.getChildren().clear();
    }

    public void addClasses(List<JoopClass> classes) {
        List<UmlClass> umlClasses = new ArrayList<>();

        int x = 0;
        int y = 0;

        for(JoopClass clazz : classes) {

            Class loadedClass = clazz.getLoadedClass();

            final UmlClass umlClass = new UmlClass(x, y, clazz.getNameWithoutFileExtension(), ClassMemberMapperUtil.getFields(loadedClass), ClassMemberMapperUtil.getConstructors(loadedClass), ClassMemberMapperUtil.getMethods(loadedClass));
            ContextMenu contextMenu = createContextMenu(ClassMemberMapperUtil.getConstructors(loadedClass));

            umlClass.setOnContextMenuRequested(event -> {
                contextMenu.hide();
                contextMenu.show(umlClass, event.getScreenX(), event.getScreenY());
            });

            umlClasses.add(umlClass);
            x += 200;
        }


        group.getChildren().addAll(umlClasses);
    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

    private ContextMenu createContextMenu(Constructor[] constructors) {
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();
        for(Constructor constructor : constructors) {
            MenuItem item = new MenuItem(String.format("%s(%s)", constructor.getName(), ClassStringFormatter.formatParameters(constructor.getParameters())));
            item.setOnAction(event -> {
                // Invoke method
//                parentController.invokeConstructor(constructor.getName());
            });
            menuItems.add(item);

        }
        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

}
