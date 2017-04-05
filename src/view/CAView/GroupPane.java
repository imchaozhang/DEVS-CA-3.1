package view.CAView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public final class GroupPane extends Parent {

    private GridPane _flowPane;
    private TitledPane _titledPane;

    public GroupPane() {
        _flowPane = new GridPane();
        _titledPane = new TitledPane();
        setContentPadding(new Insets(10));
        _titledPane.setCollapsible(false);
        _titledPane.setContent(_flowPane);
        super.getChildren().add(_titledPane);
    }

    public GroupPane(String title, Node content) {
        this();
        setText(title);
        setContent(content);
    }

    public GroupPane(String title, Node content, Insets contentPadding) {
        this(title, content);
        setContentPadding(contentPadding);
    }

    public void setText(String value) {
        _titledPane.setText(value);
    }

    public void setContent(Node node) {
        _flowPane.getChildren().add(node);
    }

    public void setContentPadding(Insets value) {
        _flowPane.setPadding(value);
    }
}