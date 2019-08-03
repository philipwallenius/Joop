package rocks.wallenius.joop.adapter.view.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rocks.wallenius.joop.adapter.controller.ClassFileUtils;

import java.util.Optional;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class NewClassDialog extends Stage {

    private final static String DIALOG_TITLE = "Create New Class";
    private final static String DIALOG_INVALID_CLASS_NAME_ERROR = "Invalid class name";

    private final static String DEFAULT_CLASS_NAME = "Untitled";

    private final Stage parentStage;
    private TextField inputName;
    private Label labelError;
    private Button buttonOK, buttonCancel;
    private boolean cancelled;

    public NewClassDialog(Stage parentStage) {
        super();
        this.parentStage = parentStage;
        initialize();
    }

    /**
     * Initializes the NewDialog and displays it to the user
     */
    private void initialize() {

        cancelled = false;

        // Setup the stage
        setTitle(DIALOG_TITLE);
        setResizable(false);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(parentStage);

        // Create input fields and buttons
        GridPane gridInputs = createGridInputs();
        GridPane gridButtons = createGridButtons();

        labelError = new Label(DIALOG_INVALID_CLASS_NAME_ERROR);
        labelError.setVisible(false);
        GridPane gridError = new GridPane();
        gridError.setAlignment(Pos.CENTER);
        gridError.add(labelError, 0, 0);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(gridInputs, gridError, gridButtons);

        Scene scene = new Scene(vbox);
        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                cancelled = true;
                close();
            } else if (ke.getCode() == KeyCode.ENTER) {
                if(JavaClassNameUtil.isValidClassName(inputName.getText().trim())) {
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
        inputName = new TextField(DEFAULT_CLASS_NAME);

        // Ensure that only numbers are entered into width and height fields
        inputName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!JavaClassNameUtil.isValidClassName(newValue)) {
                labelError.setVisible(true);
                buttonOK.setDisable(true);
            } else {
                labelError.setVisible(false);
                buttonOK.setDisable(false);
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

        // Validate name has been entered
        if(name.length() == 0) {
            showInvalidInputAlert("Please enter a name for the class.");
            inputName.requestFocus();
            return;
        }

        // Validate name doesn't end with dot
        if(name.endsWith(".")) {
            showInvalidInputAlert(DIALOG_INVALID_CLASS_NAME_ERROR);
            inputName.requestFocus();
            return;
        }

        // Validate that class doesnt exist
        if(ClassFileUtils.exists(name)) {
            showInvalidInputAlert("Cannot create file. File already exists.");
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
