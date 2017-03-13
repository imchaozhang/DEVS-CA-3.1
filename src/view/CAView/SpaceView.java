package view.CAView;

import java.util.LinkedList;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SpaceView {

	private static double sceneWidth = 1200;
	private static double sceneHeight = 800;
	public static CellView[][] cellView;
	static double gridWidth, gridHeight;
	static int n, m;
	public static LinkedList<ParallelTransition> playback = new LinkedList<ParallelTransition>();
	// public static Slider slider;
	private static int stepSpeed = 1;
	private static long count = 0;

	private static boolean playbackSelected = false;
	private static boolean playbacked = false;
	private static boolean sizechanged = false;

	private static BorderPane border;
	private static HBox hbox;
	private static VBox vbox;
	
	private double currentTime = 0;

	// private static int indexOfPlaybackControl;

	public static void initial(int i, int j) {

		// super();
		n = i;
		m = j;
		gridWidth = sceneWidth / n;
		gridHeight = sceneHeight / m;

		cellView = new CellView[n][m];

	}

	public Scene createScene() {

		border = new BorderPane();

		hbox = addHBox();
		vbox = addVBox();
		border.setTop(hbox);
		border.setLeft(vbox);

		// Group root = new Group();

		Group root = new Group();
		border.setCenter(root);

		// initialize playfield
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				// create node
				CellView node = new CellView(i, j, gridWidth, gridHeight);

				// add node to group
				root.getChildren().add(node);

				// add to playfield for further reference using an array
				cellView[i][j] = node;

				node.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent t) {
						Node mynode = (Node) t.getSource();
						node.tp.show(mynode, mynode.getScene().getWindow().getX() + t.getSceneX(),
								mynode.getScene().getWindow().getY() + t.getSceneY());
					}
				});

				node.setOnMouseReleased(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						node.tp.hide();

					}

				});

			}
		}

		Scene scene = new Scene(border, sceneWidth + vbox.getPrefWidth(), sceneHeight + hbox.getPrefHeight());
		// System.out.println("w: " + vbox.getPrefWidth() + ";h: " +
		// hbox.getPrefHeight());

		return scene;

		// animate();

	}

	public void closeScene() {

	}

	public void animate() {
		// return the states to the lastest one to make sure the playback will
		// not interrupt the current animation
		if (playbacked) {
			playback.get(playback.size() - 1).play();
			playbacked = false;
		}

		// only transition for playback
		ParallelTransition playbackTransition = new ParallelTransition();

		for (int ai = 0; ai < n; ai++) {
			for (int aj = 0; aj < m; aj++) {
				CellView currentnode = cellView[ai][aj];
				Color previouscolor = currentnode.previouscolor;
				if (!currentnode.isDatalistEmp()) {
					CellView nextnode = currentnode.step();
					currentTime = nextnode.currentTime;
					Color nextcolor = nextnode.currentcolor;
					if (count % stepSpeed == 0) {
						if (playbackSelected) { // if animation is selected, all
												// the cells'
							// transition will be recorded
							FillTransition ftA = new FillTransition(Duration.millis(500), currentnode.rectangle,
									previouscolor, nextcolor);
							ftA.setAutoReverse(false);

							playbackTransition.getChildren().add(ftA);
						}
						if (nextcolor != previouscolor) {
							cellView[ai][aj].rectangle.setFill(nextcolor);
							cellView[ai][aj].previouscolor = nextcolor;
						}

					}
				}

			}
		}
		if (!playbackTransition.getChildren().isEmpty()) {

			if (playback.size() <= 20) {
				playback.add(playbackTransition);
				for (Node nodeIn : vbox.getChildren()) {
					if (nodeIn.getId() == "playbackControl") {
						((Slider) nodeIn).setMax(playback.size() - 1);
						sizechanged = true;
						((Slider) nodeIn).setValue(playback.size() - 1);
						sizechanged = false;
					}

				}
			} else {
				playback.poll();
				playback.add(playbackTransition);
				for (Node nodeIn : vbox.getChildren()) {
					if (nodeIn.getId() == "playbackControl") {
						sizechanged = true;
						((Slider) nodeIn).setValue(playback.size() - 1);
						sizechanged = false;
					}
				}
			}
		}
		// playbackTransition.play();
		//
		// playbackTransition.setOnFinished(new EventHandler<ActionEvent>() {
		// @Override
		// public void handle(ActionEvent event) {
		// // animate();
		//
		// }
		//
		// });

		for (Node nodeIn : vbox.getChildren()) {
			if (nodeIn.getId() == "playbackControl") {

			}

		}

		count++;

	}

	public HBox addHBox() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");
		hbox.setPrefHeight(60);

		Button buttonCurrent = new Button("Current");
		buttonCurrent.setPrefSize(100, 20);

		Button buttonProjected = new Button("Projected");
		buttonProjected.setPrefSize(100, 20);
		hbox.getChildren().addAll(buttonCurrent, buttonProjected);

		return hbox;
	}

	public VBox addVBox() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(8);
		vbox.setPrefWidth(250);

		Text title = new Text("Data");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		vbox.getChildren().add(title);

		Hyperlink options[] = new Hyperlink[] { new Hyperlink("Sales"), new Hyperlink("Marketing"),
				new Hyperlink("Distribution"), new Hyperlink("Costs") };

		for (int i = 0; i < 4; i++) {
			VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
			vbox.getChildren().add(options[i]);
		}

		Slider playbackControl = new Slider(0, 0, 0);
		playbackControl.setId("playbackControl");
		playbackControl.setLayoutX(10);
		playbackControl.setLayoutY(95);
		playbackControl.setShowTickLabels(true);
		playbackControl.setShowTickMarks(true);
		playbackControl.setMajorTickUnit(5);
		playbackControl.setSnapToTicks(true);
		playbackControl.setBlockIncrement(1);

		final Label playbackValue = new Label(
				"Current Step: " + Long.toString(count - (long) playbackControl.getValue()));

		playbackControl.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if (playbackControl.getMax() > 0 && !sizechanged) {
					playback.get((int) playbackControl.getValue()).play();
					playbacked = true;
					playbackValue.setText(
							"Current Step: " + String.format("%.0f", (currentTime - playback.size()+1 + (double) new_val.longValue())));
					if (playbackControl.getMax() < 20) {
						// playbackControl.setMax(playback.size());
						// playbackControl.autosize();
					}

				}
			}
		});

		playbackControl.setDisable(true); // disable the playback by default

		CheckBox cb_playbackSelect = new CheckBox("Playback");
		cb_playbackSelect.setSelected(false);
		cb_playbackSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				playbackSelected = new_val;
				playbackControl.setDisable(!new_val);
			}
		});

		vbox.getChildren().add(cb_playbackSelect);

		vbox.setMargin(playbackControl, new Insets(10));
		vbox.getChildren().add(playbackControl);
		vbox.getChildren().add(playbackValue);

		Slider stepSpeedControl = new Slider(1, 9, 1);
		stepSpeedControl.setId("stepSpeedControl");
		stepSpeedControl.setLayoutX(10);
		stepSpeedControl.setLayoutY(125);
		// stepSpeedControl.setCenterShape(true);
		stepSpeedControl.setShowTickLabels(true);
		stepSpeedControl.setShowTickMarks(true);
		stepSpeedControl.setMajorTickUnit(4);
		stepSpeedControl.setSnapToTicks(true);
		final Label stepSpeedValue = new Label(
				"CA Animation Speed: " + Integer.toString((int) stepSpeedControl.getValue()));

		// stepSpeedControl.setBlockIncrement(1);

		stepSpeedControl.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				stepSpeed = (int) stepSpeedControl.getValue();
				stepSpeedValue.setText("CA Animation Speed: " + String.format("%.0f", new_val));
			}
		});

		vbox.setMargin(stepSpeedControl, new Insets(10, 10, 0, 10));
		vbox.setMargin(stepSpeedValue, new Insets(0, 0, 0, 40));
		vbox.getChildren().add(stepSpeedControl);
		vbox.getChildren().add(stepSpeedValue);

		return vbox;
	}

}
