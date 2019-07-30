package rocks.wallenius.joop.adapter.gui.dialog;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import rocks.wallenius.joop.adapter.gui.classdiagram.Parameter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class NewObjectDialog extends Dialog<NewObject> {

    private String className;
    private Parameter[] parameters;
    private List<TextField> inputs;
    private Label errorLabel;

    public NewObjectDialog(String className, Parameter[] parameters) {
        super();

        getDialogPane().getStylesheets().add("css/dialogs.css");

        this.className = className;
        this.parameters = parameters;
        inputs = new ArrayList<>();

        setTitle(String.format("Create %s object", className));

        setResizable(true);

        setup();
    }

    private void setup() {
        GridPane pane = new GridPane();
        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");
        pane.add(errorLabel, 0, 0);

        Label instanceNameLbl = new Label("Instance name: ");
        TextField instanceNameTxt = new TextField();
        pane.add(instanceNameLbl, 0, 1);
        pane.add(instanceNameTxt, 1, 1);

        ButtonType buttonTypeOk = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);

        getDialogPane().getButtonTypes().add(buttonTypeOk);

        final Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if(!isValid(instanceNameTxt, parameters)) {
                event.consume();
            }
        });

        if(parameters.length > 0) {
            setHeaderText("Please provide parameter values");

            int rowIndex = 2;

            for(Parameter param : parameters) {
                Label lbl = new Label(String.format("%s %s: ", param.getType().getSimpleName(), param.getName()));
                TextField txt = new TextField();
                pane.add(lbl, 0, rowIndex);
                pane.add(txt, 1, rowIndex);
                inputs.add(txt);
                rowIndex++;
            }

        }

        getDialogPane().setContent(pane);



        setResultConverter(button -> {
            if (button == buttonTypeOk) {

                String instanceName = instanceNameTxt.getText().trim();
                List<Object> arguments = new ArrayList<>();

                for (int i = 0; i < parameters.length; i++) {
                    TextField input = inputs.get(i);
                    arguments.add(castArgument(parameters[i].getType(), input.getText()));
                }

                return new NewObject(instanceName, Arrays.stream(parameters).map(Parameter::getType).collect(Collectors.toList()), arguments);
            }
            return null;
        });
    }

    private boolean isValid(TextField instanceNameInput, Parameter[] parameters) {

        final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
        boolean isValid = true;

        String instanceName = instanceNameInput.getText().trim();
        final Pattern pattern = Pattern.compile("[a-zA-Z$_][a-zA-Z0-9$_]*");
        if(instanceName.length() <= 0 || !pattern.matcher(instanceName).matches()) {
            instanceNameInput.pseudoClassStateChanged(errorClass, true);
            isValid = false;
        } else {
            instanceNameInput.pseudoClassStateChanged(errorClass, false);
        }

        for (int i = 0; i < parameters.length; i++) {
            try {
                TextField input = inputs.get(i);
                castArgument(parameters[i].getType(), input.getText());
                input.pseudoClassStateChanged(errorClass, false);
            } catch(Exception ex) {
                TextField input = inputs.get(i);
                input.pseudoClassStateChanged(errorClass, true);

                isValid = false;
            }
        }

        if(!isValid) {
            errorLabel.setText("Invalid value");
        } else {
            errorLabel.setText("");
        }

        return isValid;
    }

    private Object castArgument(Class type, String value) {
        Object result;
        value = value.trim();
        if(type == String.class) {
            result = value;
        } else if(type == int.class) {
            result = Integer.parseInt(value);
        } else if(type == double.class) {
            result = Double.parseDouble(value);
        } else if(type == float.class) {
            result = Float.parseFloat(value);
        } else if(type == char.class) {
            result = value.charAt(0);
        } else if(type == byte.class) {
            result = Byte.valueOf(value);
        } else if(type == short.class) {
            result = Short.valueOf(value);
        } else if(type == long.class) {
            result = Long.valueOf(value);
        } else if(type == boolean.class) {
            result = Boolean.valueOf(value);
        } else {
            throw new ClassCastException(String.format("Invalid argument value for %s: %s", type.getSimpleName(), value));
        }
        return result;
    }

}
