package view.CAView.FXMLComponents;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class CATimeViewControlMenu extends Pane {
	
	private Node view;
    private CATimeViewControlMenuController controller;

    public CATimeViewControlMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CATimeViewControlMenu.fxml"));
//        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
//            @Override
//            public Object call(Class<?> param) {
//                return controller = new CATimeViewControlMenuController();
//            }
//        });
        controller = new CATimeViewControlMenuController();
        fxmlLoader.setController(controller);
        
        try {
            view = (Node) fxmlLoader.load();

        } catch (IOException ex) {
        }
        getChildren().add(view);
    }

//    public void setWelcome(String str) {
//        controller.textField.setText(str);
//    }
//
//    public String getWelcome() {
//        return controller.textField.getText();
//    }
//
//    public StringProperty welcomeProperty() {
//        return controller.textField.textProperty();
//    }
}