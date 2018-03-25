package rocks.wallenius.joop.gui.classdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import rocks.wallenius.joop.gui.dialog.NewObject;
import rocks.wallenius.joop.gui.dialog.NewObjectDialog;
import rocks.wallenius.joop.gui.util.ClassMemberMapperUtil;
import rocks.wallenius.joop.gui.WindowController;
import rocks.wallenius.joop.gui.util.ClassStringFormatter;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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

    public void addClasses(List<Class> classes) {
        List<UmlClass> umlClasses = new ArrayList<>();

        int x = 0;
        int y = 0;

        for(Class clazz : classes) {

            final UmlClass umlClass = new UmlClass(x, y, clazz.getSimpleName(), ClassMemberMapperUtil.getFields(clazz), ClassMemberMapperUtil.getConstructors(clazz), ClassMemberMapperUtil.getMethods(clazz));
            ContextMenu contextMenu = createContextMenu(clazz);

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

    private ContextMenu createContextMenu(Class clazz) {
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        for(Constructor constructor : ClassMemberMapperUtil.getConstructors(clazz)) {

            MenuItem item = new MenuItem(String.format("%s(%s)", constructor.getName(), ClassStringFormatter.formatParameters(constructor.getParameters())));
            item.setOnAction(event -> {
                invokeConstructor(clazz, constructor);
            });
            menuItems.add(item);

        }

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

    private void invokeConstructor(Class clazz, Constructor constructor) {

        List<Class> params = Arrays.stream(constructor.getParameters()).map(rocks.wallenius.joop.gui.classdiagram.Parameter::getType).collect(Collectors.toList());

        NewObjectDialog dialog = new NewObjectDialog(clazz.getName(), params.toArray(new Class[params.size()]));

        Optional<NewObject> result = dialog.showAndWait();

        if(result.isPresent()) {
            NewObject newObject = result.get();
            int numberOfArguments = newObject.getArguments().size();
            parentController.invokeConstructor(clazz, newObject.getInstanceName(), newObject.getParameters().toArray(new Class[numberOfArguments]), newObject.getArguments().toArray(new Object[numberOfArguments]));
        }

    }

}
