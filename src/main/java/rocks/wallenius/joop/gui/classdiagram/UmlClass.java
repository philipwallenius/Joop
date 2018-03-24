package rocks.wallenius.joop.gui.classdiagram;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 05/03/2018.
 */
public class UmlClass extends VBox {

    private StackPane titleBox;
    private StackPane propertiesBox;
    private StackPane methodsBox;

    private final static int DEFAULT_WIDTH = 140;

    private String title;
    private Field[] fields;
    private Method[] methods;

    public UmlClass(int x, int y, String title, Field[] fields, Method[] methods) {
        super();

        this.title = title;
        this.fields = fields;
        this.methods = methods;

        setLayoutX(x);
        setLayoutY(y);

        initializeShape();
        initializeContent();
    }

    private void initializeShape() {
        titleBox = new StackPane();
        propertiesBox = new StackPane();
        methodsBox = new StackPane();

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.web("#888888"));
        setEffect(dropShadow);

        getChildren().addAll(titleBox, propertiesBox, methodsBox);
    }

    private void initializeContent() {
        drawTitle();
        drawFields();
        drawMethods();
    }

    private void drawTitle() {
        Rectangle titleContainer = new Rectangle();
        titleContainer.setWidth(DEFAULT_WIDTH);
        titleContainer.setHeight(20);
        titleContainer.setStroke(Color.BLACK);
        titleContainer.setFill(Color.web("#FFFFF0"));

        Text text = new Text();
        text.setText(title);
        text.setFont(Font.font(null, FontWeight.BOLD, 14));
        titleBox.getChildren().addAll(titleContainer, text);
    }

    private void drawFields() {
        VBox propertiesHolder = new VBox();
        propertiesHolder.setAlignment(Pos.BASELINE_LEFT);
        propertiesHolder.setPadding(new Insets(5, 0, 0, 10));



        List<Text> props = new ArrayList<Text>();

        for(Field field : fields) {
            Text text = new Text();
            text.setText(String.format("%s %s", getAccessModifierSymbol(field.getAccessModifier()), field.isFinal() ? field.getName().toUpperCase() : field.getName()));
            text.setFont(Font.font(null, FontPosture.REGULAR, 10));
            if(field.isStatic()) {
                text.setUnderline(true);
            }
            props.add(text);
        }

        Rectangle fieldsContainer = new Rectangle();
        fieldsContainer.setWidth(DEFAULT_WIDTH);
        fieldsContainer.setHeight(20 * fields.length);
        fieldsContainer.setStroke(Color.BLACK);
        fieldsContainer.setFill(Color.web("#FFFFF0"));
        propertiesBox.getChildren().addAll(fieldsContainer);

        for(Text prop : props) {
            propertiesHolder.getChildren().addAll(prop);
        }

        propertiesBox.getChildren().addAll(propertiesHolder);
    }

    private void drawMethods() {
        VBox methodsHolder = new VBox();
        methodsHolder.setAlignment(Pos.BASELINE_LEFT);
        methodsHolder.setPadding(new Insets(5, 0, 0, 10));


        List<Text> m = new ArrayList<Text>();

        for(Method method : methods) {
            Text text = new Text();
            text.setText(String.format("%s %s", getAccessModifierSymbol(method.getAccessModifier()), method.getName()));
            text.setFont(Font.font(null, FontWeight.BOLD, 10));
            m.add(text);
        }

        Rectangle methodsContainer = new Rectangle();
        methodsContainer.setWidth(DEFAULT_WIDTH);
        methodsContainer.setHeight(20 * methods.length);
        methodsContainer.setStroke(Color.BLACK);
        methodsContainer.setFill(Color.web("#FFFFF0"));
        methodsBox.getChildren().addAll(methodsContainer);

        for(Text prop : m) {
            methodsHolder.getChildren().addAll(prop);
        }

        methodsBox.getChildren().addAll(methodsHolder);
    }

    private static String getAccessModifierSymbol(String accessModifier) {
        switch (accessModifier) {
            case "public" : {
                return "+";
            }
            case "private" : {
                return "-";
            }
            case "protected" : {
                return "#";
            }
            case "package" : {
                return "~";
            }
            default: {
                throw new IllegalArgumentException(String.format("Invalid accessModifier: %", accessModifier));
            }
        }
    }

}
