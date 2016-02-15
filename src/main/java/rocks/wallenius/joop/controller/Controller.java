package rocks.wallenius.joop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import rocks.wallenius.joop.gui.dialog.NewDialog;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 *
 * MVC Controller
 *
 * Created by philipwallenius on 14/02/16.
 */
public class Controller {

    @FXML
    TabPane tabPane;

    private Model model;

    /**
     * MVC components instantiated from this constructor
     */
    public Controller() {
        model = new Model();
    }

    /**
     * Handler for open custom class events
     * @param event
     */
    @FXML
    protected void openCustomClass(ActionEvent event) {
        System.out.println("Clicked Open Button!");
    }

    /**
     * Handler for save custom class events
     * @param event
     */
    @FXML
    protected void saveCustomClass(ActionEvent event) {
        System.out.println("Clicked Save Button!");
    }

    /**
     * Handler for compile custom class events
     * @param event
     */
    @FXML
    protected void compileClasses(ActionEvent event) {
        System.out.println("Clicked Compile Button!");
    }

    /**
     * Handler for application exit
     * @param event
     */
    @FXML
    protected void exitApplication(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handler for new custom class events
     * @param event
     */
    @FXML
    protected void newCustomClass(ActionEvent event) {

        Optional<String> className = promptClassName();

        if(className.isPresent()) {
            try {
                createNewClass(className.get());
            } catch(IOException | URISyntaxException exception) {
                exception.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to create new class");
            }
        }
    }

    /**
     * Creates a popup requesting for a class name for the new class to be created
     * @return Returns an Optional with the class name as a String
     */
    private Optional<String> promptClassName() {
        NewDialog newDialog = new NewDialog((Stage)tabPane.getScene().getWindow());
        return newDialog.getValue();
    }

    /**
     * Creates a new class and opens a new tab with a CodeArea
     * @param className to name the new class
     * @throws IOException
     * @throws URISyntaxException
     */
    private void createNewClass(String className) throws IOException, URISyntaxException {
        CustomClass newCustomClass = new CustomClass();
        newCustomClass.setName(className);
        newCustomClass.setCode(loadClassTemplate(className));
        model.addClass(newCustomClass);
        Tab newTab = new Tab(newCustomClass.getName());
        newTab.setContent(new CodeArea(newCustomClass.getCode()));
        tabPane.getTabs().add(newTab);
    }

    /**
     * Loads the default class code from template and inserts the specific class name
     * @param className to insert into template class code
     * @return Returns template class code with passed class name
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadClassTemplate(String className) throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("/class_template.java");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return content.replace("<<CLASSNAME>>", className);
    }

}
