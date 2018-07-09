package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.command.GetSimConfigsCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.State;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.Token;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimConfigsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * This class automatically uses the correct EditTab instance.
 */
public class SimConfigEditorTabLoader {

    SimulatorControlTab hostTab;
    SimulatorConfig simConfig;
    GenericQueryTab editTab;

    public SimConfigEditorTabLoader() {
    }

    public SimConfigEditorTabLoader(SimulatorControlTab hostTab, SimulatorConfig simConfig) {
        this.hostTab = hostTab;
        this.simConfig = simConfig;
    }

    public void load(State state) {

        List<SimId> ids = new ArrayList<>();
        SimId si = new SimId(new TestSession(state.getValue(Token.TEST_SESSION)), state.getValue(Token.SYSTEM_ID));
        ids.add(si);

        new GetSimConfigsCommand() {
            @Override
            public void onComplete(List<SimulatorConfig> configs) {
                simConfig = configs.get(0);
                load();
            }
        }.run(new GetSimConfigsRequest(ClientUtils.INSTANCE.getCommandContext(),ids));
    }

    public void load() {
        load(true, null);
    }

    public void load(boolean select, String nameIn) {
        if (ActorType.ONDEMAND_DOCUMENT_SOURCE.getShortName().equals(simConfig.getActorType())) {
            // This simulator requires content state initialization
            editTab = new OddsEditTab(hostTab, simConfig);
            String name = nameIn;
            if (name==null || "".equals(name)) {
               name = "ODDS";
            }
            editTab.onTabLoad(select, name);
        } else

        {
            // Generic state-less type simulators
            editTab = new EditTab(hostTab, simConfig);
            String name = nameIn;
            if (name==null || "".equals(name)) {
                name = "SimConfig " + simConfig.getId().toString();
            }
            editTab.onTabLoad(select, name);
        }
    }

    public GenericQueryTab getTab() {
        return editTab;
    }
}
