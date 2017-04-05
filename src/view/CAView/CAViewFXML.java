package view.CAView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class CAViewFXML {

	@FXML
	private CheckBox PSelect, cb_State, cb_Sigma, cb_TL, cb_StatusChanged;
	@FXML
	private Button PBMaxLengthButton, ANSpeedButton;
	@FXML
	private TextField PBMaxLength, PBFrom, PBTo, PBInterval, ANSpeed;
	@FXML
	private Slider PBTracking, ANSpeedSlider;
	@FXML
	private Text PBStatus,CellChangedNumber;

	@FXML
	protected void actionApplySettingsButton(ActionEvent event) {
		ANSpeed.setText("Sign in button pressed");
		System.out.println("The button was clicked!");
	}

}
