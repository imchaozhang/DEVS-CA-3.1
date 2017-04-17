package view.CAView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import controller.ControllerInterface;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.util.Duration;
import view.View;

public class SpaceView {

	private static ControllerInterface controller;

	private static double sceneWidth = 729;
	private static double sceneHeight = 749;
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
	private static boolean isState = true, isTL = true, isSigma = true, isStatusChanged = true;

	private static int playbackSize = 50;

	private static Scene scene;
	private static Parent ui;
	private static VBox vbox;
	private static Slider playbackControl;

	private double currentTime = 0;

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	@FXML
	private CheckBox PSelect, cb_State, cb_Sigma, cb_TL, cb_StatusChanged;
	@FXML
	private Button PBMaxLengthButton, ANSpeedButton, HideAndShowControlButton;
	@FXML
	private TextField PBMaxLength, PBFrom, PBTo, PBInterval, ANSpeed;
	@FXML
	private Slider PBTracking, ANSpeedSlider;
	@FXML
	private Text PBStatus, CellChangedNumber;
	@FXML
	private HBox hbox;
	@FXML
	private AnchorPane leftP, centerP, right, PlaybackPositioned;
	@FXML
	private Group root;
	@FXML
	private SplitPane ca_split;

	public SpaceView() {

	}

	public static void initial(int i, int j, ControllerInterface c) {

		n = i;
		m = j;
		setGridSize();
		cellView = new CellView[n][m];
		controller = c;

	}

	private static void setGridSize() {
		gridWidth = sceneWidth / n;
		gridHeight = sceneHeight / m;

	}

	public static void reset() {
		stepSpeed = 1;
		count = 0;
		numberOfCellChanged = 0;
		playbackSelected = false;
		playbacked = false;
		sizechanged = false;
		animationPaused = false;
		isState = true;
		isTL = true;
		isSigma = true;
		isStatusChanged = true;

		playback.clear();

	}

	public Scene createScene() throws Exception {

		// define the vbox from FXMLLoader
		ui = FXMLLoader.load(getClass().getResource("ComplexCA.fxml"));

		playbackControl = addPlaybackSilder();

		// Put playbackControl Slider to the right position
		for (Node nodeIn : ((VBox) ui).getChildren()) {
			if (nodeIn instanceof SplitPane) {
				Node nodeIn2 = ((SplitPane) nodeIn).getItems().get(0);
				Node nodeIn3 = ((AnchorPane) nodeIn2).getChildren().get(0);
				Node nodeIn4 = ((VBox) nodeIn3).getChildren().get(1);
				Node nodeIn5 = ((TitledPane) nodeIn4).getContent();
				Node nodeIn6 = ((AnchorPane) nodeIn5).getChildren().get(0);
				((GridPane) nodeIn6).add(playbackControl, 0, 6, 4, 1);
				GridPane.setMargin(playbackControl, new Insets(-6, 0, 0, 0));

				// Node nodeIn7 = ((GridPane) nodeIn6).getChildren().get(14);
				//
				// ((AnchorPane) nodeIn7).getChildren().add(playbackControl);

				break;
			}
		}

		scene = new Scene(ui);

		return scene;

		// animate();

	}

	public void closeScene() {
		((BorderPane) ui).getCenter().setVisible(false);

	}

	public void openScene() {
		((BorderPane) ui).getCenter().setVisible(true);

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
						playbackControl.setMax(playback.size() - 1);
						playbackControl.setValue(playback.size() - 1);

					} else {
						playback.poll();
						playback.add(playbackTransition);
						playbackControl.setValue(playback.size() - 1);

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

	}

