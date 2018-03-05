package rocks.wallenius.joop.controller;

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
import org.fxmisc.richtext.StyleClassedTextArea;
import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.gui.dialog.NewDialog;
import rocks.wallenius.joop.gui.syntaxhighlight.SyntaxHighlighter;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.Tab;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    private ObjectViewController objectViewController;

    private SyntaxHighlighter syntaxHighlighter;

    /**
     * MVC components instantiated from this constructor
     */
    public GuiController() {
        syntaxHighlighter = new SyntaxHighlighter();
        List<JoopClass> classes = new ArrayList<JoopClass>();
        mainController = new MainController(classes);
        classViewController = new ClassViewController(classes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        classViewController.setCanvas(classView);
//        objectViewController.setCanvas(objectView);

        buttonSave.setDisable(true);
        buttonCompile.setDisable(true);
        menuItemSave.setDisable(true);
        menuItemCompile.setDisable(true);

        setupGuiBindings();

        // initialize program with a closed console
        consoleAndStatusBarContainer.getChildren().remove(console);

    }

    /**
     * Handler for open button events
     */
    @FXML
    protected void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Class");
        FileChooser.ExtensionFilter extFilterSvg = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java", "*.JAVA");
        fileChooser.getExtensionFilters().addAll(extFilterSvg);
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (file != null) {
            try {
                JoopClass loadedClass = mainController.openClass(file);
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
    protected void save() {
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

    @FXML
    protected void closeConsole() {
        menuItemConsole.setSelected(false);
    }

    @FXML
    private void openConsole() {
        menuItemConsole.setSelected(true);
    }

    /**
     * Handler for compile Tab events
     */
    @FXML
    protected void compileClasses() {

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
            consoleStyleClassedTextArea.clear();

//            List<File> fileList = model.getClasses().stream().map(rocks.wallenius.joop.model.entity.Tab::getFile).collect(Collectors.toList());

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
     * Handler for new button events
     */
    @FXML
    protected void create() {
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

    /**
     * Handler for application exit
     */
    @FXML
    protected void exitApplication() {
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
                buttonCompile.setDisable(false);
                menuItemCompile.setDisable(false);
            } else {
                buttonCompile.setDisable(true);
                menuItemCompile.setDisable(true);
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
        buttonSave.setDisable(!tab.getChanged());
        menuItemSave.setDisable(!tab.getChanged());

        tab.changedProperty().addListener((observable, oldValue, newValue) -> {
            buttonSave.setDisable(!newValue);
            menuItemSave.setDisable(!newValue);
        });
    }

    public void stop() {
        syntaxHighlighter.stop();
    }

}
