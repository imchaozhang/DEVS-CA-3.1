package view.CAView;

import java.util.HashMap;

import javafx.scene.paint.Color;

public class CAViewUI {
	private static HashMap<String, Color> PhaseColor = new HashMap<String, Color>();

	public static void addPhaseColor(String phase, Color color) {
		PhaseColor.put(phase, color);
	}

	public static Color getColor(String phase) {
		return (Color) PhaseColor.get(phase);
	}

	public static boolean changeColor(String phase, Color color) {
		try {
			PhaseColor.replace(phase, color);
			return true;
		} catch (Exception e) {
			return false;

		}

	}

}
