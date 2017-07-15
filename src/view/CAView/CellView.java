package view.CAView;

import java.util.ArrayList;
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
import view.timeView.Graph;
import view.timeView.GraphFactory;

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
	public CATracker catracker;
	
	//parameters for TimeView
	public boolean isTimeViewWindowOpen;
	public boolean trackPhase;
	public boolean trackSigma;
	public boolean trackTL;
	public boolean trackTN;
    
	public boolean isCATimeViewSelected;
	public boolean istrackinglogselected;   //to make one tracking log panel
    
	public boolean isZeroTimeSelected;
	
   
    //private boolean isBreakout = false;
    public String xUnit = "sec";
    public String timeIncr = "10";
    public String tlUnit = "";
    public String tnUnit = "";
    public String phaseUnit = "";
    public String sigmaUnit = "";
    
	
	private String tp_Sigma="", tp_State="", tp_TL="", tp_StatusChanged="";

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
		tp.setAutoHide(true);
		
		//setting for CA TimeView
        trackPhase = false;
        trackSigma = false;
        trackTL    = false;
        trackTN    = false;
        isCATimeViewSelected = false;
        istrackinglogselected = false;
        isZeroTimeSelected = false;

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
				}
					status = (String) current.getData();
					currentcolor = CAViewUI.getColor(current.getData().toString());
				

			}

			Event currentSigma = datalistCAView.poll();

			tp_State = "\nphase: " + current.getData();
			
			tp_Sigma = "\nsigma: " + currentSigma.getData();
			
			tp_TL = "\nCurrent time: "+ current.getTime();
			
			tp_StatusChanged = "\nStatus Changed: " + statusChanged;
			
			currentTime = current.getTime();
		} catch (Exception e) {
			System.out.println("No data in list!");

		}
		return this;

	}
	
	public void refreshNodeColor(){
		currentcolor = CAViewUI.getColor(status);
		rectangle.setFill(currentcolor);	
	}

	public boolean isDatalistEmp() {
		if (datalistCAView.isEmpty())
			return true;
		else
			return false;

	}
	public void setTPText(boolean _isPhase, boolean _isSigma, boolean _isStateChanged){
		
		text.setText("i: " + this.i + ", j: " + this.j + ((_isPhase)?tp_State:"" ) + ((_isSigma)?tp_Sigma:"") + ((_isStateChanged)?tp_StatusChanged:""));
		tp.setText(text.getText());		
		
	}
	
	
	public void changeWidth(double new_width){
		
		rectangle.setWidth(new_width);
		
	}
	
	public void changeHeight(double new_height){
		
		rectangle.setWidth(new_height);
		
	}
	
	public void setCATracker(CATracker _CAtracker){
		catracker = _CAtracker;		
	}
	
	public void setCATimeViewGraphs(){
		catracker.setTrackPhase(trackPhase);
		catracker.setTrackSigma(trackSigma);
		catracker.setTrackTL(trackTL);
		catracker.setTrackTN(trackTN);
		catracker.settimeIncrement(timeIncr);
		catracker.setxUnit(xUnit);
		catracker.setisBreakout(true);
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		GraphFactory graphFactory = new GraphFactory();
		if (trackTL) {
			Graph tl = graphFactory.createChart("STATEVARIABLE");
			tl.setCategory("STATEVARIABLE");
			tl.setUnit(tlUnit);
			tl.setName("tl");
			tl.setZeroTimeAdvance(isZeroTimeSelected);
			graphs.add(tl);
		}
		if (trackTN) {
			Graph tn = graphFactory.createChart("STATEVARIABLE");
			tn.setName("tN");
			tn.setUnit(tnUnit);
			tn.setCategory("STATEVARIABLE");
			tn.setZeroTimeAdvance(isZeroTimeSelected);
			graphs.add(tn);

		}
		if (trackPhase) {
			Graph phase = graphFactory.createChart("STATE");
			phase.setName("Phase");
			phase.setUnit(phaseUnit);
			phase.setCategory("STATE");
			phase.setZeroTimeAdvance(isZeroTimeSelected);
			graphs.add(phase);
		}
		if (trackSigma) {
			Graph sigma = graphFactory.createChart("SIGMA");
			sigma.setName("Sigma");
			sigma.setUnit(sigmaUnit);
			sigma.setCategory("SIGMA");
			sigma.setZeroTimeAdvance(isZeroTimeSelected);
			graphs.add(sigma);
		}
		
		this.catracker.setGraphs(graphs);	
		
		
		catracker.getCATrackingControl().registerCATimeView(catracker.getGraphs(),
				catracker.getModelNum(), catracker.getxUnit(), catracker.gettimeIncrement(), isTimeViewWindowOpen);
		
	}
	
	public int getI(){
		return i;
		
	}
	
	public int getJ(){
		return j;
		
	}
	

}
