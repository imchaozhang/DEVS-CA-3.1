package view.CAView.Components;

import java.util.HashSet;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import view.CAView.CellView;

public class RubberBandSelection {

	final private DragContext dragContext = new DragContext();
	private SelectionModel selectionModel = new SelectionModel();

	private Rectangle rect;

	private Group group;

	public int[] topleft = { -1, -1 };
	public int[] bottomright = { -1, -1 };

	public RubberBandSelection(Group group) {

		this.group = group;

		rect = new Rectangle(0, 0, 0, 0);
		rect.setStroke(Color.BLUE);
		rect.setStrokeWidth(1);
		rect.setStrokeLineCap(StrokeLineCap.ROUND);
		rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

		group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
		group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
		group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

	}

	EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			dragContext.mouseAnchorX = event.getSceneX();
			dragContext.mouseAnchorY = event.getSceneY();

			rect.setX(dragContext.mouseAnchorX);
			rect.setY(dragContext.mouseAnchorY);
			rect.setWidth(0);
			rect.setHeight(0);

			group.getChildren().add(rect);
			for (Node node : group.getChildren()) {

				if (node instanceof CellView) {
					if (node.getBoundsInParent().intersects(rect.getBoundsInParent())) {
						topleft[0] = ((CellView) node).getI();
						topleft[1] = ((CellView) node).getJ();	
						break;
					}
				}
			}

			event.consume();
			System.out.println("TopLeft: "+topleft[0]+", "+topleft[1]);

		}
	};

	EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			if (!event.isShiftDown() && !event.isControlDown()) {
				selectionModel.clear();
			}

			for (Node node : group.getChildren()) {

				if (node instanceof CellView) {
					if (node.getBoundsInParent().intersects(rect.getBoundsInParent())) {

						if (event.isShiftDown()) {

							selectionModel.add(node);

						} else if (event.isControlDown()) {

							if (selectionModel.contains(node)) {
								selectionModel.remove(node);
							} else {
								selectionModel.add(node);
							}
						} else {
							selectionModel.add(node);
							bottomright[0] = ((CellView) node).getI();
							bottomright[1] = ((CellView) node).getJ();	
						}

					}
				}

			}

			selectionModel.log();

			rect.setX(0);
			rect.setY(0);
			rect.setWidth(0);
			rect.setHeight(0);

			group.getChildren().remove(rect);
			

			event.consume();

			System.out.println("BottomRight: "+bottomright[0]+", "+bottomright[1]);

		}
	};

	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			double offsetX = event.getSceneX() - dragContext.mouseAnchorX;
			double offsetY = event.getSceneY() - dragContext.mouseAnchorY;

			if (offsetX > 0)
				rect.setWidth(offsetX);
			else {
				rect.setX(event.getSceneX());
				rect.setWidth(dragContext.mouseAnchorX - rect.getX());
			}

			if (offsetY > 0) {
				rect.setHeight(offsetY);
			} else {
				rect.setY(event.getSceneY());
				rect.setHeight(dragContext.mouseAnchorY - rect.getY());
			}

			
			event.consume();


		}
	};

	private final class DragContext {

		public double mouseAnchorX;
		public double mouseAnchorY;

	}

	private class SelectionModel {
		Set<Node> selection = new HashSet<>();

		public void add(Node node) {
			node.setStyle("-fx-effect: dropshadow(three-pass-box, red, 2, 2, 0, 0);");
			selection.add(node);
		}

		public void remove(Node node) {
			node.setStyle("-fx-effect: null");
			selection.remove(node);
		}

		public void clear() {

			while (!selection.isEmpty()) {
				remove(selection.iterator().next());
			}

		}

		public boolean contains(Node node) {
			return selection.contains(node);
		}

		public void log() {
			// System.out.println( "Items in model: " + Arrays.asList(
			// selection.toArray()));
		}

	}
}