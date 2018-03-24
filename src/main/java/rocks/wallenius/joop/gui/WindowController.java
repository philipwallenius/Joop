package rocks.wallenius.joop.gui;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.controller.MainController;
import rocks.wallenius.joop.gui.classdiagram.ClassDiagramController;
import rocks.wallenius.joop.gui.console.ConsoleController;
import rocks.wallenius.joop.gui.objectdiagram.ObjectDiagramController;
import rocks.wallenius.joop.oldgui.dialog.NewDialog;
import rocks.wallenius.joop.oldgui.syntaxhighlight.SyntaxHighlighter;
import rocks.wallenius.joop.gui.menubar.MenubarController;
import rocks.wallenius.joop.model.entity.JoopClass;
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
    TabPane tabPane;

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

    private SyntaxHighlighter syntaxHighlighter;

    /**
     * MVC components instantiated from this constructor
     */
    public WindowController() {
        syntaxHighlighter = new SyntaxHighlighter();
        List<JoopClass> classes = new ArrayList<JoopClass>();
        mainController = new MainController(classes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menubarController.setParentController(this);
        toolbarController.setParentController(this);
        consoleController.setParentController(this);
        classDiagramController.setParentController(this);
        objectDiagramController.setParentController(this);

        setupGuiBindings();

        // initialize program with a closed console
        consoleAndStatusBarContainer.getChildren().remove(console);

    }

    /**
     * Handler for open button events
     */
    @FXML
    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Class");
        FileChooser.ExtensionFilter extFilterSvg = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java", "*.JAVA");
        fileChooser.getExtensionFilters().addAll(extFilterSvg);
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (file != null) {

            String className = file.getName();

            // standardize name
            if (className.toLowerCase().endsWith(".java")) {
                className = className.substring(0, className.lastIndexOf("."));
            }

            try {
                JoopClass loadedClass = mainController.openClass(className, file);
                addTab(loadedClass);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load class").showAndWait();
            }
        }
    }

    /**
     * Handler for save button events
     */
    @FXML
    public void save() {
        saveClass(tabPane.getSelectionModel().getSelectedItem());
    }

    private void saveAll() {
        for(javafx.scene.control.Tab currentTab : tabPane.getTabs()) {
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
     * Handler for compile Tab events
     */
    @FXML
    public void compileClasses() {

        boolean proceed = true;
        if(isUnsavedChanges()) {
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
                classDiagramController.draw();
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
     * Handler for new button events
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

                addTab(createdClass);

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

    private boolean isUnsavedChanges() {
        boolean unsavedChanges = false;
        for(javafx.scene.control.Tab tab : tabPane.getTabs()) {
            if(tab instanceof Tab) {
                Tab currentTab = (Tab) tab;
                if(currentTab.getChanged()) {
                    unsavedChanges = true;
                    break;
                }
            }
        }
        return unsavedChanges;
    }

    /**
     * Sets up bindings between different GUI elements
     */
    private void setupGuiBindings() {

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Tab currentTab = getTabByName(newValue.getText());
                bindClassChangesToButtons(currentTab);
                setSyntaxHighlightingForTab(currentTab);
            }
        });

        // enable/disable Compile-button depending on if there are open tabs in the view
        tabPane.getTabs().addListener((ListChangeListener<javafx.scene.control.Tab>) c -> {
            if (tabPane.getTabs().size() > 0) {
                toolbarController.getButtonCompile().setDisable(false);
                menubarController.getMenuItemCompile().setDisable(false);
            } else {
                toolbarController.getButtonCompile().setDisable(true);
                menubarController.getMenuItemCompile().setDisable(true);
            }
        });

        // toggle console window depending on View menu Console item
        menubarController.getMenuItemConsole().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                consoleAndStatusBarContainer.getChildren().add(0, console);
            } else {
                consoleAndStatusBarContainer.getChildren().remove(console);
            }
        });

    }

    private void setSyntaxHighlightingForTab(Tab tab) {
        syntaxHighlighter.applySyntaxHighlighting(tab.getCodeArea());
    }

    private Tab getTabByName(String name) {
        for(javafx.scene.control.Tab tab : tabPane.getTabs()) {
            if(tab.getText().equals(name) && tab instanceof Tab) {
                return (Tab) tab;
            }
        }
        return null;
    }

    /**
     * Creates a popup requesting for a class name for the new class to be created
     *
     * @return Returns an Optional with the class name as a String
     */
    private Optional<String> promptClassName() {
        NewDialog newDialog = new NewDialog((Stage) tabPane.getScene().getWindow());
        return newDialog.getValue();
    }

    private Optional<ButtonType> promptUnsavedChanges() {
        return new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the changes to all open classes?").showAndWait();
    }

    /**
     * Creates a new tab in the view for a class
     *
     * @param clazz to create tab for
     */
    private void addTab(JoopClass clazz) {
        Tab newTab = new Tab(clazz);
        newTab.getCodeArea().setOnKeyPressed(event -> newTab.setChanged(true));
        newTab.setOnClosed(event -> {
            mainController.closeClass(newTab.getClazz());
        });
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        bindClassChangesToButtons(newTab);
    }

    /**
     * Binds the Save button and enables/disables it according to the changed-flag in the Tab for each class in each tab.
     *
     * @param tab to listen for changes in
     */
    private void bindClassChangesToButtons(rocks.wallenius.joop.model.entity.Tab tab) {
        toolbarController.getButtonSave().setDisable(!tab.getChanged());
        menubarController.getMenuItemSave().setDisable(!tab.getChanged());

        tab.changedProperty().addListener((observable, oldValue, newValue) -> {
            toolbarController.getButtonSave().setDisable(!newValue);
            menubarController.getMenuItemSave().setDisable(!newValue);
        });
    }

    public void stop() {
        syntaxHighlighter.stop();
    }

    public List<JoopClass> getClasses() {
        return mainController.getClasses();
    }

}
