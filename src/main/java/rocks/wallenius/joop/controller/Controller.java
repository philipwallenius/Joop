package rocks.wallenius.joop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.Clazz;

import java.util.Optional;

/**
 * Created by philipwallenius on 14/02/16.
 */
public class Controller {

    @FXML
    TabPane tabPane;

//    @FXML
//    BorderPane borderPane;
//    Stage stageTheLabelBelongs = (Stage) borderPane.getScene().getWindow();

    private Model model;

    public Controller() {
        model = new Model();
    }

    @FXML
    protected void newCustomClass(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog("Untitled");
        inputDialog.setTitle("New");
        inputDialog.setHeaderText("New Class");
        inputDialog.setContentText("Class Name: ");
        Optional<String> name = inputDialog.showAndWait();
        if(name.isPresent()) {
            Clazz newClazz = new Clazz();
            newClazz.setName(name.get());
            newClazz.setCodeArea(new CodeArea());
            model.addClass(newClazz);
            Tab newTab = new Tab(newClazz.getName());
            newTab.setContent(newClazz.getCodeArea());
            tabPane.getTabs().add(newTab);

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

}
