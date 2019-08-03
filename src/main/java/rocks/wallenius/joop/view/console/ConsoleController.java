package rocks.wallenius.joop.view.console;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import rocks.wallenius.joop.view.View;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        ContextMenu contextMenu = createContextMenu();

        consoleStyleClassedTextArea.setOnContextMenuRequested(event -> {
            contextMenu.hide();
            contextMenu.show(consoleStyleClassedTextArea, event.getScreenX(), event.getScreenY());
        });

        consoleStyleClassedTextArea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
            }
        });

    }

    @FXML
    public void closeConsole() {
        this.parentController.closeConsole();
    }

    public void clear() {
        if(consoleStyleClassedTextArea.getText().length() > 0) {
            consoleStyleClassedTextArea.clear();
        }
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

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        MenuItem item = new MenuItem("Clear All");
        item.setOnAction(event -> clear());
        menuItems.add(item);

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
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
