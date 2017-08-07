package view.CAView.Components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

	private IntegerProperty[] topleft = { new SimpleIntegerProperty(-1), new SimpleIntegerProperty(-1) };
	private IntegerProperty[] bottomright =  { new SimpleIntegerProperty(-1), new SimpleIntegerProperty(-1) };

	private Label labelTopLeft = new Label("10000");
	private Label labelBottomRight = new Label("10000");

	public RubberBandSelection(Group group) {

		this.group = group;

		rect = new Rectangle(0, 0, 0, 0);
		rect.setStroke(Color.BLUE);
		rect.setStrokeWidth(0.8);
		rect.setStrokeLineCap(StrokeLineCap.ROUND);
		rect.setOpacity(0.8);
		rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

		disable(false);

	}

	EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			dragContext.mouseAnchorX = event.getX();
			dragContext.mouseAnchorY = event.getY();
			dragContext.groupMaxX = group.getBoundsInLocal().getMaxX();
			dragContext.groupMaxY = group.getBoundsInLocal().getMaxY();

			clearRubberBand();

			rect.setX(dragContext.mouseAnchorX);
			rect.setY(dragContext.mouseAnchorY);
			rect.setWidth(0);
			rect.setHeight(0);

			group.getChildren().add(rect);

			event.consume();

		}
	};

	EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			selectionModel.clear();

			int i = 0;
			for (Node node : group.getChildren()) {

				if (node instanceof CellView) {
					if (node.getBoundsInParent().intersects(rect.getBoundsInParent())) {
						//selectionModel.add(node);
						i++;
						if (i == 1) {
							topleft[0].setValue(((CellView) node).getI());
							topleft[1].setValue(((CellView) node).getJ());
							bottomright[0].setValue(((CellView) node).getI());
							bottomright[1].setValue(((CellView) node).getJ());
						} else {
							bottomright[0].setValue(((CellView) node).getI());
							bottomright[1] .setValue(((CellView) node).getJ());
						}
					}
				}
			}
			labelTopLeft.setText("(" + topleft[0].getValue() + ", " + topleft[1].getValue() + ")");
			labelTopLeft.setLayoutX(rect.getX());
			labelTopLeft.setLayoutY(rect.getY());
			group.getChildren().add(labelTopLeft);
			if (topleft[0].getValue() != bottomright[0].getValue() || topleft[1].getValue() != bottomright[1].getValue()) {
				labelBottomRight.setText("(" + bottomright[0].getValue() + ", " + bottomright[1].getValue() + ")");
				group.getChildren().add(labelBottomRight);
				int labelTextLength = labelBottomRight.getText().length();
				labelBottomRight.setLayoutX(Math.max(rect.getX() + rect.getWidth() - 6*labelTextLength, 0));
				labelBottomRight.setLayoutY(Math.max(rect.getY() + rect.getHeight() - 16, 0));

			}

			// selectionModel.log();

			event.consume();

		}
	};

	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			double offsetX = Math.min(event.getX(), dragContext.groupMaxX) - dragContext.mouseAnchorX;
			double offsetY = Math.min(event.getY(), dragContext.groupMaxY) - dragContext.mouseAnchorY;

			if (offsetX > 0) {
				rect.setX(dragContext.mouseAnchorX);
				rect.setWidth(offsetX);
			}

			else {
				if (event.getX() < 0) {
					rect.setX(0);
				} else {
					rect.setX(event.getX());
				}
				rect.setWidth(dragContext.mouseAnchorX - rect.getX());
			}

			if (offsetY > 0) {
				rect.setY(dragContext.mouseAnchorY);
				rect.setHeight(offsetY);
			} else {
				if (event.getY() < 0) {
					rect.setY(0);
				} else {
					rect.setY(event.getY());
				}
				rect.setHeight(dragContext.mouseAnchorY - rect.getY());
			}

			event.consume();

		}
	};

	private final class DragContext {

		public double mouseAnchorX;
		public double mouseAnchorY;
		public double groupMaxX;
		public double groupMaxY;

	}

	private class SelectionModel {
		Set<Node> selection = new HashSet<>();

		public void add(Node node) {
			selection.add(node);
		}

		public void remove(Node node) {
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
			System.out.println("Items in model: " + Arrays.asList(selection.toArray()));
		}

	}

	public IntegerProperty[] getTopleft() {
		return topleft;
	}

	public void setTopleft(IntegerProperty[] topleft) {
		this.topleft = topleft;
	}

	public IntegerProperty[] getBottomright() {
		return bottomright;
	}

	public void setBottomright(IntegerProperty[] bottomright) {
		this.bottomright = bottomright;
	}

	public void clearRubberBand() {
		selectionModel.clear();
		group.getChildren().remove(rect);
		group.getChildren().remove(labelBottomRight);
		group.getChildren().remove(labelTopLeft);
	}

	public void disable(boolean d) {
		if (d) {
			try {
				group.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
				group.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
				group.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
				clearRubberBand();
				
			} catch (Exception e) {
			}

		} else {
			try {
				group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
				group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
				group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
				
			} catch (Exception e) {
			}

		}

	}
}