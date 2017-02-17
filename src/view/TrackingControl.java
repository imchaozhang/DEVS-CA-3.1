/* 
 * 
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
/**
 * Tracking Control class: extracted from ModelTrackingComponent to integrate the TimeView
 * @author Sungung Kim
 */

package view;

import facade.CAmodeling.FCACellModel;
import facade.CAmodeling.FCASpaceModel;
import facade.modeling.*;
import facade.simulation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import controller.*;
import view.timeView.*;
import view.timeView.Event;

import view.CAView.*;

public class TrackingControl {

	private boolean isTrackingLogSelected = false;
	private int cntModel = 0;
	private int TrackingLogIndex = 1;
	protected static TimeView[] timeView;
	static int tc;
	// added

	protected ArrayList graphList = null;
	private List allModels;
	private List<Event> dataTimeView;
	private static Tracker[] modelColumn;
	private static ModelTrackingComponent modelTracking;
	private static String rootModelName;

	protected static ArrayList<ExternalTimeView> windowHandles = new ArrayList<ExternalTimeView>(0);

	private static CATracker[] CAmodelColumn;
	private static SpaceView caView;
	private List<Event> dataCAView;

	private int XSize, YSize;
	public void controlTimeView(String control) {

		if (control == "Reset") {
			isTrackingLogSelected = false;
		} else {
			for (int i = 0; i < modelColumn.length; i++) {
				// In the case of reset, close the TimeView and TrackingLog
				if (modelColumn[i].isTimeViewSelected())
					timeView[i].clock.start();
			}
		}
	}

	public void addTracking(double currTime) {

		for (int i = 0; i < modelColumn.length; i++) {

			if (!isTrackingLogSelected)
				isTrackingLogSelected = modelColumn[i].isTrackingSelected();

			if (modelColumn[i].isTimeViewSelected()) {
				dataTimeView = modelColumn[i].getCurrentTimeViewData(currTime);
				for (int j = 0; j < dataTimeView.size(); j++) {
					tc++;
					// System.out.println("addingevents^^^^^^^^^^"+(dataTimeView.get(j)));
					timeView[i].addEvent(dataTimeView.get(j));
				}
				timeView[i].endTime(currTime);
			}

		}
		if (isTrackingLogSelected)
			modelTracking.addTrackingSet(currTime);

	}

	public void loadSimModel(FModel rootModel) {
		cntModel = 0;
		rootModelName = rootModel.getName();
		if (rootModel instanceof FAtomicModel) {
			allModels = new ArrayList(1);
			allModels.add(new Tracker(rootModel, 0));
			cntModel++;
		} else
			allModels = getAllModels((FCoupledModel) rootModel);

		modelColumn = (Tracker[]) allModels.toArray(new Tracker[0]);

		// Initialize
		timeView = new TimeView[cntModel];
		// chartView = new ChartView[cntModel];
		dataTimeView = new ArrayList(1);
	}

	private List getAllModels(FCoupledModel model) {
		List list = new ArrayList();
		list.add(new Tracker(model, cntModel));
		cntModel++;
		Iterator children = model.getChildren().iterator();
		while (children.hasNext()) {
			FModel next = (FModel) children.next();
			if (next instanceof FAtomicModel) {
				list.add(new Tracker(next, cntModel));
				cntModel++;
			} else if (next instanceof FCoupledModel)
				list.addAll(getAllModels((FCoupledModel) next));
			// else error
		}
		return list;
	}

