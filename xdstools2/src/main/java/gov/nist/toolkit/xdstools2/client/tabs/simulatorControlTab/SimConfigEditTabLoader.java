package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;

public class SimConfigEditTabLoader {

    SimulatorControlTab hostTab;
    SimulatorConfig simConfig;

    public SimConfigEditTabLoader(SimulatorControlTab hostTab, SimulatorConfig simConfig) {
        this.hostTab = hostTab;
        this.simConfig = simConfig;
    }

    public void load() {
        if (ActorType.ONDEMAND_DOCUMENT_SOURCE.getShortName().equals(simConfig.getActorType())) {
            // This simulator requires content state initialization
            OddsEditTab editTab;
            editTab = new OddsEditTab(hostTab, simConfig);
            editTab.onTabLoad(true, "ODDS");
        } else

        {
            // Generic state-less type simulators
            GenericQueryTab editTab = new EditTab(hostTab, simConfig);
            editTab.onTabLoad(true, "SimConfig " + simConfig.getId().toString());
        }
    }
}
