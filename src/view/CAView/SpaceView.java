package view.CAView;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import controller.ControllerInterface;
import facade.simulation.FSimulator;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.CAView.FXMLComponents.CATimeViewControlMenuController;

public class SpaceView {

	private static ControllerInterface controller;
	private static String name;

	private static double sceneWidth = 400;
	private static double sceneHeight = 400;
	public static CellView[][] cellView;

	static double gridWidth, gridHeight;
	static int n, m;
	public static LinkedList<ParallelTransition> playback = new LinkedList<ParallelTransition>();

	public static LinkedList<Double> PBStatusIndex = new LinkedList<Double>();

	// real time factors
	private static final double[] REAL_TIME_FACTORS = { 0.0001, 0.001, 0.01, 0.1, 0.5, 1, 5, 10, 50, 100, 1000 };

	private static int stepSpeed = 1;
	private static long count = 0;
	private static long numberOfCellChanged = 0;

	private static boolean playbackSelected = false, animationSelected = true;
	private static boolean playbacked = false;
	private static boolean sizechanged = false;
	private static boolean animationPaused = false;
	// atStartPoint is used to check if the simulation just start.
	public static boolean atStartPoint = true;

	// private static ToggleSwitch AnimationSwitch;
	// private static BooleanProperty animationOn;

	private static boolean isPhase = true, isSigma = true, isStateChanged = true;

	private static int playbackSize = 30;
	private static int trackingInterval = 1;

	private static Scene scene;
	private static Parent ui;
	private static VBox vbox;
	private static Slider playbackControl, realTimeControl;

	private double CAcurrentTime = 0;

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	private Text simulatorStateDoc, numberCellChangedState;

	private Label playbackValue, RealTimeFactor;

	private StringProperty playbackValueString;

	private Button btn_run, btn_step, btn_stepn, btn_pause, btn_reset;

	// for Area Selection
	private int[] topleft = { 0, 0 };
	private int[] bottomright = { cellView.length, cellView[0].length };
	private static int xCellsNumber, yCellsNumber;
	// private Group root2;

	// For FXML

	@FXML
	private CheckBox ANSelect, PSelect, cb_Phase, cb_Sigma, cb_StateChanged;
	@FXML
	private Button PBMaxLengthButton, ANSpeedButton, HideAndShowControlButton, AreaSelectFunctionButton;

	@FXML
	private TextField PBMaxLength, PBInterval, ANSpeed, tlX, tlY, brX, brY;
	@FXML
	private Slider PBTracking, ANSpeedSlider;
	@FXML
	private Text PBStatus;
	@FXML
	private TextArea consoleText;
	@FXML
	private HBox hbox;
	@FXML
	private AnchorPane leftP, centerP, groupP, consoleP, PlaybackPositioned, groupScrollP;
	@FXML
	private Group root;
	@FXML
	private SplitPane ca_split, console_split;
	@FXML
	private Text model_name;
	@FXML
	private Tab consoleTab;
	@FXML
	private TabPane DisplayTabs;
	@FXML
	private ScrollPane groupScrollView;

	public SpaceView() {

	}

	public static void initial(String _name, int i, int j, ControllerInterface c) {

		name = _name;
		n = i;
		m = j;
		xCellsNumber = n;
		yCellsNumber = m;
		// setGridSize();
		gridHeight = gridWidth = Math.min(sceneWidth / n, sceneHeight / m);
		cellView = new CellView[n][m];
		controller = c;

	}

