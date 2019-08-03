package rocks.wallenius.joop.view.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import rocks.wallenius.joop.view.View;
import rocks.wallenius.joop.model.Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    }

    public void close(Source source) {
        Tab tabToBeClosed = null;

        for(javafx.scene.control.Tab tab : tabPane.getTabs()) {
            if(tab.getText().equals(source.getClassName()) && tab instanceof rocks.wallenius.joop.view.editor.Tab) {
                tabToBeClosed = tab;
            }
        }

        if(tabToBeClosed != null) {
            tabPane.getTabs().remove(tabToBeClosed);
        }
    }

    public Source getActiveSource() {
        rocks.wallenius.joop.view.editor.Tab activeTab = (rocks.wallenius.joop.view.editor.Tab) tabPane.getSelectionModel().getSelectedItem();
        return activeTab.getSource();
    }

    public List<Source> getAllSources() {
        List<Source> sources = new ArrayList<>(tabPane.getTabs().size());
        tabPane.getTabs().forEach(tab -> sources.add(((rocks.wallenius.joop.view.editor.Tab)tab).getSource()));
        return sources;
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

/*public class EditorController implements Initializable {

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

    public rocks.wallenius.joop.adapter.view.editor.Tab addTab(JoopClass clazz) {
        rocks.wallenius.joop.adapter.view.editor.Tab newTab = new rocks.wallenius.joop.adapter.view.editor.Tab(clazz);
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
            if(tab instanceof rocks.wallenius.joop.adapter.view.editor.Tab) {
                rocks.wallenius.joop.adapter.view.editor.Tab currentTab = (rocks.wallenius.joop.adapter.view.editor.Tab) tab;
                if(currentTab.getChanged()) {
                    unsavedChanges = true;
                    break;
                }
            }
        }

        return unsavedChanges;
    }

    private void setSyntaxHighlightingForTab(rocks.wallenius.joop.adapter.view.editor.Tab tab) {
        syntaxHighlighter.applySyntaxHighlighting(tab.getCodeArea());
    }

    *//**
     * Sets up bindings between different GUI elements
     *//*
    private void setupUiBindings() {

        // listen to when user changes tabs in the editor, bind the current selected tab and class to the save button
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rocks.wallenius.joop.adapter.view.editor.Tab currentTab = getTabByName(newValue.getText());
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

    *//**
     * Binds the Save button and enables/disables it according to the changed-flag in the Tab for each class in each tab.
     *
     * @param tab to listen for changes in
     *//*
    private void bindClassChangesToButtons(rocks.wallenius.joop.adapter.view.editor.Tab tab) {
        parentController.getToolbarController().getButtonSave().setDisable(!tab.getChanged());
        parentController.getMenubarController().getMenuItemSave().setDisable(!tab.getChanged());

        tab.changedProperty().addListener((observable, oldValue, newValue) -> {
            parentController.getToolbarController().getButtonSave().setDisable(!newValue);
            parentController.getMenubarController().getMenuItemSave().setDisable(!newValue);
        });
    }

    private rocks.wallenius.joop.adapter.view.editor.Tab getTabByName(String name) {
        for(javafx.scene.control.Tab tab : getTabs()) {
            if(tab.getText().equals(name) && tab instanceof rocks.wallenius.joop.adapter.view.editor.Tab) {
                return (rocks.wallenius.joop.adapter.view.editor.Tab) tab;
            }
        }
        return null;
    }

}*/

