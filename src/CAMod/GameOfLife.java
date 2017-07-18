package CAMod;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import SimpArcMod.genr;
import javafx.application.Application;
import javafx.stage.Stage;
import model.modeling.CAModels.TwoDimCellSpace;
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
		this(20,20);
	}

	public GameOfLife(int xDim, int yDim) {
		super("Game of Life", xDim, yDim);

//		clock g = new clock("g", 1);
//		add(g);
		this.numCells = xDim * yDim;
		for (int i = 0; i < xDimCellspace; i++) {
			for (int j = 0; j < yDimCellspace; j++) {
				Life life = new Life(i, j);
				addCell(life);
				
				Random randomno = new Random();
				life.isLife = randomno.nextInt(2);
				life.initialize();
//				addCoupling(g, "out", life, "clock");
			}
		}
		
//		for(int x=0;x<b_heptomino.length;x++)
//            for(int y=0;y<b_heptomino[x].length;y++){
//            	Life l = (Life)(getCell(x+xDim/2-b_heptomino.length/2, y+yDim/2-b_heptomino[x].length/2));
//            			l.isLife = b_heptomino[x][y];
//            			l.initialize();
//            }
        
		

		// Do the couplings

		doNeighborToNeighborCoupling();

	}

    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(800, 700);
        ((ViewableComponent)withName("Cell_key = 0 ,value = 2")).setPreferredLocation(new Point(6, 249));
        ((ViewableComponent)withName("Cell_key = 2 ,value = 3")).setPreferredLocation(new Point(204, 285));
        ((ViewableComponent)withName("Cell_key = 1 ,value = 0")).setPreferredLocation(new Point(381, 52));
        ((ViewableComponent)withName("Cell_key = 2 ,value = 4")).setPreferredLocation(new Point(370, 214));
        ((ViewableComponent)withName("Cell_key = 2 ,value = 1")).setPreferredLocation(new Point(-99, 148));
        ((ViewableComponent)withName("Cell_key = 2 ,value = 2")).setPreferredLocation(new Point(302, -35));
        ((ViewableComponent)withName("Cell_key = 4 ,value = 0")).setPreferredLocation(new Point(-62, -37));
        ((ViewableComponent)withName("Cell_key = 1 ,value = 2")).setPreferredLocation(new Point(212, 145));
        ((ViewableComponent)withName("Cell_key = 1 ,value = 4")).setPreferredLocation(new Point(101, -29));
        ((ViewableComponent)withName("Cell_key = 3 ,value = 2")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 3 ,value = 3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 4 ,value = 4")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 4 ,value = 2")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 0 ,value = 0")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 2 ,value = 0")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 0 ,value = 3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 3 ,value = 0")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 4 ,value = 3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 0 ,value = 1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 1 ,value = 1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 0 ,value = 4")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 1 ,value = 3")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 3 ,value = 1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 4 ,value = 1")).setPreferredLocation(new Point(50, 50));
        ((ViewableComponent)withName("Cell_key = 3 ,value = 4")).setPreferredLocation(new Point(50, 50));
    }
}
