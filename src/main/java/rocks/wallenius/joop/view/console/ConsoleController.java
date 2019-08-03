package rocks.wallenius.joop.view.console;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fxmisc.richtext.StyleClassedTextArea;
import rocks.wallenius.joop.view.View;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ConsoleController implements Initializable {

    private View parentController;

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
        appendLine(message, "infoText");
    }

    public void appendError(String message) {
        appendLine(message, "exceptionText");
    }

    public void setParentController(View parentController) {
        this.parentController = parentController;
    }

    private int getStartIndex() {
        int startIndex = consoleStyleClassedTextArea.getText().length();
        return Math.max(0, startIndex);
    }

    private void appendLine(String message, String styleClass) {
        message = message + System.lineSeparator();
        int startIndex = getStartIndex();
        consoleStyleClassedTextArea.appendText(message);
        consoleStyleClassedTextArea.setStyleClass(startIndex, startIndex + message.length(), styleClass);
    }

}
