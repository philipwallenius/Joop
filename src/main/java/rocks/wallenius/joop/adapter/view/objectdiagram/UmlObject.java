package rocks.wallenius.joop.adapter.view.objectdiagram;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class UmlObject extends VBox {

    private StackPane titleBox;
    private StackPane propertiesBox;

    private final static int MIN_HEIGHT = 80;
    private final static int MIN_WIDTH = 100;

    private String className;
    private String instanceName;
    private Property[] properties;

    public UmlObject(String className, String instanceName, Property[] properties) {
        super();
        this.className = className;
        this.instanceName = instanceName;
        this.properties = properties;

        initializeShape();
        initializeContent();
    }

    private void initializeShape() {

        titleBox = new StackPane();
        propertiesBox = new StackPane();

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.web("#888888"));
        setEffect(dropShadow);

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#92cff6")), new Stop(1, Color.web("#85cbf8"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        Background bg = new Background(new BackgroundFill(lg1, null, null));
        setBackground(bg);

        getChildren().addAll(titleBox, propertiesBox);
    }

    private void initializeContent() {
        drawTitle();
        drawProperties();
        drawMethods();
    }

    private void drawTitle() {
        VBox titleHolder = new VBox();
        titleHolder.setAlignment(Pos.BASELINE_CENTER);
        titleHolder.setPadding(new Insets(0, 0, 7, 0));
        titleHolder.setBorder(createBorder());

        Text text = new Text();
        text.setText(String.format("%s :%s", instanceName, className));
        text.setFont(Font.font(null, FontWeight.BOLD, 14));

        titleHolder.getChildren().addAll(text);

        titleBox.getChildren().addAll(titleHolder);
    }

    private void drawProperties() {
        VBox propertiesHolder = new VBox();
        propertiesHolder.setAlignment(Pos.BASELINE_LEFT);
        propertiesHolder.setPadding(new Insets(7, 5, 7, 5));

        List<Text> props = new ArrayList<Text>();

        for(Property property : properties) {
            Text text = new Text();
            String name = property.getName();
            Object value = property.getValue();
            text.setText(String.format("%s = %s", name, value != null ? value.toString() : "null"));
            text.setFont(Font.font(null, FontPosture.REGULAR, 10));
            props.add(text);
        }

        for(Text prop : props) {
            propertiesHolder.getChildren().addAll(prop);
        }

        propertiesBox.getChildren().addAll(propertiesHolder);
    }

    private Border createBorder() {
        return new Border(new BorderStroke(null, null, Color.BLACK, null, null, null, BorderStrokeStyle.SOLID, null, null, null, null));
    }

    private void drawMethods() {

    }

}
