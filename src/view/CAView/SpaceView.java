package view.CAView;

import java.lang.reflect.Array;
import java.util.LinkedList;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpaceView extends Application {

	private double sceneWidth = 1200;
	private double sceneHeight = 800;
	public static CellView[][] cellView;
	double gridWidth, gridHeight;
	static int n, m;
	public static LinkedList<ParallelTransition> lookback = new LinkedList<ParallelTransition>();
	//public static Slider slider;
	

	public static void initial(int i, int j) {

		// super();
		n = i;
		m = j;

	}

	@Override
	public void init() {
		gridWidth = sceneWidth / n;
		gridHeight = sceneHeight / m;

		cellView = new CellView[n][m];

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane border = new BorderPane();
		
		HBox hbox = addHBox();
		VBox vbox = addVBox();
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
						node.tp.show(mynode, primaryStage.getX() + t.getSceneX(), primaryStage.getY() + t.getSceneY());
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
		System.out.println("w: " + vbox.getPrefWidth() + ";h: " + hbox.getPrefHeight());

		primaryStage.setScene(scene);
		primaryStage.show();

		// animate();

	}

	public void animate() {

		ParallelTransition parallelTransition = new ParallelTransition();
		
		ParallelTransition lookbackTransition = new ParallelTransition();

		for (int ai = 0; ai < n; ai++) {
			for (int aj = 0; aj < m; aj++) {
				CellView currentnode = cellView[ai][aj];
				if (!currentnode.isDatalistEmp()) {
					FillTransition ftA = new FillTransition(Duration.millis(500), currentnode.rectangle,
							currentnode.color, currentnode.step().color);
					ftA.setAutoReverse(false);
					if (currentnode.statusChanged) 
					{
						parallelTransition.getChildren().add(ftA);
					}
					
					lookbackTransition.getChildren().add(ftA);

				}

			}
		}

		if (lookback.size() <= 20) {
			lookback.add(lookbackTransition);
		} else {
			lookback.poll();
			lookback.add(lookbackTransition);
		}

		parallelTransition.play();

		parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// animate();
				

			}

		});

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

		Slider slider = new Slider(0, 20, 0);
		slider.setLayoutX(10);
		slider.setLayoutY(95);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(5);
		slider.setBlockIncrement(1);

		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				lookback.get((int) slider.getValue()).play();
				if(slider.getMax()<20){
					//slider.setMax(lookback.size());
				}

			}
		});

		// slider.valueProperty().addListener((ov, curVal, newVal) ->
		// lookback.get((int)slider.getValue()).play());

		vbox.setMargin(slider, new Insets(10));
		vbox.getChildren().add(slider);

		return vbox;
	}

}
