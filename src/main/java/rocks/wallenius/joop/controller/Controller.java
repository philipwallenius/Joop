package rocks.wallenius.joop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import rocks.wallenius.joop.gui.dialog.NewDialog;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by philipwallenius on 14/02/16.
 */
public class Controller {

    @FXML
    TabPane tabPane;

    private Model model;

    public Controller() {
        model = new Model();
    }

    @FXML
    protected void newCustomClass(ActionEvent event) {

        Optional<String> className = promptClassName();

        if(className.isPresent()) {
            createNewClass(className.get());
        }
    }

    @FXML
    protected void openCustomClass(ActionEvent event) {
        System.out.println("Clicked Open Button!");
    }

    @FXML
    protected void saveCustomClass(ActionEvent event) {
        System.out.println("Clicked Save Button!");
    }

    @FXML
    protected void compileClasses(ActionEvent event) {
        System.out.println("Clicked Compile Button!");
    }

    @FXML
    protected void exitApplication(ActionEvent event) {
        System.exit(0);
    }

    private Optional<String> promptClassName() {
//        boolean validClassName = false;
//        Optional<String> inputName = Optional.of("Default");
//        String warning = "";
//
//        while(!validClassName && inputName.isPresent()) {
//            TextInputDialog inputDialog = new TextInputDialog("Untitled");
//            inputDialog.setTitle("New");
//            inputDialog.setHeaderText(warning);
//            inputDialog.setContentText("Class Name: ");
//            inputName = inputDialog.showAndWait();
//            if(inputName.isPresent()) {
//                validClassName = JavaUtil.isValidClassName(inputName.get());
//                if(!validClassName) {
//                    warning = "Invalid class name.";
//                }
//            }
//        }

        NewDialog newDialog = new NewDialog((Stage)tabPane.getScene().getWindow());

        return newDialog.getValue();
    }

    private void createNewClass(String className) {
        CustomClass newCustomClass = new CustomClass();
        newCustomClass.setName(className);
        newCustomClass.setCodeArea(new CodeArea());
        model.addClass(newCustomClass);
        Tab newTab = new Tab(newCustomClass.getName());
        newTab.setContent(newCustomClass.getCodeArea());
        tabPane.getTabs().add(newTab);
    }

}
