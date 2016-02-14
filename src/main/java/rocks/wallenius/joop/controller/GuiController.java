package rocks.wallenius.joop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

/**
 * Created by philipwallenius on 13/02/16.
 */
public class GuiController {

    @FXML
    protected void handleNewButtonAction(ActionEvent event) {
        System.out.println("Clicked New Button!");
    }

    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
        System.out.println("Clicked Open Button!");
    }

    @FXML
    protected void handleSaveButtonAction(ActionEvent event) {
        System.out.println("Clicked Save Button!");
    }

    @FXML
    public void handleCompileButtonAction(ActionEvent event) {
        System.out.println("Clicked Compile Button!");
    }

    @FXML
    public void handleExitButtonAction(ActionEvent event) {
        System.exit(0);
    }

}
