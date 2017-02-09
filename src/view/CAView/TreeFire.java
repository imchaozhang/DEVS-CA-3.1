package view.CAView;

import java.util.Random;

import javafx.animation.Animation.Status;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TreeFire extends Application {

	private double sceneWidth = 1580;
	private double sceneHeight = 960;

	private int n = 10;
	private int m = 10;

	double gridWidth = sceneWidth / n;
	double gridHeight = sceneHeight / m;

	MyNode[][] playfield = new MyNode[n][m];

	@Override
	public void start(Stage primaryStage) {

		Group root = new Group();

		// initialize playfield
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				// create node
				MyNode node = new MyNode(i * gridWidth, j * gridHeight, gridWidth, gridHeight);

				// add node to group
				root.getChildren().add(node);

				// add to playfield for further reference using an array
				playfield[i][j] = node;

			}
		}

		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		primaryStage.setScene(scene);
		primaryStage.show();

		animate();

	}

	private void animate() {

		ParallelTransition parallelTransition = new ParallelTransition();

		for (int ai = 0; ai < n; ai++) {
			for (int aj = 0; aj < m; aj++) {
				MyNode currentnode = playfield[ai][aj];
				MyNode N, E, W, S;
				if (ai > 0) {
					N = playfield[ai - 1][aj];
				} else
					N = null;
				if (ai < n - 1) {
					S = playfield[ai + 1][aj];
				} else
					S = null;
				if (aj > 0) {
					W = playfield[ai][aj - 1];
				} else
					W = null;
				if (aj < m - 1) {
					E = playfield[ai][aj + 1];
				} else
					E = null;

				// MyNode nextnode = currentnode.step(N, E, W, S);
				FillTransition ftA = new FillTransition(Duration.millis(1000), currentnode.rectangle, currentnode.color,
						currentnode.step(N, E, W, S).color);
				ftA.setAutoReverse(false);
				parallelTransition.getChildren().add(ftA);
			}
		}
		parallelTransition.play();

		parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				animate();
			}

		});

	}
	
	public static void runFX(){
		
		launch("");
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static class MyNode extends StackPane {
		public Rectangle rectangle;
		public String status = "";
		public Label label;
		public Color color;

		public MyNode(double x, double y, double width, double height) {

			// initialize rectangle
			rectangle = new Rectangle(width, height);
			// rectangle.setStroke(Color.BLACK);
			if (new Random().nextInt(10) > 1) {
				color = Color.GREEN;
				status = "tree";
			} else {
				rectangle.setFill(Color.ANTIQUEWHITE);
				color = Color.WHITE;
				status = "emp";
			}
			// create label
			rectangle.setFill(color);
			label = new Label(status);

			// set position
			setTranslateX(x);
			setTranslateY(y);

			getChildren().add(rectangle);
			getChildren().add(label);
		}

		public MyNode step(MyNode N, MyNode E, MyNode W, MyNode S) {

			//0. if it is empty do nothing
			if(status == "empty")
				return this;
			// 1. change by itself
			if (status == "tree") {
				// 0.2% chance to catch a fire itself
				if (new Random().nextInt(10000) < 100) {
					status = "fire";
					color = Color.RED;
				}

			} else if (status == "fire") {
				// 50% chance to the fire will stop
				if (new Random().nextInt(10) < 2) {
					status = "tree";
					color = Color.GREEN;
				} else if (new Random().nextInt(10) < 5) {
					status = "emp";
					color = Color.WHITE;
				}

			}

			// 2. change caused by neighbor
			// if one of its neighbor is on fire, 50% chance it will catch the
			// fire
			// MyNode N, S, W, E;

			if (status == "tree") {
				if (N != null && N.status == "fire") {
					if (new Random().nextInt(10) < 5) {
						status = "fire";
						color = Color.RED;
					}
				}
			}
			if (status == "tree") {
				if (E != null && E.status == "fire") {
					if (new Random().nextInt(10) < 5) {
						status = "fire";
						color = Color.RED;
					}
				}
			}
			if (status == "tree") {
				if (W != null && W.status == "fire") {
					if (new Random().nextInt(10) < 5) {
						status = "fire";
						color = Color.RED;
					}
				}
			}
			if (status == "tree") {
				if (S != null && S.status == "fire") {
					if (new Random().nextInt(10) < 5) {
						status = "fire";
						color = Color.RED;
					}
				}
			}

			label.setText(status);

			return this;

		}

	}

}