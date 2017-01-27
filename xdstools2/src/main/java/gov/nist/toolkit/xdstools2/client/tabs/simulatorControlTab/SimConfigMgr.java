package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;


import com.google.gwt.user.client.ui.FlowPanel;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;



/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 *
 */
public class SimConfigMgr extends BaseSimConfigMgr {
    /**
     *
     */
    SimConfigMgr(SimulatorControlTab simulatorControlTab, FlowPanel panel, SimulatorConfig config, String testSession) {
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
