package view.CAView;

import java.util.LinkedList;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import view.timeView.Event;

public class CellView extends StackPane {

	public Rectangle rectangle;
	public String status = "";
	public StringProperty sstatus;
	public Text text;
	public Color color;
	public LinkedList<Event> datalistCAView = new LinkedList<Event>();

	public CellView(double x, double y, double width, double height) {

		// initialize rectangle
		rectangle = new Rectangle(width, height);
		//rectangle.setFill(Color.ANTIQUEWHITE);
		color = Color.ANTIQUEWHITE;
		
		rectangle.setFill(color);
		text = new Text("start");
		

		// set position
		setTranslateX(x);
		setTranslateY(y);

		getChildren().add(rectangle);
		getChildren().add(text);
	}

	public void addEvent(Event e)  {
		if (e.getName() == "Phase") {
			//System.out.println(e);
			try{datalistCAView.add(e);
			
			}
			catch(Exception e1){
				System.out.println("Not added");
			}
		}
	}

	public CellView step() {
		Event current = datalistCAView.poll();
		if (current.getName() == "Phase" && current.getData() == "tree") {
			status = "tree";
			color = Color.GREEN;

		} else if (current.getName() == "Phase" && current.getData() == "fire") {
			status = "fire";
			color = Color.RED;

		} else {

			status = "emp";
			color = Color.WHITE;
		}
		
		text.setText(status+":"+current.getTime());
		
		return this;

	}
	
	public boolean isDatalistEmp(){
		if(datalistCAView.isEmpty())
			return true;
		else
			return false;
		
	}

}
