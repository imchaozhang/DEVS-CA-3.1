package view.CAView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import facade.CAmodeling.FCASpaceModel;
import facade.modeling.FAtomicModel;
import facade.modeling.FModel;
import view.timeView.Event;

public class CATrackingControl {
	private int cntModel = 0;
	private static String rootModelName;
	private List allModels;
	
	private static CATracker[] CAmodelColumn;
	private static SpaceView caView;
	private List<Event> dataCAView;

	private int XSize, YSize;
	
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
					//System.out.println("X=" + x + ", Y=" + y + ": " + dataCAView.get(j));
					caView.cellView[x][y].addEvent(dataCAView.get(j));

				}

			}
		}
		caView.animate();

	}
	
	

}
