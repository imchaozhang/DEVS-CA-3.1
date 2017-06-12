package facade.modeling.CAmodeling;

import GenCol.Pair;
import facade.modeling.FAtomicModel;
import facade.modeling.FModel;
import facade.simulation.FSimulator;
import model.modeling.CAModels.TwoDimCell;
import view.modeling.ViewableAtomic;

public class FCACellModel extends FAtomicModel {
	private TwoDimCell model;

	public FCACellModel(TwoDimCell model, FSimulator simulator) {
		this(model, null, simulator);
	}

	public FCACellModel(TwoDimCell model, FModel parent, FSimulator simulator) {
		super(model, parent, simulator);
		 this.model = model;
	}

	public TwoDimCell getModel() {
		return model;
	}

	public Pair getID() {
		return model.getId();

	}

	public int getXcoord() {
		return model.getXcoord();
	}

	public int getYcoord() {
		return model.getYcoord();
	}

	public Pair neighborID(int i, int j) {
		return model.neighborId(i, j);
	}

}
