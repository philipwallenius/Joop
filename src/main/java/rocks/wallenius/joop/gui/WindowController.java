package rocks.wallenius.joop.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.StatusBar;
import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.controller.MainController;
import rocks.wallenius.joop.gui.classdiagram.ClassDiagramController;
import rocks.wallenius.joop.gui.console.ConsoleController;
import rocks.wallenius.joop.gui.objectdiagram.ObjectDiagramController;
import rocks.wallenius.joop.gui.tabs.TabsController;
import rocks.wallenius.joop.gui.dialog.NewClassDialog;
import rocks.wallenius.joop.gui.menubar.MenubarController;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.JoopObject;
import rocks.wallenius.joop.model.entity.Tab;
import rocks.wallenius.joop.gui.toolbar.ToolbarController;

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
public class WindowController implements Initializable {

    @FXML
    TabsController tabsController;

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

    private MainController mainController;

    private ObservableList<Class> classes;
    private ObservableList<JoopObject> objects;


    public WindowController() {
        classes = FXCollections.observableArrayList();
        objects = FXCollections.observableArrayList();
        mainController = new MainController(classes, objects);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tabsController.setParentController(this);
        menubarController.setParentController(this);
        toolbarController.setParentController(this);
        consoleController.setParentController(this);
        classDiagramController.setParentController(this);
        objectDiagramController.setParentController(this);

        // initialize with a closed console
        consoleAndStatusBarContainer.getChildren().remove(console);

        setupUiBindings();
        setupDiagramBindings();
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

            String className = file.getName();

            // standardize name
            if (className.toLowerCase().endsWith(".java")) {
                className = className.substring(0, className.lastIndexOf("."));
            }

            try {
                JoopClass loadedClass = mainController.openClass(className, file);
                tabsController.addTab(loadedClass);
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
        saveClass(tabsController.getActiveTab());
    }

    private void saveAll() {
        for(javafx.scene.control.Tab currentTab : tabsController.getTabs()) {
            saveClass(currentTab);
        }
    }

    private void saveClass(javafx.scene.control.Tab t) {
        Tab tab = (Tab) t;
        try {
            mainController.saveClass(tab.getClazz(), tab.getCodeArea().getText());
            tab.setChanged(false);
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
        if(tabsController.isUnsavedChanges()) {
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
                mainController.compileClasses();
                mainController.loadClasses();

                statusBar.setText("Compilation completed successfully");
                String msg = "Compilation completed successfully";
                consoleController.appendInfo(msg);
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

                // standardize the name of the new class
                if (fullyQualifiedName.toLowerCase().endsWith(".java")) {
                    fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf("."));
                }

                JoopClass createdClass = mainController.createClass(fullyQualifiedName);

                tabsController.addTab(createdClass);

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

    public void closeClass(String fullyQualifiedName) {
        mainController.closeClass(fullyQualifiedName);
    }

    public void stop() {
        tabsController.stop();
    }

    public MenubarController getMenubarController() {
        return menubarController;
    }

    public ToolbarController getToolbarController() {
        return toolbarController;
    }

    public void invokeConstructor(Class clazz, String instanceName, Class[] parameters, Object[] arguments) {
        mainController.invokeConstructor(clazz, instanceName, parameters, arguments);
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

    private void setupDiagramBindings() {
        classes.addListener((ListChangeListener<Class>) c -> {
            classDiagramController.clear();
            classDiagramController.addClasses(classes);
        });
        objects.addListener((ListChangeListener<JoopObject>) c -> {
            objectDiagramController.clear();
            objectDiagramController.addObjects(objects);
        });
    }
}
