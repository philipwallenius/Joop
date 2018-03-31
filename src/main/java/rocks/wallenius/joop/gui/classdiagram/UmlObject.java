package rocks.wallenius.joop.gui.classdiagram;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class UmlObject extends VBox {

    private StackPane titleBox;

    private final static int MIN_HEIGHT = 80;
    private final static int MIN_WIDTH = 100;

    private String className;
    private String instanceName;

    public UmlObject(String className, String instanceName) {
        super();
        this.className = className;
        this.instanceName = instanceName;

        initializeShape();
        initializeContent();
    }

    private void initializeShape() {

        titleBox = new StackPane();

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        setPadding(new Insets(10, 10, 10, 10));
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

        getChildren().addAll(titleBox);
    }

    private void initializeContent() {
        drawTitle();
    }

    private void drawTitle() {
        VBox titleHolder = new VBox();
        titleHolder.setAlignment(Pos.BASELINE_CENTER);
        titleHolder.setPadding(new Insets(0, 0, 10, 0));

        Text text = new Text();
        text.setText(String.format("%s :%s", instanceName, className));
        text.setFont(Font.font(null, FontWeight.BOLD, 14));

        titleHolder.getChildren().addAll(text);

        titleBox.getChildren().addAll(titleHolder);
    }

}
