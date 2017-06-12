package facade.modeling.CAmodeling;

import java.util.List;
import java.util.Vector;

import GenCol.Pair;
import facade.modeling.FAtomicModel;
import facade.modeling.FCoupledModel;
import facade.modeling.FModel;
import facade.modeling.FModelException;
import facade.simulation.FSimulator;
import model.modeling.IOBasicDevs;
import model.modeling.componentIterator;
import model.modeling.digraph;
import model.modeling.CAModels.TwoDimCell;
import model.modeling.CAModels.TwoDimCellSpace;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableDigraph;

public class FCASpaceModel extends FCoupledModel {

	private TwoDimCellSpace model;
	private Vector CAChildComponents;

	public FCASpaceModel(TwoDimCellSpace model, FSimulator simulator) {
		this(model, null, simulator);
	}

	public FCASpaceModel(TwoDimCellSpace model, FModel parent, FSimulator simulator) {
		super(model, parent, simulator);
		this.model = model;
		this.CAChildComponents = createCAChildModels(model,this,simulator);
		
	}
	
    public TwoDimCellSpace getModel(){
    	return model;
    }   
    
    public List getCAChildren()
    {
        return new Vector(CAChildComponents);
    }

	public int getNumCells() {
		return model.numCells;
	}

	public int getXDimSize() {

		return model.xDimCellspace;
	}

	public int getYDimSize() {

		return model.yDimCellspace;
	}

	public TwoDimCell getCell(int x, int y) {
		return (TwoDimCell) model.withId(x, y);
	}

	private static Vector createCAChildModels(digraph model, FModel fModel, FSimulator fSimulator) {
		Vector vector = new Vector();
		componentIterator it = model.getComponents().cIterator();
		while (it.hasNext()) {
			IOBasicDevs next = it.nextComponent();
			if (next instanceof TwoDimCell)
				vector.add(new FCACellModel((TwoDimCell) next, fModel, fSimulator));
			else if (next instanceof TwoDimCellSpace)
				vector.add(new FCASpaceModel((TwoDimCellSpace) next, fModel, fSimulator));
			else
				throw new FModelException("Unknown Model Type: " + next.getName());
		}
		return vector;
	}

}
