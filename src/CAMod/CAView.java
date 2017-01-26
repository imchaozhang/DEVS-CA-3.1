package CAMod;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CAView extends Application {

	private double sceneWidth = 1580;
	private double sceneHeight = 960;

	private int n = 10;
	private int m = 10;

	double gridWidth = sceneWidth / n;
	double gridHeight = sceneHeight / m;

	MyNode[][] playfield = new MyNode[n][m];

	public CAView(int xdim, int ydim) {
		n = xdim;
		m = ydim;

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}

	public static class MyNode extends StackPane {
		public Rectangle rectangle;
		public String status = "";
		public Label label;
		public Color color;

		public MyNode(double x, double y, double width, double height) 
		{
			// initialize rectangle
			rectangle = new Rectangle(width, height);
			
		}
	}
}
