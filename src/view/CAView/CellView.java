package view.CAView;

import java.util.LinkedList;

import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import view.timeView.Event;

public class CellView extends StackPane {

	public Rectangle rectangle;
	public String status = "passive";
	public StringProperty sstatus;
	public Text text;
	public Color color;
	public LinkedList<Event> datalistCAView = new LinkedList<Event>();
	public Tooltip tp = new Tooltip();
	public boolean statusChanged = false;

	private int i, j;

	public CellView(int x, int y, double width, double height) {

		// initialize rectangle
		rectangle = new Rectangle(width, height);
		// rectangle.setFill(Color.ANTIQUEWHITE);
		color = Color.WHITE;

		rectangle.setFill(color);
		text = new Text("start");
		text.setFont(Font.font("Verdana", 8));

		// set position
		i = x;
		j = y;
		setTranslateX(x * width);
		setTranslateY(y * height);

		getChildren().add(rectangle);
		// getChildren().add(text);

		tp.setText(text.getText());

	}

	public void addEvent(Event e) {
		if (e.getName() == "Phase") {
			// System.out.println(e);
			try {
				datalistCAView.add(e);

			} catch (Exception e1) {
				System.out.println("Not added");
			}
		}

		else if (e.getName() == "Sigma") {
			// System.out.println(e);
			try {
				datalistCAView.add(e);

			} catch (Exception e1) {
				System.out.println("Not added");
			}
		}
	}

	public CellView step() {
		statusChanged = false;
		Event current = datalistCAView.poll();
		if (current.getName() == "Phase") {
			if (status != current.getData())
				statusChanged = true;

		}

		if (current.getName() == "Phase" && current.getData() == "tree") {
			status = "tree";
			color = Color.GREEN;

		} else if (current.getName() == "Phase" && current.getData() == "fire") {
			status = "fire";
			color = Color.RED;

		} else {

			status = "passive";
			color = Color.WHITE;
		}

		Event currentSigma = datalistCAView.poll();

		text.setText("i: " + this.i + ", j: " + this.j + "\nphase: " + current.getData() + "\ntime: "
				+ current.getTime() + "\nsigma: " + currentSigma.getData() + "\nStatus Changed: " + statusChanged);
		tp.setText(text.getText());

		return this;

	}

	public boolean isDatalistEmp() {
		if (datalistCAView.isEmpty())
			return true;
		else
			return false;

	}

}
