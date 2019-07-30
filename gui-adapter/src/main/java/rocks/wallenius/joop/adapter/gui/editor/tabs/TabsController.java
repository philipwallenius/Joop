package rocks.wallenius.joop.adapter.gui.editor.tabs;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import rocks.wallenius.joop.adapter.gui.WindowController;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.adapter.gui.editor.SyntaxHighlighter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class TabsController implements Initializable {

    @FXML
    TabPane tabPane;

    private WindowController parentController;

    private SyntaxHighlighter syntaxHighlighter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        syntaxHighlighter = new SyntaxHighlighter();
        setupUiBindings();
    }

    public void setParentController(WindowController parentController) {
        this.parentController = parentController;
    }

    public List<Tab> getTabs() {
        return tabPane.getTabs();
    }

    public Tab getActiveTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    public rocks.wallenius.joop.adapter.gui.editor.tabs.Tab addTab(JoopClass clazz) {
        rocks.wallenius.joop.adapter.gui.editor.tabs.Tab newTab = new rocks.wallenius.joop.adapter.gui.editor.tabs.Tab(clazz);
        newTab.getCodeArea().setOnKeyPressed(event -> newTab.setChanged(true));
        newTab.setOnClosed(event -> {
            parentController.closeClass(newTab.getClazz());
        });
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        bindClassChangesToButtons(newTab);
        return newTab;
    }

    public void stop() {
        syntaxHighlighter.stop();
    }

    public boolean isUnsavedChanges() {
        boolean unsavedChanges = false;

        for(javafx.scene.control.Tab tab : getTabs()) {
            if(tab instanceof rocks.wallenius.joop.adapter.gui.editor.tabs.Tab) {
                rocks.wallenius.joop.adapter.gui.editor.tabs.Tab currentTab = (rocks.wallenius.joop.adapter.gui.editor.tabs.Tab) tab;
                if(currentTab.getChanged()) {
                    unsavedChanges = true;
                    break;
                }
            }
        }

        return unsavedChanges;
    }

    private void setSyntaxHighlightingForTab(rocks.wallenius.joop.adapter.gui.editor.tabs.Tab tab) {
        syntaxHighlighter.applySyntaxHighlighting(tab.getCodeArea());
    }

    /**
     * Sets up bindings between different GUI elements
     */
    private void setupUiBindings() {

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rocks.wallenius.joop.adapter.gui.editor.tabs.Tab currentTab = getTabByName(newValue.getText());
                bindClassChangesToButtons(currentTab);
                setSyntaxHighlightingForTab(currentTab);
            }
        });

        // enable/disable Compile-button depending on if there are open tabs in the view
        tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (tabPane.getTabs().size() > 0) {
                parentController.getToolbarController().getButtonCompile().setDisable(false);
                parentController.getMenubarController().getMenuItemCompile().setDisable(false);
            } else {
                parentController.getToolbarController().getButtonCompile().setDisable(true);
                parentController.getMenubarController().getMenuItemCompile().setDisable(true);
            }
        });

    }

    /**
     * Binds the Save button and enables/disables it according to the changed-flag in the Tab for each class in each tab.
     *
     * @param tab to listen for changes in
     */
    private void bindClassChangesToButtons(rocks.wallenius.joop.adapter.gui.editor.tabs.Tab tab) {
        parentController.getToolbarController().getButtonSave().setDisable(!tab.getChanged());
        parentController.getMenubarController().getMenuItemSave().setDisable(!tab.getChanged());

        tab.changedProperty().addListener((observable, oldValue, newValue) -> {
            parentController.getToolbarController().getButtonSave().setDisable(!newValue);
            parentController.getMenubarController().getMenuItemSave().setDisable(!newValue);
        });
    }

    private rocks.wallenius.joop.adapter.gui.editor.tabs.Tab getTabByName(String name) {
        for(javafx.scene.control.Tab tab : getTabs()) {
            if(tab.getText().equals(name) && tab instanceof rocks.wallenius.joop.adapter.gui.editor.tabs.Tab) {
                return (rocks.wallenius.joop.adapter.gui.editor.tabs.Tab) tab;
            }
        }
        return null;
    }

}
