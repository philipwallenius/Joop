package rocks.wallenius.joop.view.objectdiagram.dialog;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import rocks.wallenius.joop.view.classdiagram.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 02/04/2018.
 */
public class MethodDialog extends Dialog<MethodParameters> {

    private Parameter[] parameters;
    private List<TextField> inputs;
    private Label errorLabel;

    public MethodDialog(Parameter[] parameters) {
        super();

        getDialogPane().getStylesheets().add("css/dialogs.css");

        this.parameters = parameters;
        inputs = new ArrayList<>();

        setTitle("Invoke method");

        setResizable(true);

        setup();
    }

    private void setup() {
        GridPane pane = new GridPane();
        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");
        pane.add(errorLabel, 0, 0);

        ButtonType buttonTypeOk = new ButtonType("Invoke", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().add(buttonTypeOk);

        final Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if(!isValid(parameters)) {
                event.consume();
            }
        });

        if(parameters.length > 0) {
            setHeaderText("Please provide parameter values");

            int rowIndex = 1;

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

                List<Object> arguments = new ArrayList<>();

                for(int i = 0; i < parameters.length; i++) {
                    TextField input = inputs.get(i);
                    arguments.add(castArgument(parameters[i].getType(), input.getText()));
                }

                return new MethodParameters(Arrays.stream(parameters).map(Parameter::getType).collect(Collectors.toList()), arguments);
            }
            return null;
        });
    }

    private boolean isValid(Parameter[] parameters) {

        final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
        boolean isValid = true;

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
