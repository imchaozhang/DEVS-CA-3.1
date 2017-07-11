package view.CAView;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import controller.ControllerInterface;
import facade.modeling.FAtomicModel;
import facade.modeling.FModel;
import facade.modeling.CAmodeling.FCASpaceModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import view.ExternalTimeView;
import view.timeView.Event;
import view.timeView.TimeView;

public class CATrackingControl {
	private int cntModel = 0;
	private static String rootModelName;
	private List allModels;

	private ControllerInterface controller;

	private static CATracker[] CAmodelColumn;
	private static SpaceView caView;
	private List<Event> dataCAView;

	private List<Event> dataTimeView;
	protected static TimeView[] timeView;

	protected static ArrayList<ExternalTimeView> windowHandles = new ArrayList<ExternalTimeView>(0);

	private static JDialog dialog;

	private int XSize, YSize;

	// adding for CATracker, by Chao
	public void loadCAModel(FModel rootModel, ControllerInterface controller) {
		this.controller = controller;

		cntModel = 0;
		rootModelName = rootModel.getName();
		allModels = getAllCAModels((FCASpaceModel) rootModel);

		CAmodelColumn = (CATracker[]) allModels.toArray(new CATracker[0]);

		// Initialize
		XSize = CAmodelColumn[0].getXSize();
		YSize = CAmodelColumn[0].getYSize();

		if (XSize != -1) {
			SpaceView.initial(rootModelName, XSize, YSize, controller);
			caView = new SpaceView();
			// start a JavaFX Panel
			initAndRunFX();
		}

		for (int i = 0; i < CAmodelColumn.length; i++) {
			int x = CAmodelColumn[i].getX();
			int y = CAmodelColumn[i].getY();
			CATracker catrack = CAmodelColumn[i];

			// System.out.println("x=" + x + ";y=" + y);
			// System.out.println(CAmodelColumn.length);
			if (x != -1 && y != -1) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						SpaceView.cellView[x][y].setCATracker(catrack);
					}
				});
			}
		}

		timeView = new TimeView[cntModel];
		dataCAView = new ArrayList<Event>(1);
		dataTimeView = new ArrayList(1);
	}

	private static void initAndRunFX() {

		// new Thread() {
		// @Override
		// public void run() {
		// javafx.application.Application.launch(SpaceView.class);
		//
		// }
		// }.start();
		try {
			dialog.setVisible(false);
		} catch (Exception e) {

		}
		dialog = new JDialog();
		final JFXPanel contentPane = new JFXPanel();
		dialog.setContentPane(contentPane);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setTitle("DEVS-CA Simulation");

		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		// building the scene graph must be done on the javafx thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				try {
					contentPane.setScene(caView.createScene());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						dialog.pack();
						dialog.setVisible(true);
					}
				});
			}
		});

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
			dataTimeView = CAmodelColumn[i].getCurrentTimeViewData(currTime);
			int x = CAmodelColumn[i].getX();
			int y = CAmodelColumn[i].getY();

			// System.out.println("x=" + x + ";y=" + y);
			// System.out.println(CAmodelColumn.length);
			if (x != -1 && y != -1) {
				for (int j = 0; j < dataCAView.size(); j++) {
					// System.out.println("X=" + x + ", Y=" + y + ": " +
					// dataCAView.get(j));
					caView.cellView[x][y].addEvent(dataCAView.get(j));
				}

				if (timeView[i] != null) {
					for (int j = 0; j < dataTimeView.size(); j++) {
						// System.out.println("addingevents^^^^^^^^^^"+(dataTimeView.get(j)));
						timeView[i].addEvent(dataTimeView.get(j));
					}
					timeView[i].endTime(currTime);
				}

			}
		}
		caView.animate();

	}

	public SpaceView getCAView() {
		return caView;
	}

	// adding by Chao. For the CA Timeview Tracking
	public void registerCATimeView(ArrayList graphs, final int num, String XLabel, String TimeIncre,
			boolean isTimeViewWindowOpen) {
		timeView[num] = new TimeView(graphs, CAmodelColumn[num].getAttachedModel().getName(), XLabel, TimeIncre);
		if (isTimeViewWindowOpen) {
			for (ExternalTimeView etv : windowHandles) {
				if (etv.getName() == CAmodelColumn[num].getAttachedModel().getName()) {
					etv.setInvisible();
					// windowHandles.remove(etv);
				}
			}
		}

		ExternalTimeView ETV = new ExternalTimeView(CAmodelColumn[num].getAttachedModel().getName(),
				timeView[num].retTG());
		windowHandles.add(ETV);
		javax.swing.SwingUtilities.invokeLater(ETV);

	}

	public void controlCATimeView(String control) {
		if (control == "Reset") {

		} else {
			for (int i = 0; i < CAmodelColumn.length; i++) {
				if (timeView[i] != null)
					timeView[i].clock.start();
			}
		}

	}

	public void clearWindows() {
		for (ExternalTimeView etv : windowHandles) {
			etv.setInvisible();
		}
		windowHandles = new ArrayList<ExternalTimeView>(0);
	}

}
