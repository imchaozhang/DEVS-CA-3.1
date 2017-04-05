package view.CAView;

import java.util.HashMap;

import javafx.scene.paint.Color;

public class CAViewUI {
	private static HashMap<String, Color> PhaseColors = new HashMap<String, Color>();

	public static void addPhaseColor(String phase, Color color) {
		PhaseColors.put(phase, color);
	}

	public static Color getColor(String phase) {
		return (Color) PhaseColors.get(phase);
	}

	public static boolean changeColor(String phase, Color color) {
		try {
			PhaseColors.replace(phase, color);
			return true;
		} catch (Exception e) {
			return false;

		}

	}
	
	public static HashMap<String, Color> getAllPhaseColor(){
		return PhaseColors;
	}

}
