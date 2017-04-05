package view.CAView;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class SpaceView2 {

	private static double sceneWidth = 900;
	private static double sceneHeight = 700;
	public static CellView[][] cellView;
	static double gridWidth, gridHeight;
	static int n, m;
	public static LinkedList<ParallelTransition> playback = new LinkedList<ParallelTransition>();
	// public static Slider slider;
	private static int stepSpeed = 1;
	private static long count = 0;
	private static long numberOfCellChanged = 0;
	private StringProperty numberOfCellChangedValue = new SimpleStringProperty("0");

	private static boolean playbackSelected = false;
	private static boolean playbacked = false;
	private static boolean sizechanged = false;
	private static boolean animationPaused = false;

	private static int playbackSize = 50;

	private static Scene scene;
	private static BorderPane border;
	private static HBox hbox;
	private static VBox vbox;

	private double currentTime = 0;

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

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

		scene = new Scene(border, sceneWidth + vbox.getPrefWidth(), sceneHeight + hbox.getPrefHeight());
		// System.out.println("w: " + vbox.getPrefWidth() + ";h: " +
		// hbox.getPrefHeight());

		return scene;

		// animate();

	}

	public void closeScene() {
		border.getCenter().setVisible(false);

	}

	public void openScene() {
		border.getCenter().setVisible(true);

	}

	public void animate() {
		// return the states to the lastest one to make sure the playback will
		// not interrupt the current animation
		if (playbacked) {
			playback.get(playback.size() - 1).play();
			playbacked = false;
		}

		// count the number of cells that have changed during the designated
		// period
		numberOfCellChanged = 0;

		// ExecutorService executor = Executors.newSingleThreadExecutor();

		// only transition for playback
		ParallelTransition playbackTransition = new ParallelTransition();
		LinkedList<FillTransition> ftList = new LinkedList<FillTransition>();

		for (int ai = 0; ai < n; ai++) {
			for (int aj = 0; aj < m; aj++) {
				CellView currentnode = cellView[ai][aj];
				// currentnode.setCache(true);
				// currentnode.setCacheHint(CacheHint.SPEED);
				Color previouscolor = currentnode.previouscolor;
				if (!currentnode.isDatalistEmp()) {
					CellView nextnode = currentnode.step();
					currentTime = nextnode.currentTime;
					Color nextcolor = nextnode.currentcolor;
					if (count % stepSpeed == 0) {

						if (playbackSelected) { // if animation is selected, all
												// the cells'
							// transition will be recorded.
							// add a background thread for the filltransition
							// animation, because it is slow.

							Runnable r = new Runnable() {
								public void run() {
									FillTransition ftA = new FillTransition(Duration.millis(500), currentnode.rectangle,
											previouscolor, nextcolor);
									ftA.setAutoReverse(false);
									// ftA.setCycleCount(2);
									ftList.add(ftA);
								}
							};

							executor.execute(r);

							// new Thread(r).run();

						}
						if (nextcolor != previouscolor) {

							numberOfCellChanged++;

						}
						if (!animationPaused) {
							cellView[ai][aj].rectangle.setFill(nextcolor);
							cellView[ai][aj].previouscolor = nextcolor;
						}

					}
				}

			}
		}

		Runnable r1 = new Runnable() {
			public void run() {
				if (!ftList.isEmpty())
					playbackTransition.getChildren().addAll(ftList);

			}

		};

		if (playbackSelected) {
			executor.execute(r1);
		}

		Runnable r2 = new Runnable() {
			public void run() {
				sizechanged = true;
				if (!playbackTransition.getChildren().isEmpty()) {

					if (playback.size() <= playbackSize) {
						playback.add(playbackTransition);
						for (Node nodeIn : vbox.getChildren()) {
							if (nodeIn.getId() == "playbackControl") {
								((Slider) nodeIn).setMax(playback.size() - 1);
								((Slider) nodeIn).setValue(playback.size() - 1);
							}

						}
					} else {
						playback.poll();
						playback.add(playbackTransition);
						for (Node nodeIn : vbox.getChildren()) {
							if (nodeIn.getId() == "playbackControl") {
								((Slider) nodeIn).setValue(playback.size() - 1);
							}
						}
					}
				}
				sizechanged = false;
			}

		};

		if (playbackSelected) {
			executor.execute(r2);
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

		count++;

		// for (Node nodeIn : hbox.getChildren()) {
		// if (nodeIn.getId() == "numberOfCellChangedLabel") {
		// ((Label) nodeIn).setText("Number of Cells Changed: " +
		// Long.toString(numberOfCellChanged));
		//
		// }
		// }

	}

	public HBox addHBox() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");
		hbox.setPrefHeight(60);

		Button animationControl = new Button("Pause Animation");
		animationControl.setPrefSize(160, 20);

		animationControl.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (animationControl.getText() == "Pause Animation") {
					animationControl.setText("Resume Animation");
					animationPaused = true;
					closeScene();

				} else {
					animationControl.setText("Pause Animation");
					animationPaused = false;
					openScene();

				}
			}
		});

		final Label numberOfCellChangedLabel = new Label(
				"Number of Cells Changed: " + Long.toString(numberOfCellChanged));
		numberOfCellChangedLabel.setTextFill(Color.WHITE);
		// numberOfCellChangedLabel.setAlignment(Pos.BOTTOM_CENTER);
		numberOfCellChangedLabel.setTextAlignment(TextAlignment.CENTER);
		numberOfCellChangedLabel.setId("numberOfCellChangedLabel");
		numberOfCellChangedLabel.textProperty().bind(numberOfCellChangedValue);

		Button checkNumOfCellChanged = new Button("Check Number Of Cells Changed");
		checkNumOfCellChanged.setPrefSize(200, 20);

		checkNumOfCellChanged.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				numberOfCellChangedValue.setValue(numberOfCellChanged + "");
			}
		});

		hbox.getChildren().addAll(animationControl, checkNumOfCellChanged, numberOfCellChangedLabel);

		return hbox;
	}

	public VBox addVBox() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(8);
		vbox.setMaxWidth(20);

		FlowPane playbackStack = new FlowPane();
		playbackStack.setMaxWidth(230);
		

		GroupPane playbackGroup = new GroupPane("Playback Configuration", playbackStack, new Insets(30));

