package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;


import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;



/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 *
 */
public class SimConfigMgr extends BaseSimConfigMgr {
    /**
     *
     */
    SimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {
        super(simulatorControlTab, panel, config, testSession);
    }

    @Override
    public void displayBasicSimulatorConfig() {
        super.displayBasicSimulatorConfig();
    }


    @Override
    public void displayInPanel() {
        super.displayInPanel();

        addTable(getTbl());

        addSaveHandler();
    }
}
