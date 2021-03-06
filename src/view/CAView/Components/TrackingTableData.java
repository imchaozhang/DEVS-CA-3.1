package view.CAView.Components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TrackingTableData {
	private final SimpleStringProperty portName;
    private final SimpleStringProperty portUnit;
    private final SimpleBooleanProperty portSelected;

    public TrackingTableData(String pName, String pUnit, Boolean pSelected) {
        this.portName = new SimpleStringProperty(pName);
        this.portUnit = new SimpleStringProperty(pUnit);
        this.portSelected = new SimpleBooleanProperty(pSelected);
    }

    public String getPortName() {
        return portName.get();
    }

    public void setPortName(String pName) {
    	portName.set(pName);
    }
    
    public StringProperty portNameProperty() {
        return portName;
    }
    

    public String getPortUnit() {
        return portUnit.get();
    }

    public void setPortUnit(String pUnit) {
    	portUnit.set(pUnit);
    }
    
    public StringProperty portUnitProperty() {
        return portUnit;
    }
    
    public boolean getPortSelected() {
        return portSelected.get();
    }

    public void setPortSelected(boolean pSelected) {
    	portSelected.set(pSelected);
    }
    
    public BooleanProperty portSelectedProperty() {
        return portSelected;
    }
}

