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
	public Color previouscolor,currentcolor;
	public LinkedList<Event> datalistCAView = new LinkedList<Event>();
	public Tooltip tp = new Tooltip();
	public boolean statusChanged = false;
	public double currentTime = 0;

	private int i, j;

	public CellView(int x, int y, double width, double height) {

		// initialize rectangle
		rectangle = new Rectangle(width, height);
		// rectangle.setFill(Color.ANTIQUEWHITE);
		currentcolor = previouscolor = Color.WHITE;

		rectangle.setFill(currentcolor);
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
		try {
			Event current = datalistCAView.poll();
			if (current.getName() == "Phase") {
				if (status != current.getData()) {
					statusChanged = true;
					status = (String) current.getData();
					currentcolor = CAViewUI.getColor(current.getData().toString());
				}

			}

			Event currentSigma = datalistCAView.poll();

			text.setText("i: " + this.i + ", j: " + this.j + "\nphase: " + current.getData() + "\ntime: "
					+ current.getTime() + "\nsigma: " + currentSigma.getData() + "\nStatus Changed: " + statusChanged);
			tp.setText(text.getText());
			
			currentTime = current.getTime();
		} catch (Exception e) {
			System.out.println("No data in list!");

		}
		return this;

	}

	public boolean isDatalistEmp() {
		if (datalistCAView.isEmpty())
			return true;
		else
			return false;

	}

}
