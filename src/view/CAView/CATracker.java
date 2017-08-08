package view.CAView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import facade.modeling.FAtomicModel;
import facade.modeling.FModel;
import facade.modeling.CAmodeling.FCACellModel;
import facade.modeling.CAmodeling.FCASpaceModel;
import view.Tracker;
import view.TrackingControl;
import view.timeView.Event;

public class CATracker extends Tracker {

	private int x = -1, y = -1, xsize = -1, ysize = -1;
	private List CAViewData;
	private boolean isCACell;
	private boolean isCASpace;
	private double time;
	
    private CATrackingControl caTrackingControl;  
	
	private Event e;

	private FModel CAModel;

	public CATracker(FModel model, int num) {
		super(model, num);
	
		caTrackingControl = new CATrackingControl();
		
		CAModel = model;
		isCACell = model instanceof FCACellModel;
		isCASpace = model instanceof FCASpaceModel;

		if (isCACell) {
			x = ((FCACellModel) model).getXcoord();
			y = ((FCACellModel) model).getYcoord();
		} else if (isCASpace) {
			xsize = ((FCASpaceModel) model).getXDimSize();
			ysize = ((FCASpaceModel) model).getYDimSize();
		}

	}

	public int getX() {
		if (isCACell) {
			return ((FCACellModel) CAModel).getXcoord();
		} else
			return x;

	}

	public int getY() {
		if (isCACell) {
			return ((FCACellModel) CAModel).getYcoord();
		} else
			return y;

	}

	public int getXSize() {
		if (isCASpace) {
			return ((FCASpaceModel) CAModel).getXDimSize();
		} else
			return xsize;

	}

	public int getYSize() {
		if (isCASpace) {
			return ((FCASpaceModel) CAModel).getYDimSize();
		} else
			return ysize;

	}

	public List getCurrentCAViewData(double currentTime) {
		CAViewData = new ArrayList(1);
		time = currentTime;
		if (isCACell) {
			FCACellModel CACell = (FCACellModel) CAModel;
//			e = new Event("Phase", "STATE", time, CACell.getPhase());
//			CAViewData.add(e);
//			e = new Event("Sigma", "SIGMA", time, String.valueOf(CACell.getSigma()));
//			CAViewData.add(e);
//			e = new Event("tL", "STATEVARIABLE", time, String.valueOf(CACell.getTimeOfLastEvent()));
//			CAViewData.add(e);
//			e = new Event("tN", "STATEVARIABLE", time, String.valueOf(CACell.getTimeOfNextEvent()));
//			CAViewData.add(e);
			e = new Event("Phase", "STATE", time, CACell.getModel().getFormattedPhase());
			CAViewData.add(e);
			e = new Event("Sigma", "SIGMA", time, CACell.getModel().getFormattedSigma());
			CAViewData.add(e);
			e = new Event("tL", "STATEVARIABLE", time, String.valueOf(CACell.getTimeOfLastEvent()));
			CAViewData.add(e);
			e = new Event("tN", "STATEVARIABLE", time, String.valueOf(CACell.getTimeOfNextEvent()));
			CAViewData.add(e);
			
			
			

		}

		return CAViewData;
	}
	
    public CATrackingControl getCATrackingControl()
    {
    	return caTrackingControl;
    }
	

}
