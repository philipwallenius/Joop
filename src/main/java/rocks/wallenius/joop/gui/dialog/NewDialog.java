package rocks.wallenius.joop.gui.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rocks.wallenius.joop.controller.JavaUtil;

import java.util.Optional;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class NewDialog extends Stage {

    private final static String DEFAULT_NAME = "Untitled";

    private final Stage parentStage;
    private TextField inputName;
    private Label labelError;
    private boolean cancelled;
    private Button buttonOK, buttonCancel;

    public NewDialog(Stage parentStage) {
        super();
        this.parentStage = parentStage;
        cancelled = false;
        initialize();
    }

    /**
     * Initializes the NewDialog and displays it to the user
     */
    private void initialize() {

        // Setup the stage
        setTitle("Create New Class");
        setResizable(false);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(parentStage);

        // Create input fields and buttons
        GridPane gridInputs = createGridInputs();
        GridPane gridButtons = createGridButtons();

        labelError = new Label("Invalid class name");
        labelError.setVisible(false);
        GridPane gridError = new GridPane();
        gridError.setAlignment(Pos.CENTER);
        gridError.add(labelError, 0, 0);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(gridInputs, gridError, gridButtons);

        Scene scene = new Scene(vbox);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    cancelled = true;
                    close();
                } else if (ke.getCode() == KeyCode.ENTER) {
                    okAction();
                }
            }
        });

        // If user closes dialog, set cancelled value
        setOnCloseRequest(event -> {
            cancelled = true;
        });

        setScene(scene);
        showAndWait();
        requestFocus();

    }

    /**
     * Creates and styles a GridPane that contains name input field
     * @return Returns a GridPane containing the labels and text inputs
     */
    private GridPane createGridInputs() {

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);

        Label labelName = new Label("Name: ");
        inputName = new TextField(DEFAULT_NAME);

        // Ensure that only numbers are entered into width and height fields
        inputName.textProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!JavaUtil.isValidClassName(newValue)) {
                    labelError.setVisible(true);
                    buttonOK.setDisable(true);
                } else {
                    labelError.setVisible(false);
                    buttonOK.setDisable(false);
                }
            }
        });

        labelName.setMaxWidth(Double.MAX_VALUE);
        labelName.setAlignment(Pos.CENTER_RIGHT);

        gridPane.add(labelName, 0, 0, 1, 1);
        gridPane.add(inputName, 1, 0, 2, 1);

        return gridPane;
    }

    /**
     * Creates and styles a GridPane that contains OK and Cancel buttons
     * @return Returns a GridPane containing the buttons
     */
    private GridPane createGridButtons() {

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_RIGHT);
        gridPane.setHgap(10);

        buttonOK = new Button("OK");
        buttonCancel = new Button("Cancel");

        // Make buttons are equal width
        HBox.setHgrow(buttonOK, Priority.ALWAYS);
        HBox.setHgrow(buttonCancel, Priority.ALWAYS);
        buttonOK.setMaxWidth(Double.MAX_VALUE);
        buttonCancel.setMaxWidth(Double.MAX_VALUE);

        // Add event handlers to buttons
        buttonOK.setOnAction(event -> okAction());
        buttonCancel.setOnAction(event -> {
            cancelled = true;
            close();
        });

        gridPane.add(buttonCancel, 0, 0);
        gridPane.add(buttonOK, 1, 0);

        return gridPane;
    }

    private void okAction() {
        String name = inputName.getText().trim();

        // Validate name input
        if(name.length() == 0) {
            showInvalidInputAlert("Please enter a name for the class.");
            inputName.requestFocus();
            return;
        }

        // Create a new project with the inputs and close this dialog
        close();
    }

    /**
     * Displays a simple popup to the user with given title and message with an OK button
     * @param message displayed in popup window
     */
    private void showInvalidInputAlert(String message) {
        // show error to user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.show();
    }

    public Optional<String> getValue() {
        if(cancelled) {
            return Optional.empty();
        }
        return Optional.of(inputName.getText().trim());
    }
}
