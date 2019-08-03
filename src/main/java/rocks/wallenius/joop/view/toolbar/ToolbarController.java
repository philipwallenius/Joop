package rocks.wallenius.joop.view.toolbar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import rocks.wallenius.joop.view.View;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 22/03/2018.
 */
public class ToolbarController implements Initializable {

    private View parentController;

    @FXML
    Button buttonSave;

    @FXML
    Button buttonCompile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public Button getButtonSave() {
        return buttonSave;
    }

    public Button getButtonCompile() {
        return buttonCompile;
    }

    public void setParentController(View parentController) {
        this.parentController = parentController;
    }

}
