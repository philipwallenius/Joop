package rocks.wallenius.joop.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.fxmisc.richtext.StyleClassedTextArea;
import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.gui.dialog.NewDialog;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * MVC GuiController
 * <p>
 * Created by philipwallenius on 14/02/16.
 */
public class GuiController implements Initializable {

    @FXML
    TabPane tabPane;

    @FXML
    Button buttonSave;

    @FXML
    Button buttonCompile;

    @FXML
    MenuItem menuItemSave;

    @FXML
    MenuItem menuItemCompile;

    @FXML
    CheckMenuItem menuItemConsole;

    @FXML
    StatusBar statusBar;

    @FXML
    VBox consoleAndStatusBarContainer;

    @FXML
    VBox console;

    @FXML
    StackPane classView;

    @FXML
    StackPane objectView;

    @FXML
    StyleClassedTextArea consoleStyleClassedTextArea;

    private MainController mainController;

    private ClassViewController classViewController;

    private Model model;

    /**
     * MVC components instantiated from this constructor
     */
    public GuiController() {
        model = new Model();
        mainController = new MainController(model);
        classViewController = new ClassViewController(model);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        classViewController.setCanvas(classView);

        buttonSave.setDisable(true);
        buttonCompile.setDisable(true);
        menuItemSave.setDisable(true);
        menuItemCompile.setDisable(true);

        setupGuiBindings();

        // intialize program with closed console
        consoleAndStatusBarContainer.getChildren().remove(console);

    }

    /**
     * Handler for open CustomClass events
     */
    @FXML
    protected void openCustomClass() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Class");
        FileChooser.ExtensionFilter extFilterSvg = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java", "*.JAVA");
        fileChooser.getExtensionFilters().addAll(extFilterSvg);
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (file != null) {
            try {

                CustomClass loadedCustomClass = mainController.openClass(file);
                addClassTab(loadedCustomClass);

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load class").showAndWait();
            }
        }
    }

    /**
     * Handler for save CustomClass events
     */
    @FXML
    protected void saveCustomClass() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        CustomClass c = model.getCustomClassByName(tab.getText());
        try {
            mainController.saveClass(c);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to save class").showAndWait();
        }
    }

    @FXML
    protected void closeConsole() {
        menuItemConsole.setSelected(false);
    }

    @FXML
    private void openConsole() {
        menuItemConsole.setSelected(true);
    }


    /**
     * Handler for compile CustomClass events
     */
    @FXML
    protected void compileClasses() {

        statusBar.setText("Compiling...");
        consoleStyleClassedTextArea.clear();

        // compile all open classes
        if (model.getClasses().size() > 0) {

            List<File> fileList = model.getClasses().stream().map(CustomClass::getFile).collect(Collectors.toList());

            try {

                mainController.compileClasses();
                mainController.loadClasses();
                classViewController.drawClassDiagram();
                statusBar.setText("Compilation completed successfully");

            } catch (CompilationException compilationException) {
                openConsole();
                consoleStyleClassedTextArea.clear();
                consoleStyleClassedTextArea.appendText(compilationException.getCompilationExceptionMessage());
                consoleStyleClassedTextArea.setStyleClass(0, compilationException.getCompilationExceptionMessage().length(), "exceptionText");
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
     * Handler for clicks on the new button
     */
    @FXML
    protected void newCustomClass() {
        Optional<String> className = promptClassName();

        if (className.isPresent()) {
            try {

                String path = className.get();

                // standardize the name of the new class
                if (path.toLowerCase().endsWith(".java")) {
                    path = path.substring(0, path.lastIndexOf("."));
                }
                String filePath = path.replace(".", "/");

                CustomClass createdClass = mainController.createNewClass(path);

                addClassTab(createdClass);

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to create new class").showAndWait();
            }
        }
    }

    /**
     * Handler for application exit
     */
    @FXML
    protected void exitApplication() {
        System.exit(0);
    }

    /**
     * Sets up bindings between different GUI elements
     */
    private void setupGuiBindings() {

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                bindClassChangesToSaveButtons(model.getCustomClassByName(newValue.getText()));
            }
        });

        // enable/disable Compile-button depending on if there are open tabs in the view
        tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (tabPane.getTabs().size() > 0) {
                buttonCompile.setDisable(false);
            } else {
                buttonCompile.setDisable(true);
            }
        });

        // toggle console window depending on View menu Console item
        menuItemConsole.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                consoleAndStatusBarContainer.getChildren().add(0, console);
            } else {
                consoleAndStatusBarContainer.getChildren().remove(console);
            }
        });

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

    /**
     * Creates a new tab in the view for a class
     *
     * @param customClass to create tab for
     */
    private void addClassTab(CustomClass customClass) {
        Tab newTab = new Tab(customClass.getName());
        customClass.getCodeArea().setOnKeyPressed(event -> {
            customClass.setChanged(true);
        });
        newTab.setContent(customClass.getCodeArea());
        newTab.setOnClosed(event -> model.removeClass(customClass));
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        bindClassChangesToSaveButtons(customClass);
    }

    /**
     * Binds the Save button and enables/disables it according to the changed-flag in the CustomClass for each class in each tab.
     *
     * @param c class to listen for changes in
     */
    private void bindClassChangesToSaveButtons(CustomClass c) {
        buttonSave.setDisable(!c.getChanged());
        menuItemSave.setDisable(!c.getChanged());
        c.changedProperty().addListener((observable, oldValue, newValue) -> {
            buttonSave.setDisable(!newValue);
            menuItemSave.setDisable(!newValue);
        });
    }

}