	// adding for CATracker, by Chao
	public void loadCAModel(FModel rootModel) {
		cntModel = 0;
		rootModelName = rootModel.getName();
		allModels = getAllCAModels((FCASpaceModel) rootModel);

		CAmodelColumn = (CATracker[]) allModels.toArray(new CATracker[0]);

		// Initialize
		XSize = CAmodelColumn[0].getXSize();
		YSize = CAmodelColumn[0].getYSize();

		if (XSize != -1) {
			SpaceView.initial(XSize, YSize);
			caView = new SpaceView();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					initAndRunFX();

				}
			});

		}

		dataCAView = new ArrayList(1);
	}

	private static void initAndRunFX() {

		new Thread() {
			@Override
			public void run() {
				javafx.application.Application.launch(SpaceView.class);
			}
		}.start();

	}


	private List getAllCAModels(FCASpaceModel rootModel) {
		List list = new ArrayList();
		list.add(new CATracker(rootModel, cntModel));
		cntModel++;
		Iterator children = ((FCASpaceModel) rootModel).getCAChildren().iterator();
		while (children.hasNext()) {
			FModel next = (FModel) children.next();
			if (next instanceof FAtomicModel) {
				list.add(new CATracker(next, cntModel));
				cntModel++;
			} else if (next instanceof FCASpaceModel)
				list.addAll(getAllCAModels((FCASpaceModel) next));
			// else error
		}
		return list;
	}

	public void addCATracking(double currTime) {
		for (int i = 0; i < CAmodelColumn.length; i++) {
			dataCAView = CAmodelColumn[i].getCurrentCAViewData(currTime);
			int x = CAmodelColumn[i].getX();
			int y = CAmodelColumn[i].getY();
//			System.out.println("x=" + x + ";y=" + y);
//			System.out.println(CAmodelColumn.length);
			if (x != -1 && y != -1) {
				for (int j = 0; j < dataCAView.size(); j++) {
					tc++;
					//System.out.println("X=" + x + ", Y=" + y + ": " + dataCAView.get(j));
					caView.cellView[x][y].addEvent(dataCAView.get(j));

				}

			}
		}
		caView.animate();

	}

	public void trackingLogOption(Component owner, String option) {
		if (option.equalsIgnoreCase("Tracking Log Settings...")) {
			for (int i = 0; i < modelColumn.length; i++)
				if (!isTrackingLogSelected)
					isTrackingLogSelected = modelColumn[i].isTrackingSelected();

			if (isTrackingLogSelected)
				modelTracking.customizeComponent(owner);
			else
				JOptionPane.showMessageDialog(null, "You need to select the tracking log first!", "Warning",
						JOptionPane.WARNING_MESSAGE);
		}

		else if (option.equalsIgnoreCase("Refresh Tracking Log"))
			modelTracking.refresh();
	}

	public Tracker findTrackerFor(FModel model) {
		Tracker t = null;
		boolean found = false;
		for (int i = 0; i < modelColumn.length && !found; i++) {
			if (modelColumn[i].getAttachedModel() == model) {
				t = modelColumn[i];
				found = true;
			}
		}
		return t;
	}

	public String getHTML() {
		return modelTracking.getHTMLString();
	}

	public String[] getEncodedCSV(FModel model) {
		return modelTracking.getEncodedCSVString(model);
	}

	public String getCSV(FModel model) {
		return modelTracking.getCSVString(model);
	}

	public void registerTrackingLog() {
		modelTracking = new ModelTrackingComponent();
		modelTracking.loadModel(rootModelName, modelColumn);

		// If there exist the same tab, remove it
		if (View.tabbedPane.getTabCount() != 1 && // if there is another tab
													// except console
				View.tabbedPane.getTitleAt(TrackingLogIndex).equalsIgnoreCase("Tracking Log"))
			View.tabbedPane.remove(TrackingLogIndex);
		// Add a tab
		View.tabbedPane.add(modelTracking.retTL(), TrackingLogIndex);
		View.tabbedPane.setTitleAt(TrackingLogIndex, "Tracking Log");
	}

	public void registerTimeView(ArrayList graphs, final int num, String XLabel, String TimeIncre, boolean isBreakout) {
		timeView[num] = new TimeView(graphs, modelColumn[num].getAttachedModel().getName(), XLabel, TimeIncre);

		// If there exist the same tab, remove it
		for (int i = 0; i < View.tabbedPane.getTabCount(); i++) {
			if (modelColumn[num].getAttachedModel().getName().equalsIgnoreCase(View.tabbedPane.getTitleAt(i))) {
				View.tabbedPane.remove(i);
			}
		}

		if (!isBreakout) // BREAK OUT CONTROLS
		{
			// Add a tab
			View.tabbedPane.add(modelColumn[num].getAttachedModel().getName(), timeView[num].retTG());
		} else {
			// make a separate window
			ExternalTimeView ETV = new ExternalTimeView(modelColumn[num].getAttachedModel().getName(),
					timeView[num].retTG());
			windowHandles.add(ETV);
			javax.swing.SwingUtilities.invokeLater(ETV);
		}
		graphList = graphs;
	}

	public void clearWindows() {
		while (windowHandles.size() > 0)
			windowHandles.remove(0).dispose();
	}

}
