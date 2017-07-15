package view.CAView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.text.MutableAttributeSet;
import controller.ControllerInterface;
import facade.simulation.FSimulator;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.View;
import view.CAView.FXMLComponents.CATimeViewControlMenuController;

public class SpaceView {

	private static ControllerInterface controller;
	private static String name;

	private static double sceneWidth = 729;
	private static double sceneHeight = 800;
	public static CellView[][] cellView;
	static double gridWidth, gridHeight;
	static int n, m;
	public static LinkedList<ParallelTransition> playback = new LinkedList<ParallelTransition>();

	public static LinkedList<Double> PBStatusIndex = new LinkedList<Double>();

	// real time factors
	private static final double[] REAL_TIME_FACTORS = { 0.0001, 0.001, 0.01, 0.1, 0.5, 1, 5, 10, 50, 100, 1000 };

	// public static Slider slider;
	private static int stepSpeed = 1;
	private static long count = 0;
	private static long numberOfCellChanged = 0;

	private static boolean playbackSelected = false;
	private static boolean playbacked = false;
	private static boolean sizechanged = false;
	private static boolean animationPaused = false;
	// atStartPoint is used to check if the simulation just start.
	public static boolean atStartPoint = true;

	private static boolean isPhase = true, isSigma = true, isStateChanged = true;

	private static int playbackSize = 50;

	private static Scene scene;
	private static Parent ui;
	private static VBox vbox;
	private static Slider playbackControl, realTimeControl;

	private double CAcurrentTime = 0;

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	private Text simulatorStateDoc, numberCellChangedState, RealTimeFactor;

	private Label playbackValue;

	private StringProperty playbackValueString;

	private Button btn_run, btn_step, btn_stepn, btn_pause, btn_reset;

	// For FXML

	@FXML
	private CheckBox PSelect, cb_Phase, cb_Sigma, cb_StateChanged;
	@FXML
	private Button PBMaxLengthButton, ANSpeedButton, HideAndShowControlButton;

	@FXML
	private TextField PBMaxLength, PBFrom, PBTo, PBInterval, ANSpeed;
	@FXML
	private Slider PBTracking, ANSpeedSlider;
	@FXML
	private Text PBStatus;

	@FXML
	private HBox hbox;
	@FXML
	private AnchorPane leftP, centerP, right, PlaybackPositioned;
	@FXML
	private Group root;
	@FXML
	private SplitPane ca_split;
	@FXML
	private Text model_name;

	public SpaceView() {

	}

	public static void initial(String _name, int i, int j, ControllerInterface c) {

		name = _name;
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
		isPhase = true;
		isSigma = true;
		isStateChanged = true;
		atStartPoint = true;

		playback.clear();
		PBStatusIndex.clear();

	}

