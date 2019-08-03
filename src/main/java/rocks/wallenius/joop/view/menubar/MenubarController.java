package rocks.wallenius.joop.view.menubar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import rocks.wallenius.joop.view.JavaFXViewImpl;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class MenubarController implements Initializable {

    private JavaFXViewImpl parentController;

    @FXML
    MenuItem menuItemSave;

    @FXML
    MenuItem menuItemCompile;

    @FXML
    CheckMenuItem menuItemConsole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItemSave.setDisable(true);
        menuItemCompile.setDisable(true);
    }

    @FXML
    public void open() {
        parentController.open();
    }

    @FXML
    public void save() {
        parentController.save();
    }

    @FXML
    public void create() {
        parentController.create();
    }

    @FXML
    public void compileClasses() {
        parentController.compileClasses();
    }

    @FXML void exitApplication() {
        parentController.exitApplication();
    }

    @FXML
    public void closeConsole() {
        menuItemConsole.setSelected(false);
    }

    @FXML
    public void openConsole() {
        menuItemConsole.setSelected(true);
    }

    @FXML
    public void about() {
        Alert about = new Alert(Alert.AlertType.INFORMATION, "JOOP by Philip Wallenius\n\nContact:\nphilip.wallenius@gmail.com");
        about.setTitle("About");
        about.setHeaderText("About");
        about.showAndWait();
    }


    public MenuItem getMenuItemSave() {
        return menuItemSave;
    }

    public MenuItem getMenuItemCompile() {
        return menuItemCompile;
    }

    public CheckMenuItem getMenuItemConsole() {
        return menuItemConsole;
    }

    public void setParentController(JavaFXViewImpl parentController) {
        this.parentController = parentController;
    }

}