	private static void setGridSize() {
		gridHeight = gridWidth = Math.min(sceneWidth / n, sceneHeight / m);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				cellView[i][j].changeWidth(gridWidth);
				cellView[i][j].setTranslateX(i * gridWidth);
				cellView[i][j].changeHeight(gridHeight);
				cellView[i][j].setTranslateY(j * gridHeight);
			}
		}

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

		// //add animation switch
		// AnimationSwitch = new ToggleSwitch();
		// //Since the Switch initially is on false, we fire a mouse click event
		// for itself
		// AnimationSwitch.fireEvent( new MouseEvent(MouseEvent.MOUSE_CLICKED,
		// 0,
		// 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
		// true, true, true, true, true, true, null));
		// animationOn = AnimationSwitch.switchedOnProperty();
		// AnimationSwitch.setScaleX(0.9);
		// AnimationSwitch.setScaleY(0.9);

		playbackControl = addPlaybackSilder();

		addControlButtons();

		realTimeControl = addRealTimeControl();

		simulatorStateDoc = new Text();
		simulatorStateDoc.setLineSpacing(8);
		simulatorStateDoc.prefHeight(60);

		numberCellChangedState = new Text();

		// root2 = new Group();
		// addGroup();

		synchronizeView();

		// Put playbackControl Slider to the right position
		for (Node nodeIn : ((VBox) ui).getChildren()) {
			if (nodeIn instanceof SplitPane) {
				Node nodeIn2 = ((SplitPane) nodeIn).getItems().get(0);

				// Node nodeInGroup = ((SplitPane) nodeIn).getItems().get(1);
				//
				// HiddenSidesPane pane = new HiddenSidesPane();
				// pane.setContent(root2);
				//
				// TextArea ta = new TextArea();
				// ta.setEditable(false);
				// JXConsoleComponent console = new JXConsoleComponent(ta);
				// PrintStream ps = new PrintStream(console, true);
				// System.setOut(ps);
				// System.setErr(ps);
				// ta.setOnMouseClicked(new EventHandler<MouseEvent>() {
				// @Override
				// public void handle(MouseEvent event) {
				// if (pane.getPinnedSide() != null) {
				// pane.setPinnedSide(null);
				// } else {
				// pane.setPinnedSide(Side.RIGHT);
				// }
				// }
				// });;
				//
				//
				//
				// pane.setRight(ta);
				// //pane.setPinnedSide(Side.RIGHT);
				// pane.setTriggerDistance(20);
				//
				// ((AnchorPane) nodeInGroup).getChildren().add(pane);

				Node nodeIn3 = ((AnchorPane) nodeIn2).getChildren().get(0);
				Node nodeIn4 = ((VBox) nodeIn3).getChildren().get(2);
				Node nodeIn5 = ((TitledPane) nodeIn4).getContent();
				Node nodeIn6 = ((AnchorPane) nodeIn5).getChildren().get(0);
				((GridPane) nodeIn6).add(playbackControl, 0, 4, 4, 1);
				GridPane.setMargin(playbackControl, new Insets(-6, 0, 0, 0));

				((GridPane) nodeIn6).add(playbackValue, 3, 5);
				GridPane.setMargin(playbackValue, new Insets(-6, 0, 0, 0));

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
				GridPane.setMargin(RealTimeFactor, new Insets(5, 0, 0, 10));
				GridPane.setMargin(realTimeControl, new Insets(-5, 5, 5, 5));
				GridPane.setMargin(simulatorStateDoc, new Insets(0, 0, 0, 10));

				// add Number of Cell Changed
				Node nodeIn10 = ((VBox) nodeIn3).getChildren().get(0);
				Node nodeIn11 = ((TitledPane) nodeIn10).getContent();
				Node nodeIn12 = ((AnchorPane) nodeIn11).getChildren().get(0);
				((GridPane) nodeIn12).add(numberCellChangedState, 3, 3);
				GridPane.setMargin(numberCellChangedState, new Insets(10, 0, 10, -2));

				// add animation switch
				Node nodeIn13 = ((VBox) nodeIn3).getChildren().get(1);
				Node nodeIn14 = ((TitledPane) nodeIn13).getContent();
				Node nodeIn15 = ((AnchorPane) nodeIn14).getChildren().get(0);
				// ((GridPane) nodeIn15).add(AnimationSwitch, 1, 0);
				// GridPane.setMargin(AnimationSwitch, new Insets(0, 0, 5, 5));

				break;
			}
		}

		scene = new Scene(ui);

		return scene;

		// animate();

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
		playbackControl.setMinorTickCount(0);
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

	private Slider addRealTimeControl() {

		Slider _realTimeControl = new Slider(0, REAL_TIME_FACTORS.length - 1, 1);
		_realTimeControl.setSnapToTicks(true);
		_realTimeControl.setMinorTickCount(0);
		_realTimeControl.setMajorTickUnit(1);

		Double currentValue = _realTimeControl.getValue();
		RealTimeFactor = new Label("Real Time Factor: " + REAL_TIME_FACTORS[currentValue.intValue()]);

		// set the inital real time factor based on the slider inu=itial value
		controller.userGesture(controller.SIM_SET_RT_GESTURE, REAL_TIME_FACTORS[currentValue.intValue()]);

		_realTimeControl.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				RealTimeFactor.setText("Real Time Factor: " + Double.toString(REAL_TIME_FACTORS[new_val.intValue()]));
				controller.userGesture(controller.SIM_SET_RT_GESTURE, REAL_TIME_FACTORS[new_val.intValue()]);

			}
		});

		return _realTimeControl;

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
						animationPaused = false;

					} else {

						animationPaused = true;
					}

					if (nextcolor != previouscolor) {

						numberOfCellChanged++;

					}
					// if (!animationPaused && animationOn.get()) {
					/*
					 * Since Group root will be invisible when animation switch
					 * is off, these two conditions will do the same.
					 */
					if (!animationPaused && animationSelected) {
						cellView[ai][aj].rectangle.setFill(nextcolor);
						cellView[ai][aj].previouscolor = nextcolor;
					} else {
						cellView[ai][aj].rectangle.setFill(nextcolor);
						cellView[ai][aj].previouscolor = nextcolor;
					}

					if (playbackSelected && count % trackingInterval == 0) {
						// if animation is selected and at the tracking interval
						// step, all the cells' transition will be recorded.
						// add a background thread for the filltransition
						// animation, because this can be efficient.
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
	protected void actionANSpeedSetting(ActionEvent event) {
		Double _animationSpeed;
		try {
			_animationSpeed = Double.parseDouble(ANSpeed.getText());
		} catch (Exception e) {
			_animationSpeed = -1.0;
		}
		if (_animationSpeed >= 1 && _animationSpeed <= 100) {
			ANSpeedSlider.setValue(_animationSpeed);

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error: Animation Speed Setting is not accepted");
			alert.setContentText("The number must be between 1 and 100.");
			alert.showAndWait();
			ANSpeed.setText(Integer.toString(stepSpeed));

		}
	}

	@FXML
	protected void actionPlaybackSetting(ActionEvent event) {
		Double _maxLength, _interval;
		try {
			_maxLength = Double.parseDouble(PBMaxLength.getText());
			_interval = Double.parseDouble(PBInterval.getText());
		} catch (Exception e) {
			_maxLength = -1.0;
			_interval = -1.0;
		}

		if (_maxLength < playbackSize) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Playback Max Length must not be smaller then previsou setting.");
			alert.showAndWait();
		} else if (_maxLength >= 1 && _maxLength <= 1000 && _interval >= 1 && _interval <= 100) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Successful!");
			alert.setHeaderText("Playback Setting Successful");
			alert.setContentText(
					"Max Length: " + _maxLength.intValue() + "\nRecording Interval: " + _interval.intValue());
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				playbackSize = _maxLength.intValue();
				trackingInterval = _interval.intValue();
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error: Playback Setting is not accepted");
			alert.setContentText("Max Length must be between 1 and 1000.\nInterval should be between 1 and 100");
			alert.showAndWait();
		}

		PBMaxLength.setText(Integer.toString(playbackSize));
		PBInterval.setText(Integer.toString(trackingInterval));

		// ANSpeed.setText("Sign in button pressed");
		// System.out.println("The button was clicked!");
	}

	@FXML
	private void addGroup() {
		// get Group and hbox from FXML

		topleft[0] = Integer.parseInt(tlX.getText());
		topleft[1] = Integer.parseInt(tlY.getText());

		bottomright[0] = Integer.parseInt(brX.getText());
		bottomright[1] = Integer.parseInt(brY.getText());

		// set Grid Size
		// sceneWidth = groupP.getPrefWidth();
		// sceneHeight = groupP.getPrefHeight();
		
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
	public void addFXConsole() {
		consoleText.setEditable(false);
		JXConsoleComponent console = new JXConsoleComponent(consoleText);
		PrintStream ps = new PrintStream(console, true);
		System.setOut(ps);
		System.setErr(ps);
	}

	@FXML
	public void resizeCA() {

		groupP.widthProperty().addListener((observable, oldValue, newValue) -> {
			sceneWidth = (double) newValue-25;
			sceneWidth = sceneWidth*n/xCellsNumber;			
			setGridSize();
		});

		groupP.heightProperty().addListener((observable, oldValue, newValue) -> {
			if ((double) newValue < 1) {
				ANSelect.setSelected(false);
			}
			sceneHeight = (double) newValue-25;
			sceneHeight = sceneHeight*m/yCellsNumber;
			setGridSize();
		});
	}

	@FXML
	protected void actionEnlarge(ActionEvent event) {
		sceneWidth = sceneWidth * 1.3;
		sceneHeight = sceneHeight * 1.3;
		setGridSize();

	}

	@FXML
	protected void actionReduce(ActionEvent event) {
		sceneWidth = sceneWidth / 1.3;
		sceneHeight = sceneHeight / 1.3;
		setGridSize();

	}


	@FXML
	protected void actionAreaSelect(ActionEvent event) {
		topleft[0] = Integer.parseInt(tlX.getText());
		topleft[1] = Integer.parseInt(tlY.getText());

		bottomright[0] = Integer.parseInt(brX.getText());
		bottomright[1] = Integer.parseInt(brY.getText());

		xCellsNumber = bottomright[0] - topleft[0] + 1;
		yCellsNumber = bottomright[1] - topleft[1] + 1;

		gridWidth = (groupP.getWidth() - 25) / xCellsNumber;
		gridHeight = (groupP.getHeight() - 25) / yCellsNumber;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				if (i >= topleft[0] && i <= bottomright[0] && j >= topleft[1] && j <= bottomright[1]) {
					cellView[i][j].setDisable(false);
					cellView[i][j].setVisible(true);
				}

				else {
					cellView[i][j].setDisable(true);
					cellView[i][j].setVisible(false);
				}

			}
		}

		sceneWidth = gridWidth * n;
		sceneHeight = gridHeight * m;
		setGridSize();

		double scrollX = topleftNodeInScrollPaneX(groupScrollView, cellView[topleft[0]][topleft[1]]);
		double scrollY = topleftNodeInScrollPaneY(groupScrollView, cellView[topleft[0]][topleft[1]]);

		final Timeline timeline = new Timeline();
		final KeyValue kvalueX = new KeyValue(groupScrollView.hvalueProperty(), scrollX);
		final KeyValue kvalueY = new KeyValue(groupScrollView.vvalueProperty(), scrollY);

		final KeyFrame kf = new KeyFrame(Duration.millis(500), kvalueX, kvalueY);
		timeline.getKeyFrames().add(kf);
		timeline.play();

		System.out.println(
				"v: " + groupScrollView.vvalueProperty().get() + ";h: " + groupScrollView.hvalueProperty().get());

	}

	public double topleftNodeInScrollPaneY(ScrollPane scrollPane, Node node) {
		double h = scrollPane.getContent().getBoundsInLocal().getHeight();
		double y = node.getBoundsInParent().getMinY();
		double v = scrollPane.getViewportBounds().getHeight();
		double m = scrollPane.getVmax();
		return (m * ((y) / (h - v)));
	}

	public double topleftNodeInScrollPaneX(ScrollPane scrollPane, Node node) {
		double w = scrollPane.getContent().getBoundsInLocal().getWidth();
		double x = node.getBoundsInParent().getMinX();
		double v = scrollPane.getViewportBounds().getWidth();
		double m = scrollPane.getHmax();
		return (m * ((x) / (w - v)));
	}

	@FXML
	public void initialize() {
		// setup the model name for simulator UI
		model_name.setText("Model Running: \"" + name + "\"");

		// setup the Animiaton Control Panel
		ANSpeed.setText(Integer.toString(stepSpeed));
		ANSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			ANSpeed.setText(Integer.toString(newValue.intValue()));
			stepSpeed = newValue.intValue();

		});

		// addHBox();

		// setup the area selection index as initial, before call the addGroup
		// function.
		tlX.setText("" + 0);
		tlY.setText("" + 0);
		brX.setText("" + (n - 1));
		brY.setText("" + (m - 1));

		// setup the root of nodes
		addGroup();

		// setup the FX console pane
		addFXConsole();

		// resize the cell node
		resizeCA();

		// set the console Tab to a default location when it is clicked.
		DisplayTabs.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				console_split.getDividers().get(0).setPosition(0.75);
			}
		});

		// THe play back Control Initialized
		PBMaxLength.setDisable(true);
		PBMaxLength.setText(Integer.toString(playbackSize));
		PBInterval.setDisable(true);
		PBInterval.setText(Integer.toString(trackingInterval));
		PBMaxLengthButton.setDisable(true);

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
				PBMaxLengthButton.setDisable(!new_val);
				PBMaxLength.setDisable(!new_val);
				PBInterval.setDisable(!new_val);
			}
		});

		ANSelect.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				animationSelected = new_val;
				ANSpeed.setDisable(!new_val);
				ANSpeedButton.setDisable(!new_val);
				ANSpeedSlider.setDisable(!new_val);
				root.setVisible(new_val);
			}
		});

	}

	@FXML
	protected void hideAndShowControl(ActionEvent event) {
		// Double propValue =
		// ca_split.getDividers().get(0).positionProperty().doubleValue();
		Ellipse ellipse = new Ellipse();
		ca_split.getDividers().get(0).positionProperty().bindBidirectional(ellipse.opacityProperty());
		// leftP.minWidthProperty().bindBidirectional(new
		// SimpleDoubleProperty((ellipse.opacityProperty().multiply(ca_split.getWidth()).doubleValue())));

		// leftP.minWidthProperty().bindBidirectional(new
		// SimpleDoubleProperty((ellipse.opacityProperty().multiply(height)).doubleValue()));
		if (HideAndShowControlButton.getText().equalsIgnoreCase("Hide Control")) {
			leftP.setMinWidth(0);
			FadeTransition dt = new FadeTransition(Duration.millis(100), ellipse);
			dt.setFromValue(0.425);
			dt.setToValue(0);
			dt.play();
			HideAndShowControlButton.setText("Show Control");

		} else {
			leftP.setMinWidth(300);
			FadeTransition dt = new FadeTransition(Duration.millis(500), ellipse);
			dt.setFromValue(0);
			dt.setToValue(0.425);
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