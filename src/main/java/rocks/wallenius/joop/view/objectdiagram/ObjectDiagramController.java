package rocks.wallenius.joop.view.objectdiagram;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import rocks.wallenius.joop.view.View;
import rocks.wallenius.joop.view.classdiagram.Parameter;
import rocks.wallenius.joop.view.objectdiagram.dialog.MethodDialog;
import rocks.wallenius.joop.view.objectdiagram.dialog.MethodParameters;
import rocks.wallenius.joop.view.util.ClassStringFormatter;
import rocks.wallenius.joop.model.JoopObject;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ObjectDiagramController implements Initializable {

    private View parentController;

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

    public void setParentController(View parentController) {
        this.parentController = parentController;
    }

    private void clear() {
        pane.getChildren().clear();
    }

    private void addObjects(List<JoopObject> objects) {

        for(JoopObject object : objects){

            final UmlObject umlObject = new UmlObject(object.getObject().getClass().getSimpleName(), object.getReference(), ObjectUmlMapperUtil.getProperties(object.getObject()));
            ContextMenu contextMenu = createContextMenu(object.getObject());

            umlObject.setOnContextMenuRequested(event -> {
                contextMenu.hide();
                contextMenu.show(umlObject, event.getScreenX(), event.getScreenY());
            });

            umlObject.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            pane.getChildren().add(umlObject);
        }
    }

    public void updateView(List<JoopObject> objects) {
        clear();
        addObjects(objects);
    }

    private ContextMenu createContextMenu(Object object) {
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        for(Method method : ObjectUmlMapperUtil.getMethods(object)) {

            MenuItem item = new MenuItem(String.format("%s(%s)", method.getName(), ClassStringFormatter.formatParameters(method.getParameters())));
            item.setOnAction(event -> invokeMethod(object, method));
            menuItems.add(item);

        }

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

    private void invokeMethod(Object object, Method method) {

        List<Parameter> params = Arrays.stream(method.getParameters()).map(parameter -> new Parameter(parameter.getName(), parameter.getType())).collect(Collectors.toList());

        MethodParameters methodParameters = null;
        if(params.size() > 0) {
            MethodDialog dialog = new MethodDialog(params.toArray(new Parameter[params.size()]));
            Optional<MethodParameters> result = dialog.showAndWait();
            if(result.isPresent()) {
                methodParameters = result.get();
            } else {
                return;
            }
        }

        Class[] parameters;
        Object[] arguments;
        if(methodParameters != null) {
            int numberOfArguments = methodParameters.getArguments().size();
            parameters = methodParameters.getParameters().toArray(new Class[numberOfArguments]);
            arguments = methodParameters.getArguments().toArray(new Object[numberOfArguments]);
        } else {
            parameters = new Class[0];
            arguments = new Object[0];
        }

        Object returnValue = parentController.invokeMethod(object, method.getName(), parameters, arguments);

        System.out.println("Method return type: " + method.getReturnType());

        if(!method.getReturnType().equals("void")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, returnValue != null ? returnValue.toString() : "null");
            alert.setTitle("Return value");
            alert.setHeaderText("Method returned");
            alert.showAndWait();
        }


    }

}