//		Text title = new Text("Data");
//		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//		playbackStack.getChildren().add(title);
//
//		Hyperlink options[] = new Hyperlink[] { new Hyperlink("Sales"), new Hyperlink("Marketing"),
//				new Hyperlink("Distribution"), new Hyperlink("Costs") };
//
//		for (int i = 0; i < 4; i++) {
//			playbackStack.setMargin(options[i], new Insets(0, 0, 0, 8));
//			playbackStack.getChildren().add(options[i]);
//		}

		vbox.getChildren().add(playbackGroup);

		Slider playbackControl = new Slider(0, 0, 0);
		playbackControl.setId("playbackControl");
		playbackControl.setPrefWidth(200);
//		playbackControl.setLayoutX(10);
//		playbackControl.setLayoutY(95);
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
					// if (executor.isTerminated()) {
					playback.get((int) playbackControl.getValue()).play();
					playbacked = true;
					playbackValue.setText("Current Step: " + String.format("%.0f",
							(currentTime - playback.size() + 1 + (double) new_val.longValue())));
					if (playbackControl.getMax() < 20) {
						// playbackControl.setMax(playback.size());
						// playbackControl.autosize();
					}

					// }
				}
			}
		});

		playbackControl.setDisable(true); // disable the playback by default

		Slider playbackSizeControl = new Slider(50, 200, 0);
		playbackSizeControl.setId("playbackSizeControl");
		playbackSizeControl.setPrefWidth(200);
		playbackSizeControl.setShowTickLabels(true);
		playbackSizeControl.setShowTickMarks(true);
		playbackSizeControl.setMajorTickUnit(50);
		playbackSizeControl.setMinorTickCount(0);
		playbackSizeControl.setSnapToTicks(true);
		playbackSizeControl.setBlockIncrement(50);

		final Label playbackSizeLabel = new Label("Playback Size: " + (int) playbackSizeControl.getValue());

		playbackSizeControl.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				playbackSize = (int) playbackSizeControl.getValue();
				playbackSizeLabel.setText("Playback Size: " + (int) new_val.longValue());

			}
		});

		playbackSizeControl.setDisable(true);
		playbackSizeLabel.setVisible(false);

		CheckBox cb_playbackSelect = new CheckBox("Playback");
		cb_playbackSelect.setSelected(false);
		cb_playbackSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				playbackSelected = new_val;
				playbackControl.setDisable(!new_val);
				playbackSizeControl.setDisable(!new_val);
				playbackSizeLabel.setVisible(new_val);
			}
		});

		playbackStack.getChildren().add(cb_playbackSelect);

		playbackStack.getChildren().add(playbackSizeControl);

		playbackStack.getChildren().add(playbackSizeLabel);

		playbackStack.setMargin(playbackControl, new Insets(10));
		playbackStack.getChildren().add(playbackControl);
		playbackStack.getChildren().add(playbackValue);

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
