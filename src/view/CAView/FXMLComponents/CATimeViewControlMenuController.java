package view.CAView.FXMLComponents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.CAView.CellView;

public class CATimeViewControlMenuController {

	private Stage dialogStage;
	private boolean okClicked = false;
	private CellView cellview;

	@FXML
	Button btn_ok, btn_cancel;
	@FXML
	public CheckBox ck_timeview, ck_trackinglog, ck_phase, ck_sigma, ck_tl, ck_tn, ck_zero;
	@FXML
	public TextField tx_phase, tx_sigma, tx_tl, tx_tn, tx_x, tx_inc;

	public CATimeViewControlMenuController(CellView _cellview) {
		cellview = _cellview;
	}

	public CellView getCellView() {
		return cellview;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	protected void handleOk() {
		okClicked = true;
		if (ck_timeview.isSelected()) {
			cellview.isCATimeViewSelected = true;
		} else {
			cellview.isCATimeViewSelected = false;
		}
		if (ck_trackinglog.isSelected()) {
			cellview.istrackinglogselected = true;
		} else {
			cellview.istrackinglogselected = false;
		}
		if (ck_phase.isSelected()) {
			cellview.trackPhase = true;
		} else {
			cellview.trackPhase = false;
		}
		if (ck_sigma.isSelected()) {
			cellview.trackSigma = true;
		} else {
			cellview.trackSigma = false;
		}
		if (ck_tl.isSelected()) {
			cellview.trackTL = true;
		} else {
			cellview.trackTL = false;
		}
		if (ck_tn.isSelected()) {
			cellview.trackTN = true;
		} else {
			cellview.trackTN = false;
		}
		if (ck_zero.isSelected()) {
			cellview.isZeroTimeSelected = true;
		} else {
			cellview.isZeroTimeSelected = false;
		}

		cellview.phaseUnit = tx_phase.getText();
		cellview.sigmaUnit = tx_sigma.getText();
		cellview.tlUnit = tx_tl.getText();
		cellview.tnUnit = tx_tn.getText();
		cellview.xUnit = tx_x.getText();
		cellview.timeIncr = tx_inc.getText();

		dialogStage.close();

	}

	@FXML
	protected void handleCancel() {
		okClicked = false;
		dialogStage.close();
	}

	@FXML
	private void initialize() {
		dialogStage = new Stage();
		ck_timeview.setSelected(cellview.isCATimeViewSelected);
		ck_trackinglog.setSelected(cellview.istrackinglogselected);
		ck_phase.setSelected(cellview.trackPhase);
		ck_sigma.setSelected(cellview.trackSigma);
		ck_tl.setSelected(cellview.trackTL);
		ck_tn.setSelected(cellview.trackTN);
		ck_zero.setSelected(cellview.isZeroTimeSelected);
		tx_phase.setText(cellview.phaseUnit);
		tx_sigma.setText(cellview.sigmaUnit);
		tx_tl.setText(cellview.tlUnit);
		tx_tn.setText(cellview.tnUnit);
		tx_x.setText(cellview.xUnit);
		tx_inc.setText(cellview.timeIncr);
	}
}
