package view.CAView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import facade.CAmodeling.FCACellModel;
import facade.CAmodeling.FCASpaceModel;
import facade.modeling.FAtomicModel;
import facade.modeling.FModel;
import view.Tracker;
import view.timeView.Event;

public class CATracker extends Tracker {

	private int x = -1, y = -1, xsize = -1, ysize = -1;
	private List CAViewData;
	private boolean isCACell;
	private boolean isCASpace;
	private double time;
	private Event e;

	private FModel CAModel;

	public CATracker(FModel model, int num) {
		super(model, num);
		// TODO Auto-generated constructor stub
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
			e = new Event("Phase", "STATE", time, CACell.getPhase());
			CAViewData.add(e);
			e = new Event("Sigma","SIGMA",time, CACell.getSigma()); 
			CAViewData.add(e);
			// e = new Event("X","COORD",time, CACell.getXcoord());
			// CAViewData.add(e);
			// e = new Event("Y","COORD",time, CACell.getYcoord());
			// CAViewData.add(e);
		}

		return CAViewData;
	}

}
