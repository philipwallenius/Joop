package rocks.wallenius.joop.controller;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import rocks.wallenius.joop.compiler.CompilerUtil;
import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.gui.dialog.NewDialog;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;
import rocks.wallenius.joop.util.ClassFileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MVC Controller
 * <p>
 * Created by philipwallenius on 14/02/16.
 */
public class Controller implements Initializable {

    private final static String CONF_SOURCES_DIR = "sources.directory";

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

    private Model model;

    private static ConfigurationService config;

    /**
     * MVC components instantiated from this constructor
     */
    public Controller() {
        model = new Model();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        config = ConfigurationService.getInstance();

        buttonSave.setDisable(true);
        buttonCompile.setDisable(true);
        menuItemSave.setDisable(true);
        menuItemCompile.setDisable(true);

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                bindClassChangesToSaveButtons(model.getCustomClassByName(newValue.getText()));
            }
        });

        // enable/disable Compile-button depending on if there are open tabs
        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> c) {
                if(tabPane.getTabs().size() > 0) {
                    buttonCompile.setDisable(false);
                } else {
                    buttonCompile.setDisable(true);
                }
            }
        });

    }

    /**
     * Binds the Save button and enables/disables it according to the changed-flag in the CustomClass for each class in each tab.
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

    /**
     * Handler for open custom class events
     *
     * @param event
     */
    @FXML
    protected void openCustomClass(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Class");
        FileChooser.ExtensionFilter extFilterSvg = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java", "*.JAVA");
        fileChooser.getExtensionFilters().addAll(extFilterSvg);
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if(file != null) {
            try {
                openClass(file);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to load class");
            }
        }
    }

    /**
     * Handler for save custom class events
     *
     * @param event
     */
    @FXML
    protected void saveCustomClass(ActionEvent event) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        CustomClass c = model.getCustomClassByName(tab.getText());
        try {
            saveClass(c);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to save class");
        }
    }

    /**
     * Handler for compile custom class events
     *
     * @param event
     */
    @FXML
    protected void compileClasses(ActionEvent event) {
        // compile classes
        if(model.getClasses().size() > 0) {
            List<File> fileList = model.getClasses().stream().map(CustomClass::getFile).collect(Collectors.toList());
            try {
                CompilerUtil.compile(fileList.toArray(new File[fileList.size()]));
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to compile classes");
            }
        }
    }

    /**
     * Handler for application exit
     *
     * @param event
     */
    @FXML
    protected void exitApplication(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handler for new custom class events
     *
     * @param event
     */
    @FXML
    protected void newCustomClass(ActionEvent event) {

        Optional<String> className = promptClassName();

        if (className.isPresent()) {
            try {
                createNewClass(className.get());
            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to create new class");
            }
        }
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
     * Creates a new class and opens a new tab with a CodeArea
     *
     * @param path of new class
     * @throws IOException
     * @throws URISyntaxException
     */
    private void createNewClass(String path) throws IOException, URISyntaxException {

        CustomClass newCustomClass = new CustomClass();

        // remove .java extension if it exists
        if (path.toLowerCase().endsWith(".java")) {
            path = path.substring(0, path.lastIndexOf("."));
        }

        String filePath = path.replace(".", "/");

        newCustomClass.setPath(new File(String.format("%s%s.java", config.getString(CONF_SOURCES_DIR), filePath)).toPath());

        if(containsPackageDefintions(path)) {
            String className = newCustomClass.getName().substring(0, newCustomClass.getName().lastIndexOf(".java"));
            String pckg = path.substring(0, path.lastIndexOf("."));
            newCustomClass.setCode(loadClassTemplate(className, pckg));
        } else {
            newCustomClass.setCode(loadClassTemplate(newCustomClass.getName().substring(0, newCustomClass.getName().lastIndexOf(".java"))));
        }

        // save new class to file
        saveClass(newCustomClass);
        model.addClass(newCustomClass);

        // create tab
        addClassTab(newCustomClass);
    }

    private boolean containsPackageDefintions(String className) {
        Matcher matcher = Pattern.compile("[.]{1}").matcher(className);
        return matcher.find();
    }

    private void openClass(File file) throws IOException {
        CustomClass loadedCustomClass;

        String name = file.getName();
        String code = ClassFileUtils.loadClass(file);

        loadedCustomClass = new CustomClass();
        loadedCustomClass.setCode(code);
        loadedCustomClass.setPath(file.toPath());

        model.addClass(loadedCustomClass);

        // create tab
        addClassTab(loadedCustomClass);
    }

    private void addClassTab(CustomClass customClass) {
        Tab newTab = new Tab(customClass.getName());
        CodeArea codeArea = new CodeArea(customClass.getCode());
        codeArea.setOnKeyTyped(event -> {
            customClass.setCode(codeArea.getText());
            customClass.setChanged(true);
        });
        newTab.setContent(codeArea);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        bindClassChangesToSaveButtons(customClass);
    }

    /**
     * Loads the default class code from template and inserts the specific class name
     *
     * @param className to insert into template class code
     * @return Returns template class code with passed class name
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadClassTemplate(String className) throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("/class_template.java");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return content.replace("<<CLASSNAME>>", className).replace("<<PACKAGE>>\n\n", "");
    }

    /**
     * Loads the default class code from template and inserts the specific class name and package
     * @param className to insert into template class code
     * @param pckg is the package to insert into template class code
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadClassTemplate(String className, String pckg) throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("/class_template.java");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return content.replace("<<CLASSNAME>>", className).replace("<<PACKAGE>>", String.format("package %s;", pckg));
    }

    /**
     * Saves a class to file
     *
     * @param clazz to save to file
     * @throws IOException
     */
    private void saveClass(CustomClass clazz) throws IOException {
        ClassFileUtils.saveClass(clazz);
        clazz.setChanged(false);
    }

}
