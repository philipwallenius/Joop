package rocks.wallenius.joop.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.StatusBar;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.Source;
import rocks.wallenius.joop.controller.CompilationException;
import rocks.wallenius.joop.controller.Controller;
import rocks.wallenius.joop.view.classdiagram.ClassDiagramController;
import rocks.wallenius.joop.view.console.ConsoleController;
import rocks.wallenius.joop.view.objectdiagram.ObjectDiagramController;
import rocks.wallenius.joop.view.editor.EditorController;
import rocks.wallenius.joop.view.dialog.NewClassDialog;
import rocks.wallenius.joop.view.menubar.MenubarController;
import rocks.wallenius.joop.view.toolbar.ToolbarController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * MVC GuiController
 * <p>
 * Created by philipwallenius on 14/02/16.
 */
public class JavaFXViewImpl implements Initializable {

    @FXML
    EditorController editorController;

    @FXML
    StatusBar statusBar;

    @FXML
    VBox consoleAndStatusBarContainer;

    @FXML
    MenubarController menubarController;

    @FXML
    ToolbarController toolbarController;

    @FXML
    VBox console;

    @FXML
    ConsoleController consoleController;

    @FXML
    ClassDiagramController classDiagramController;

    @FXML
    ObjectDiagramController objectDiagramController;

    private Controller controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        editorController.setParentController(this);
        menubarController.setParentController(this);
        toolbarController.setParentController(this);
        consoleController.setParentController(this);
        classDiagramController.setParentController(this);
        objectDiagramController.setParentController(this);

        // initialize with a closed console
        consoleAndStatusBarContainer.getChildren().remove(console);

        setupUiBindings();
    }

    /**
     * Handler for open button
     */
    @FXML
    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Class");
        FileChooser.ExtensionFilter extFilterSvg = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java", "*.JAVA");
        fileChooser.getExtensionFilters().addAll(extFilterSvg);
        File file = fileChooser.showOpenDialog(getWindow());
        if (file != null) {

            try {
                Source source = controller.open(file);
                editorController.open(source);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load class").showAndWait();
            }
        }
    }

    /**
     * Handler for save button
     */
    @FXML
    public void save() {
        save(editorController.getActiveSource());
    }

    private void save(Source source) {
        try {
            controller.save(source);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to save class").showAndWait();
        }
    }

    private void saveAll() {
        try {
            controller.saveAll();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to save class").showAndWait();
        }

    }

    /**
     * Handler for compile button
     */
    @FXML
    public void compileClasses() {

        boolean proceed = true;
        if(controller.hasAnySourceUnsavedChanges()) {
            proceed = false;
            Optional<ButtonType> choice = promptUnsavedChanges();

            if(choice.isPresent() && choice.get() == ButtonType.OK) {
                saveAll();
                proceed = true;
            }
        }

        if(proceed) {
            statusBar.setText("Compiling...");
            consoleController.clear();

            try {
                controller.compile();
            } catch (CompilationException compilationException) {

                menubarController.openConsole();
                consoleController.appendError(compilationException.getCompilationExceptionMessage());
                statusBar.setText("Unable to compile classes");

            } catch (IOException ioException) {
                new Alert(Alert.AlertType.ERROR, "Unable to compile classes").showAndWait();
                statusBar.setText("Unable to compile classes");
            } catch(ClassNotFoundException cnfException) {
                new Alert(Alert.AlertType.ERROR, "Unable to load compiled classes").showAndWait();
                statusBar.setText("Unable to load compiled classes");
            }

            statusBar.setText("Compilation completed successfully");
            String msg = "Compilation completed successfully";
            consoleController.appendInfo(msg);
        }

    }

    /**
     * Handler for new button
     */
    @FXML
    public void create() {
        Optional<String> input = promptClassName();

        if (input.isPresent()) {
            try {

                String fullyQualifiedName = input.get();

                Source source = controller.create(fullyQualifiedName);

                editorController.open(source);

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to create new class").showAndWait();
            }
        }
    }

    @FXML
    public void closeConsole() {
        menubarController.closeConsole();
    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }

    public void updateView(Model model) {
        this.classDiagramController.updateView(model.getClasses());
        this.objectDiagramController.updateView(model.getObjects());
    }

    public void close(Source source) {
        this.controller.close(source);
    }

    public void stop() {
        editorController.stop();
    }

    public void setActive(Source source) {
        if (source != null) {
            getToolbarController().getButtonCompile().setDisable(false);
            getMenubarController().getMenuItemCompile().setDisable(false);
        } else {
            getToolbarController().getButtonCompile().setDisable(true);
            getMenubarController().getMenuItemCompile().setDisable(true);
        }
    }

    public MenubarController getMenubarController() {
        return menubarController;
    }

    public ToolbarController getToolbarController() {
        return toolbarController;
    }

    public void invokeConstructor(Class clazz, String instanceName, Class[] parameters, Object[] arguments) {
        controller.invokeConstructor(clazz, instanceName, parameters, arguments);
    }

    public Object invokeMethod(Object object, String methodName, Class[] parameters, Object[] arguments) {
        return controller.invokeMethod(object, methodName, parameters, arguments);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private Window getWindow() {
        return statusBar.getScene().getWindow();
    }

    private Optional<String> promptClassName() {
        NewClassDialog newClassDialog = new NewClassDialog((Stage) getWindow());
        return newClassDialog.getValue();
    }

    private Optional<ButtonType> promptUnsavedChanges() {
        return new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the changes to all open classes?").showAndWait();
    }

    private void setupUiBindings() {
        // toggle console window depending on View menu Console item
        menubarController.getMenuItemConsole().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                consoleAndStatusBarContainer.getChildren().add(0, console);
            } else {
                consoleAndStatusBarContainer.getChildren().remove(console);
            }
        });
    }

}