	private void refreshCAColor() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				cellView[i][j].refreshNodeColor();

			}
		}

	}

	private Slider addPlaybackSilder() {
		Slider playbackControl = new Slider(0, 0, 0);
		playbackControl.setId("playbackControl");
		playbackControl.setPrefWidth(200);
		// playbackControl.setLayoutX(10);
		// playbackControl.setLayoutY(95);
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

		return playbackControl;

	}

	// FXML Controller

	@FXML
	private void addHBox() {
		hbox.setPadding(new Insets(10));
		hbox.setSpacing(10);

		Text name = new Text("States & Colors:");
		HBox.setMargin(name, new Insets(10, 0, 0, 5));
		hbox.getChildren().add(name);

		Separator sep = new Separator();
		sep.setOrientation(Orientation.VERTICAL);
		hbox.getChildren().add(sep);

		// Mapping the Phase Color to HBox for ColorPicker
		HashMap<String, Color> PhaseColors = CAViewUI.getAllPhaseColor();
		Iterator<Entry<String, Color>> it = PhaseColors.entrySet().iterator();
		// set an index id for phase and color
		int c = 0;
		while (it.hasNext()) {
			Entry<String, Color> pair = it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			Label title = new Label("" + pair.getKey());
			title.setId("" + c);
			ColorPicker color4phase = new ColorPicker((Color) pair.getValue());
			color4phase.setId("" + c);
			color4phase.valueProperty().addListener(new ChangeListener<Color>() {
				public void changed(ObservableValue<? extends Color> ov, Color old_val, Color new_val) {
					CAViewUI.changeColor(pair.getKey().toString(), new_val);
				}
			});

			HBox.setMargin(title, new Insets(10, 0, 0, 5));
			HBox.setMargin(color4phase, new Insets(5, 10, 0, 10));
			hbox.getChildren().addAll(title, color4phase);
			c++;
		}

		// it.remove(); // avoids a ConcurrentModificationException
	}

	@FXML
	protected void actionApplySettingsButton(ActionEvent event) {
		ANSpeed.setText("Sign in button pressed");
		System.out.println("The button was clicked!");
	}

	@FXML
	private void addGroup() {
		// get Group and hbox from FXML

		// set Grid Size
		// sceneWidth = PSelect.getBoundsInParent().getWidth();
		// sceneHeight = centerP.getPrefHeight();
		// System.out.println(sceneWidth+"; "+ sceneHeight);

		// Create context menu for right click the node
		ContextMenu contextMenu = new ContextMenu();
		Menu changeColorSubMenu = new Menu("Change Phase Color");
		String currentPhase = "";

		ColorPicker color4phase = new ColorPicker();
		MenuItem colorPickerItem = new MenuItem(currentPhase, color4phase);

		changeColorSubMenu.getItems().add(colorPickerItem);
		MenuItem copy = new MenuItem("Copy");
		MenuItem paste = new MenuItem("Paste");
		contextMenu.getItems().addAll(changeColorSubMenu, copy, paste);
		contextMenu.setAutoFix(true);
		contextMenu.setAutoHide(true);

		// initialize playfield
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				// create node
				CellView node = new CellView(i, j, gridWidth, gridHeight);

				// add node to group

				root.getChildren().add(node);

				// add to playfield for further reference using an array
				cellView[i][j] = node;

				currentPhase = node.status;
				color4phase.setValue(CAViewUI.getColor(currentPhase));

				node.setOnMousePressed(new EventHandler<MouseEvent>() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void handle(MouseEvent t) {
						Node mynode = (Node) t.getSource();
						if (t.isSecondaryButtonDown()) {
							contextMenu.show(mynode, mynode.getScene().getWindow().getX() + t.getSceneX(),
									mynode.getScene().getWindow().getY() + t.getSceneY());
							String currentPhase = node.status;
							colorPickerItem.setText(currentPhase);
							color4phase.setValue(CAViewUI.getColor(currentPhase));
							color4phase.setOnAction(new EventHandler() {
								@Override
								public void handle(Event event) {
									CAViewUI.changeColor(currentPhase, color4phase.getValue());
									refreshCAColor();
								}
							});

						} else {
							// node.tp.setText(""+node.getWidth());
							node.setTPText(isState, isTL, isSigma, isStatusChanged);
							node.tp.show(mynode, mynode.getScene().getWindow().getX() + t.getSceneX(),
									mynode.getScene().getWindow().getY() + t.getSceneY());
						}
					}
				});

				node.setOnMouseReleased(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// node.tp.hide();

					}

				});

			}
		}

	}

	@FXML
	public void initialize() {

		// addHBox();

		addGroup();

		// resize the cell node
		centerP.widthProperty().addListener((observable, oldValue, newValue) -> {
			sceneWidth = (double) newValue;
			setGridSize();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					cellView[i][j].changeWidth(gridWidth);
					cellView[i][j].setTranslateX(i * gridWidth);

				}
			}

		});

		cb_State.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isState = new_val;
			}
		});

		cb_Sigma.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isSigma = new_val;
			}
		});

		cb_TL.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isTL = new_val;
			}
		});

		cb_Sigma.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isStatusChanged = new_val;
			}
		});

		PSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				playbackSelected = new_val;
				playbackControl.setDisable(!new_val);
			}
		});

		PBMaxLength.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (Double.parseDouble(newValue) > 0 && Double.parseDouble(newValue) < 50000)
					PBTracking.setMax(Double.parseDouble(newValue));
			} catch (Exception e) {
				if (!newValue.isEmpty())
					System.out.println("Wrong input");
				PBMaxLength.setPromptText("Wrong");
			}
		});

		// PBTracking.valueProperty().addListener(new ChangeListener<Number>() {
		// public void changed(ObservableValue<? extends Number> ov, Number
		// old_val, Number new_val) {
		// if (PBTracking.getMax() > 0 && !sizechanged) {
		// // if (executor.isTerminated()) {
		// playback.get((int) PBTracking.getValue()).play();
		// playbacked = true;
		// PBStatus.setText(
		// String.format("%.0f", (currentTime - playback.size() + 1 + (double)
		// new_val.longValue())));
		// if (PBTracking.getMax() < 20) {
		// // playbackControl.setMax(playback.size());
		// // playbackControl.autosize();
		// }
		//
		// // }
		// }
		// }
		// });

	}

	@FXML
	protected void actionRun(ActionEvent event) {

		controller.userGesture(controller.SIM_RUN_GESTURE, null);
	}

	@FXML
	protected void actionStep(ActionEvent event) {
		controller.userGesture(controller.SIM_STEP_GESTURE, null);
	}

	@FXML
	protected void actionStepN(ActionEvent event) {
		TextInputDialog stepSizeInput = new TextInputDialog();
		stepSizeInput.setTitle("Step N");
		stepSizeInput.setHeaderText(null);
		stepSizeInput.setContentText("Number of steps to iterate: ");
		Optional<String> s = stepSizeInput.showAndWait();
		if (s.isPresent()) {
			try {
				Integer i = new Integer(s.get());
				controller.userGesture(controller.SIM_STEPN_GESTURE, i);
			} catch (Exception exp) {
				System.err.println(exp);
			}
		}
	}

	@FXML
	protected void actionPause(ActionEvent event) {
		controller.userGesture(controller.SIM_PAUSE_GESTURE, null);
	}

	@FXML
	protected void actionReset(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Reset Everything");
		alert.setHeaderText("Reset this Model?");
		alert.setContentText("All Tracking Data Will Be Lost.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			controller.userGesture(controller.SIM_RESET_GESTURE, null);

		}
	}

	@FXML
	protected void hideAndShowControl(ActionEvent event) {
		//Double propValue = ca_split.getDividers().get(0).positionProperty().doubleValue(); 
		Ellipse ellipse = new Ellipse();		
		ca_split.getDividers().get(0).positionProperty().bindBidirectional(ellipse.opacityProperty());
		if (HideAndShowControlButton.getText().equalsIgnoreCase("Hide Control")) {
			FadeTransition dt = new FadeTransition(Duration.millis(800), ellipse);
			dt.setFromValue(0.2818);
			dt.setToValue(0);
			dt.play();			
			HideAndShowControlButton.setText("Show Control");
			
			
		}
		else{
			FadeTransition dt = new FadeTransition(Duration.millis(800), ellipse);
			dt.setFromValue(0);
			dt.setToValue(0.2818);
			dt.play();	
			HideAndShowControlButton.setText("Hide Control");
			
		}
		
	}

	@FXML
	private void changeSliderSize(double size) {
		PBTracking.setMax(size);

	}

	@FXML
	private void changeSliderValue(double value) {

		PBTracking.setValue(value);
	}

}
