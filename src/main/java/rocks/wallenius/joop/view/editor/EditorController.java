package rocks.wallenius.joop.view.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import rocks.wallenius.joop.view.View;
import rocks.wallenius.joop.model.Source;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class EditorController implements Initializable {

    @FXML
    TabPane tabPane;
    private View parentController;
    private SyntaxHighlighter syntaxHighlighter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        syntaxHighlighter = new SyntaxHighlighter();
        setupUiBindings();
    }

    public void setParentController(View parentController) {
        this.parentController = parentController;
    }

    public void open(Source source) {
        rocks.wallenius.joop.view.editor.Tab newTab = new rocks.wallenius.joop.view.editor.Tab(source);
        newTab.setOnClosed(event -> {
            parentController.close(newTab.getSource());
        });
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().selectLast();
    }

    public Source getActiveSource() {
        rocks.wallenius.joop.view.editor.Tab activeTab = (rocks.wallenius.joop.view.editor.Tab) tabPane.getSelectionModel().getSelectedItem();
        return activeTab.getSource();
    }

    public void stop() {
        syntaxHighlighter.stop();
    }

    private void setupUiBindings() {

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if( newValue instanceof rocks.wallenius.joop.view.editor.Tab) {
                    rocks.wallenius.joop.view.editor.Tab selectedTab = (rocks.wallenius.joop.view.editor.Tab) newValue;
                    syntaxHighlighter.applySyntaxHighlighting(selectedTab.getCodeArea());
                    parentController.setActive(selectedTab.getSource());
                }
            } else {
                parentController.setActive(null);
            }
        });

    }
}
