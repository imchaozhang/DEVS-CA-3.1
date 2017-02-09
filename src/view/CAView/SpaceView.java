package view.CAView;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpaceView extends Application {

	private double sceneWidth = 1580;
	private double sceneHeight = 960;
	public static CellView[][] cellView;
	double gridWidth, gridHeight;
	static int n, m;

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

		Group root = new Group();

		// initialize playfield
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				// create node
				CellView node = new CellView(i * gridWidth, j * gridHeight, gridWidth, gridHeight);

				// add node to group
				root.getChildren().add(node);

				// add to playfield for further reference using an array
				cellView[i][j] = node;

			}
		}

		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		primaryStage.setScene(scene);
		primaryStage.show();

		// animate();

	}

	public void animate() {

		ParallelTransition parallelTransition = new ParallelTransition();

		for (int ai = 0; ai < n; ai++) {
			for (int aj = 0; aj < m; aj++) {
				CellView currentnode = cellView[ai][aj];
				if (!currentnode.isDatalistEmp()) {
					FillTransition ftA = new FillTransition(Duration.millis(500), currentnode.rectangle,
							currentnode.color, currentnode.step().color);
					ftA.setAutoReverse(false);
					parallelTransition.getChildren().add(ftA);
				}

			}
		}

		parallelTransition.play();

		parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// animate();
				
			}

		});

	}

}
