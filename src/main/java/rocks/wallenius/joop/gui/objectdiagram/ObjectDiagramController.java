package rocks.wallenius.joop.gui.objectdiagram;

import javafx.fxml.Initializable;
import rocks.wallenius.joop.gui.WindowController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ObjectDiagramController implements Initializable {

    private WindowController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

}