	public Scene createScene() throws Exception {

		// define the vbox from FXMLLoader
		ui = FXMLLoader.load(getClass().getResource("ComplexCA.fxml"));

		playbackControl = addPlaybackSilder();

		addControlButtons();

		RealTimeFactor = new Text("Real Time Factor: ");
		realTimeControl = new Slider(0,REAL_TIME_FACTORS.length,0);
		realTimeControl.setSnapToTicks(true);
		//realTimeControl.setShowTickLabels(true);
		//realTimeControl.setShowTickMarks(true);
		realTimeControl.setMinorTickCount(0);
		realTimeControl.setMajorTickUnit(1);
		

		simulatorStateDoc = new Text();
		simulatorStateDoc.setLineSpacing(8);
		simulatorStateDoc.prefHeight(60);

		numberCellChangedState = new Text();

		synchronizeView();

		// Put playbackControl Slider to the right position
		for (Node nodeIn : ((VBox) ui).getChildren()) {
			if (nodeIn instanceof SplitPane) {
				Node nodeIn2 = ((SplitPane) nodeIn).getItems().get(0);
				Node nodeIn3 = ((AnchorPane) nodeIn2).getChildren().get(0);
				Node nodeIn4 = ((VBox) nodeIn3).getChildren().get(2);
				Node nodeIn5 = ((TitledPane) nodeIn4).getContent();
				Node nodeIn6 = ((AnchorPane) nodeIn5).getChildren().get(0);
				((GridPane) nodeIn6).add(playbackControl, 0, 6, 4, 1);
				GridPane.setMargin(playbackControl, new Insets(-6, 0, 0, 0));

				((GridPane) nodeIn6).add(playbackValue, 3, 7);

				// add Control Buttons and Simulation Doc
				Node nodeIn7 = ((VBox) nodeIn3).getChildren().get(3);
				Node nodeIn8 = ((TitledPane) nodeIn7).getContent();
				Node nodeIn9 = ((AnchorPane) nodeIn8).getChildren().get(0);
				((GridPane) nodeIn9).add(btn_run, 0, 0, 1, 1);
				((GridPane) nodeIn9).add(btn_step, 1, 0, 2, 1);
				((GridPane) nodeIn9).add(btn_stepn, 0, 1, 1, 1);
				((GridPane) nodeIn9).add(btn_pause, 1, 1, 1, 1);
				((GridPane) nodeIn9).add(btn_reset, 2, 1, 1, 1);
				((GridPane) nodeIn9).add(RealTimeFactor, 0, 2, 3, 1);
				((GridPane) nodeIn9).add(realTimeControl, 0, 3, 3, 1);

				((GridPane) nodeIn9).add(simulatorStateDoc, 0, 4, 3, 2);

				GridPane.setMargin(btn_run, new Insets(5));
				GridPane.setMargin(btn_step, new Insets(5));
				GridPane.setMargin(btn_stepn, new Insets(5));
				GridPane.setMargin(btn_pause, new Insets(5));
				GridPane.setMargin(btn_reset, new Insets(5));
				GridPane.setMargin(RealTimeFactor, new Insets(5,0,0,10));
				GridPane.setMargin(realTimeControl, new Insets(-5,5,5,5));
				GridPane.setMargin(simulatorStateDoc, new Insets(0, 0, 0, 10));

				// add Number of Cell Changed
				Node nodeIn10 = ((VBox) nodeIn3).getChildren().get(0);
				Node nodeIn11 = ((TitledPane) nodeIn10).getContent();
				Node nodeIn12 = ((AnchorPane) nodeIn11).getChildren().get(0);
				((GridPane) nodeIn12).add(numberCellChangedState, 3, 3);
				GridPane.setMargin(numberCellChangedState, new Insets(10, 0, 10, -2));

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

		atStartPoint = false;

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
					// CACurrentTime is the current time for the CA system. It
					// is the newest time for the system.
					if (nextnode.currentTime > CAcurrentTime) {
						CAcurrentTime = nextnode.currentTime;
					}
					Color nextcolor = nextnode.currentcolor;
					if (count % stepSpeed == 0) {

						if (playbackSelected) { // if animation is selected, all
												// the cells'
							// transition will be recorded.
							// add a background thread for the filltransition
							// animation, because it is slow.

							Runnable r = new Runnable() {
								public void run() {
									FillTransition ftA = new FillTransition(Duration.millis(40), currentnode.rectangle,
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
				// System.out.println(ftList.toString());

			}

		};

		if (playbackSelected) {
			executor.execute(r1);
		}

		Runnable r2 = new Runnable() {
			public void run() {
				sizechanged = true;
				if (!playbackTransition.getChildren().isEmpty()) {

					if (playback.size() < playbackSize) {
						playback.add(playbackTransition);
						// add time for PB Status Display
						PBStatusIndex.add(CAcurrentTime);
						playbackControl.setMin(1);
						playbackControl.setMax(playback.size());
						playbackControl.setValue(playback.size());
						// playbackControl.valueProperty().set(playback.size() -
						// 1);
						// playbackValue.setText(Double.toString(CAcurrentTime));

					} else {
						playback.poll();
						playback.add(playbackTransition);
						PBStatusIndex.poll();
						PBStatusIndex.add(CAcurrentTime);
						playbackControl.setValue(playback.size());
						// playbackValue.setText(Double.toString(CAcurrentTime));

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

		// synchronizeView();

	}

	private void refreshCAColor() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				cellView[i][j].refreshNodeColor();

			}
		}

	}

	private void addControlButtons() {
		btn_run = new Button("Run");
		btn_step = new Button("Step");
		btn_stepn = new Button("Step(n)");
		btn_pause = new Button("Pause");
		btn_reset = new Button("Reset");

		btn_run.setPrefWidth(100);
		btn_run.setPrefHeight(33);

		btn_step.setPrefWidth(180);
		btn_step.setPrefHeight(33);

		btn_stepn.setPrefWidth(100);
		btn_stepn.setPrefHeight(33);

		btn_pause.setPrefWidth(100);
		btn_pause.setPrefHeight(33);

		btn_reset.setPrefWidth(100);
		btn_reset.setPrefHeight(33);

		btn_run.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userGesture(controller.SIM_RUN_GESTURE, null);
			}
		});

		btn_step.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userGesture(controller.SIM_STEP_GESTURE, null);
			}
		});

		btn_stepn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				TextInputDialog stepSizeInput = new TextInputDialog();
				stepSizeInput.setTitle("Step N");
				stepSizeInput.setHeaderText(null);
				stepSizeInput.setContentText("Number of steps to iterate: ");
				Optional<String> s = stepSizeInput.showAndWait();
				if (s.isPresent()) {
					try {
						Integer i = new Integer(s.get());
						controller.userGesture(controller.SIM_STEPN_GESTURE, i);
						// btn_run.setDisable(true);
					} catch (Exception exp) {
						System.err.println(exp);
					}
				}
			}
		});

		btn_pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userGesture(controller.SIM_PAUSE_GESTURE, null);
			}
		});

		btn_reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Reset Everything");
				alert.setHeaderText("Reset this Model?");
				alert.setContentText("All Tracking Data Will Be Lost.");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					controller.userGesture(controller.SIM_RESET_GESTURE, null);

				}
			}
		});

	}

	private Slider addPlaybackSilder() {
		Slider playbackControl = new Slider(0, 0, 0);
		playbackControl.setId("playbackControl");
		playbackControl.setPrefWidth(200);
		// playbackControl.setLayoutX(10);
		// playbackControl.setLayoutY(95);
		playbackControl.setShowTickLabels(true);
		playbackControl.setShowTickMarks(true);
		playbackControl.setMajorTickUnit(1);
		playbackControl.setSnapToTicks(true);
		playbackControl.setBlockIncrement(1);

		playbackValue = new Label("0");

		playbackValueString = new SimpleStringProperty();

		playbackValue.textProperty().bind(playbackValueString);

		// final Label playbackValue = new Label(
		// "Current Step: " + Long.toString(count - (long)
		// playbackControl.getValue()));

		playbackControl.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if (playbackControl.getMax() > 0 && !sizechanged) {
					// if (executor.isTerminated()) {
					int playI = playbackControl.valueProperty().intValue() - 1;
					if (playI < 0)
						playI = 0;
					playback.get(playI).play();
					playbacked = true;
					playbackValueString.set(PBStatusIndex.get(playI).toString());
					if (playbackControl.getMax() < 50) {
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

	public void synchronizeView() {
		// Corresponds to legal behaviors of the sim
		// run, step, step(n), pause, reset
		final boolean[] INITIAL_PAUSE = { false, false, false, true, false };
		final boolean[] SIMULATING = { true, true, true, false, true };
		final boolean[] END = { true, true, true, true, false };
		final Button[] ctrlButtons = { btn_run, btn_step, btn_stepn, btn_pause, btn_reset };

		boolean[] legalBehavior = INITIAL_PAUSE;
		short state = controller.getSimulator().getCurrentState();

		StringProperty stateValue = new SimpleStringProperty();

		simulatorStateDoc.textProperty().bind(stateValue);

		StringProperty numberOfCellChangedValue = new SimpleStringProperty();
		numberOfCellChangedValue.set(Long.toString(numberOfCellChanged));
		numberCellChangedState.textProperty().bind(numberOfCellChangedValue);

		String stateLabel = "Undefined";

		switch (state) {
		case FSimulator.STATE_INITIAL:
			stateLabel = "Ready";
			// stateAttr = attrSets[INITIAL_ATTR];
			legalBehavior = INITIAL_PAUSE;
			break;
		case FSimulator.STATE_SIMULATING:
			stateLabel = "Simulating";
			// stateAttr = attrSets[SIMULATING_ATTR];
			legalBehavior = SIMULATING;
			break;
		case FSimulator.STATE_PAUSE:
			stateLabel = "Pause";
			// stateAttr = attrSets[PAUSE_ATTR];
			legalBehavior = INITIAL_PAUSE;
			break;
		case FSimulator.STATE_END:
			stateLabel = "End";
			// stateAttr = attrSets[END_ATTR];
			legalBehavior = END;
			break;
		}

		for (int i = 0; i < legalBehavior.length; i++) {
			ctrlButtons[i].setDisable(legalBehavior[i]);
			// View.ButtonControls[i].setEnabled(legalBehavior[i]);
			// View.controlMenus[i].setEnabled(legalBehavior[i]);
		}

		stateValue.set("Simulator State: " + stateLabel + "\nTime of Last Event: "
				+ +Round(controller.getSimulator().getTimeOfLastEvent(), 4) + "\nTime of Next Event: "
				+ Round(controller.getSimulator().getTimeOfNextEvent(), 4));

		if (playbackSelected) {
			playbackValueString.set(Double.toString(CAcurrentTime));
		}

		// writeSimulatorInfo("Simulator State: ",attrSets[HEADER_ATTR]);
		// writeSimulatorInfo(stateLabel,stateAttr);
		// writeSimulatorInfo("\nTime of Last Event: ",attrSets[HEADER_ATTR]);
		// writeSimulatorInfo(""+Round(simulator.getTimeOfLastEvent(),
		// 4),attrSets[TIME_ATTR]);
		// writeSimulatorInfo("\nTime of Next Event: ",attrSets[HEADER_ATTR]);
		// writeSimulatorInfo(""+Round(simulator.getTimeOfNextEvent(),
		// 4),attrSets[TIME_ATTR]);
		// System.out.println("SimView%%%%%%%%%%%%%%%"+
		// simulator.getTimeOfNextEvent());
	}

	/**
	 * Return double value with two decimal points
	 */

	protected double Round(double Rval, int Rpl) {
		if (Rval > Double.MAX_VALUE) {// Infinity
			return Rval;
		} else {
			double p = (double) Math.pow(10, Rpl);
			Rval = Rval * p;
			double tmp = Math.round(Rval);
			return (double) tmp / p;
		}
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
		MenuItem caTimeView = new MenuItem("CA Time View");
		MenuItem paste = new MenuItem("Paste");
		contextMenu.getItems().addAll(changeColorSubMenu, caTimeView, paste);
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

							caTimeView.setOnAction(new EventHandler() {
								@Override
								public void handle(Event event) {
									boolean okClicked = showCATimeViewControlDialog(node);
									if (okClicked && node.isCATimeViewSelected) {
										node.setCATimeViewGraphs();
										node.isTimeViewWindowOpen = true;
									}
								}
							});

						} else {
							// node.tp.setText(""+node.getWidth());
							node.setTPText(isPhase, isSigma, isStateChanged);
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

	public boolean showCATimeViewControlDialog(CellView node) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("./FXMLComponents/CATimeViewControlMenu.fxml"));
			// initializing the controller
			CATimeViewControlMenuController popupController = new CATimeViewControlMenuController(node);
			loader.setController(popupController);
			VBox layout = (VBox) loader.load();
			Scene innerScene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = popupController.getDialogStage();

			if (scene.getWindow() != null) {
				popupStage.initOwner(scene.getWindow());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(innerScene);
			popupStage.showAndWait();

			return popupController.isOkClicked();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	@FXML
	public void initialize() {
		// set the model name for simulator UI
		model_name.setText("Model Running: \"" + name + "\"");

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

		cb_Phase.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isPhase = new_val;
			}
		});

		cb_Sigma.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isSigma = new_val;
			}
		});

		cb_StateChanged.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				isStateChanged = new_val;
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
	protected void hideAndShowControl(ActionEvent event) {
		// Double propValue =
		// ca_split.getDividers().get(0).positionProperty().doubleValue();
		Ellipse ellipse = new Ellipse();
		ca_split.getDividers().get(0).positionProperty().bindBidirectional(ellipse.opacityProperty());
		if (HideAndShowControlButton.getText().equalsIgnoreCase("Hide Control")) {
			FadeTransition dt = new FadeTransition(Duration.millis(800), ellipse);
			dt.setFromValue(0.34);
			dt.setToValue(0);
			dt.play();
			HideAndShowControlButton.setText("Show Control");

		} else {
			FadeTransition dt = new FadeTransition(Duration.millis(800), ellipse);
			dt.setFromValue(0);
			dt.setToValue(0.34);
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