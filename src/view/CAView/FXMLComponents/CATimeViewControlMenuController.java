package view.CAView.FXMLComponents;

import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.CAView.CellView;
import view.CAView.FXMLComponents.TrackingTableData;

public class CATimeViewControlMenuController {

	private Stage dialogStage;
	private boolean okClicked = false;
	private CellView cellview;
	private boolean atLeastOneInputTracked;
	private boolean atLeastOneOutputTracked;
	private boolean[] inputPorts;
	private boolean[] outputPorts;
	List inputPortNames;
	List outputPortNames;

	String[] inputUnits;
	String[] outputUnits;

	@FXML
	Button btn_ok, btn_cancel;
	@FXML
	private CheckBox ck_timeview, ck_trackinglog, ck_phase, ck_sigma, ck_tl, ck_tn, ck_zero;
	@FXML
	private TextField tx_phase, tx_sigma, tx_tl, tx_tn, tx_x, tx_inc;
	@FXML
	private TableView<TrackingTableData> input_table, output_table;
	@FXML
	private TableColumn<TrackingTableData, String> inputPortName, inputPortUnit, outputPortName, outputPortUnit;
	@FXML
	private TableColumn<TrackingTableData, Boolean> inputPortSelected, outputPortSelected;

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
	private void setInputTable() {
		inputPortNames = cellview.catracker.getAttachedModel().getInputPortNames();
		ObservableList<TrackingTableData> input_data = FXCollections.observableArrayList();
		inputUnits = cellview.catracker.getInputPortUnits();
		inputPorts = cellview.catracker.gettrackInputPorts();

		for (int i = 0; i < inputPortNames.size(); i++) {
			TrackingTableData rowData = new TrackingTableData(String.valueOf(inputPortNames.get(i)), inputUnits[i],
					inputPorts[i]);
			input_data.add(rowData);
		}
		
		inputPortName.setCellValueFactory(cellData -> cellData.getValue().portNameProperty());
		inputPortUnit.setCellValueFactory(cellData -> cellData.getValue().portUnitProperty());
		inputPortSelected.setCellValueFactory(cellData -> cellData.getValue().portSelectedProperty());

//		inputPortName.setCellValueFactory(new PropertyValueFactory<>("portName"));
//		inputPortUnit.setCellValueFactory(new PropertyValueFactory<TrackingTableData,String>("portUnit"));
//		inputPortSelected.setCellValueFactory(new PropertyValueFactory<TrackingTableData,Boolean>("portSelecgted"));
		
		input_table.setItems(input_data);
	}
	
	
	@FXML
	private void setOutputTable() {
		outputPortNames = cellview.catracker.getAttachedModel().getOutputPortNames();
		ObservableList<TrackingTableData> output_data = FXCollections.observableArrayList();
		outputUnits = cellview.catracker.getOutputPortUnits();
		outputPorts = cellview.catracker.gettrackOutputPorts();

		for (int i = 0; i < outputPortNames.size(); i++) {
			TrackingTableData rowData = new TrackingTableData(String.valueOf(outputPortNames.get(i)), outputUnits[i],
					outputPorts[i]);
			output_data.add(rowData);
		}
		
		outputPortName.setCellValueFactory(cellData -> cellData.getValue().portNameProperty());
		outputPortUnit.setCellValueFactory(cellData -> cellData.getValue().portUnitProperty());
		outputPortSelected.setCellValueFactory(cellData -> cellData.getValue().portSelectedProperty());
		
		output_table.setItems(output_data);
	}

	@FXML
	public void initialize() {
		dialogStage = new Stage();
		dialogStage.setTitle("Cell: " + cellview.getI() + ", " + cellview.getJ());
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

		setInputTable();
		setOutputTable();

	}
}
