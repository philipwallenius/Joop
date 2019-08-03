package rocks.wallenius.joop.view.console;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fxmisc.richtext.StyleClassedTextArea;
import rocks.wallenius.joop.view.JavaFXViewImpl;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ConsoleController implements Initializable {

    private JavaFXViewImpl parentController;

    @FXML
    StyleClassedTextArea consoleStyleClassedTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void closeConsole() {
        this.parentController.closeConsole();
    }

    public void clear() {
        consoleStyleClassedTextArea.clear();
    }

    public void appendInfo(String message) {
        consoleStyleClassedTextArea.appendText(message);
        consoleStyleClassedTextArea.setStyleClass(0, message.length(), "infoText");
    }

    public void appendError(String message) {
        consoleStyleClassedTextArea.appendText(message);
        consoleStyleClassedTextArea.setStyleClass(0, message.length(), "exceptionText");
    }

    public void setParentController(JavaFXViewImpl parentController) {
        this.parentController = parentController;
    }

}
