package facade.simulation.CAsimulation;

import facade.modeling.FCoupledModel;
import facade.modeling.FModel;
import facade.modeling.CAmodeling.FCASpaceModel;
import facade.simulation.FCoupledSimulator;
import model.modeling.CAModels.TwoDimCellSpace;
import model.simulation.realTime.TunableCoordinator.Listener;
import view.modeling.ViewableDigraph;

public class FCASimulator extends FCoupledSimulator {
	private FModel rootModel;

	public FCASimulator(TwoDimCellSpace model, Listener listener, short modelType) {
		super(model, listener, modelType);
		rootModel = new FCASpaceModel(model,this);
		
		// TODO Auto-generated constructor stub
	}

	public FModel getRootModel()                {return rootModel;}
	
}
