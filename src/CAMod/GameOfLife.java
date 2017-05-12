package CAMod;

import java.awt.Dimension;
import java.awt.Point;

import SimpArcMod.genr;
import javafx.application.Application;
import javafx.stage.Stage;
import model.CAmodeling.TwoDimCellSpace;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableComponent;
import view.modeling.ViewableComponentUtil;

public class GameOfLife extends TwoDimCellSpace {

	// A b-heptomino looks like this:
	// X
	// XXX
	// X XX
	public static final int[][] b_heptomino = new int[][] { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 1, 1 }, { 0, 0, 1 } };

	public GameOfLife() {
		this(20, 20);
	}

	public GameOfLife(int xDim, int yDim) {
		super("game", xDim, yDim);

//		clock g = new clock("g", 1);
//		add(g);
		this.numCells = xDim * yDim;
		for (int i = 0; i < xDimCellspace; i++) {
			for (int j = 0; j < yDimCellspace; j++) {
				Life life = new Life(i, j);
				addCell(life);
//				addCoupling(g, "out", life, "clock");
			}
		}
		
		for(int x=0;x<b_heptomino.length;x++)
            for(int y=0;y<b_heptomino[x].length;y++){
            	Life l = (Life)(getCell(x+xDim/2-b_heptomino.length/2, y+yDim/2-b_heptomino[x].length/2));
            			l.isLife = b_heptomino[x][y];
            			l.initialize();
            }
        
		

		// Do the couplings

		doNeighborToNeighborCoupling();

	}

	/**
	 * Automatically generated by the SimView program. Do not edit this
	 * manually, as such changes will get overwritten.
	 */
	public void layoutForSimView() {
		preferredSize = new Dimension(800, 700);
		((ViewableComponent) withName("Cell_key = 2 ,value = 2")).setPreferredLocation(new Point(53, 266));
		((ViewableComponent) withName("Cell_key = 0 ,value = 0")).setPreferredLocation(new Point(270, 281));
		((ViewableComponent) withName("Cell_key = 2 ,value = 0")).setPreferredLocation(new Point(395, 70));
		((ViewableComponent) withName("Cell_key = 0 ,value = 1")).setPreferredLocation(new Point(50, 50));
		((ViewableComponent) withName("Cell_key = 1 ,value = 0")).setPreferredLocation(new Point(50, 50));
		((ViewableComponent) withName("Cell_key = 0 ,value = 2")).setPreferredLocation(new Point(50, 50));
		((ViewableComponent) withName("Cell_key = 1 ,value = 2")).setPreferredLocation(new Point(50, 50));
		((ViewableComponent) withName("Cell_key = 1 ,value = 1")).setPreferredLocation(new Point(50, 50));
		((ViewableComponent) withName("Cell_key = 2 ,value = 1")).setPreferredLocation(new Point(50, 50));
	}
}