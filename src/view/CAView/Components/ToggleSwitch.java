package view.CAView.Components;

//Create by Almas Baimagambetov. Get it from https://github.com/AlmasB/FXTutorials/blob/master/src/com/almasb/ios/IOSApp.java
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ToggleSwitch extends Parent {

    private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }
    
    public void setSwitchValue(boolean on) {
    	switchedOn.setValue(on);
    	
    }

    public ToggleSwitch() {	
    	this(" ON OFF");
    	
    }
    public ToggleSwitch(String _text) {	
        Rectangle background = new Rectangle(62, 26);
        background.setArcWidth(26);
        background.setArcHeight(26);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        Circle trigger = new Circle(15);
        trigger.setCenterX(15);
        trigger.setCenterY(13);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.LIGHTGRAY);
        

        DropShadow shadow = new DropShadow();
        shadow.setRadius(2);
        trigger.setEffect(shadow);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        Text text = new Text(_text);
        StackPane stack = new StackPane();
        stack.getChildren().addAll(background,text);
        getChildren().addAll(stack, trigger);

        switchedOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState.booleanValue();
            translateAnimation.setToX(isOn ? 62 - 30 : 0);
            fillAnimation.setFromValue(isOn ? Color.WHITE : Color.SKYBLUE);
            fillAnimation.setToValue(isOn ? Color.SKYBLUE : Color.WHITE);

            animation.play();
        });

        setOnMouseClicked(event -> {
            switchedOn.set(!switchedOn.get());
        });
    }
    


}