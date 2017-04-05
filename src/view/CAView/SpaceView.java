package view.CAView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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

public class SpaceView {

	private static double sceneWidth = 1000;
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
	private static boolean isState = true, isTL = true, isSigma = true, isStatusChanged = true;

	private static int playbackSize = 50;

	private static Scene scene;
	private static Parent border;
	private static VBox vbox;

	private double currentTime = 0;

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	@FXML
	private CheckBox PSelect, cb_State, cb_Sigma, cb_TL, cb_StatusChanged;
	@FXML
	private Button PBMaxLengthButton, ANSpeedButton;
	@FXML
	private TextField PBMaxLength, PBFrom, PBTo, PBInterval, ANSpeed;
	@FXML
	private Slider PBTracking, ANSpeedSlider;
	@FXML
	private Text PBStatus, CellChangedNumber;
	@FXML
	private HBox hbox;

	public static void initial(int i, int j) {

		// super();
		n = i;
		m = j;
		gridWidth = sceneWidth / n;
		gridHeight = sceneHeight / m;

		cellView = new CellView[n][m];

	}

	public Scene createScene() throws Exception {

		// define the border from FXMLLoader
		border = FXMLLoader.load(getClass().getResource("CA.fxml"));

		// get Group and hbox from FXML
		Group root = (Group) ((BorderPane) border).getCenter();

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
						node.setTPText(isState, isTL, isSigma, isStatusChanged);
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

		scene = new Scene(border);
		// System.out.println("w: " + vbox.getPrefWidth() + ";h: " +
		// hbox.getPrefHeight());

		return scene;

		// animate();

	}

	public void closeScene() {
		((BorderPane) border).getCenter().setVisible(false);

	}

	public void openScene() {
		((BorderPane) border).getCenter().setVisible(true);

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
						// PBTracking.setMax(playback.size() - 1);
						// PBTracking.setValue(playback.size() - 1);

					} else {
						playback.poll();
						playback.add(playbackTransition);
						// PBTracking.setValue(playback.size() - 1);

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		Iterator it = PhaseColors.entrySet().iterator();
		// set an index id for phase and color
		int c = 0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			Label title = new Label("" + pair.getKey());
			title.setId("" + c);
			ColorPicker color4phase = new ColorPicker((Color) pair.getValue());
			color4phase.setId("" + c);
			color4phase.setOnAction(new EventHandler() {
				@Override
				public void handle(Event event) {
					CAViewUI.changeColor(pair.getKey().toString(), color4phase.getValue()) ;					
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
	public void initialize() {

		addHBox();

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
				PBTracking.setDisable(!new_val);
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

		PBTracking.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if (PBTracking.getMax() > 0 && !sizechanged) {
					// if (executor.isTerminated()) {
					playback.get((int) PBTracking.getValue()).play();
					playbacked = true;
					PBStatus.setText(
							String.format("%.0f", (currentTime - playback.size() + 1 + (double) new_val.longValue())));
					if (PBTracking.getMax() < 20) {
						// playbackControl.setMax(playback.size());
						// playbackControl.autosize();
					}

					// }
				}
			}
		});

	}

}
