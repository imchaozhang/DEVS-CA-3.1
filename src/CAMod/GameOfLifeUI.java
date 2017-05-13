package CAMod;

import javafx.scene.paint.Color;
import view.CAView.CAViewUI;

public class GameOfLifeUI {
	public static void setPhaseColor(){
		CAViewUI.addPhaseColor("life", Color.GREEN);
		CAViewUI.addPhaseColor("passive", Color.RED);
		CAViewUI.addPhaseColor("die", Color.WHITE);
	}
	
}
