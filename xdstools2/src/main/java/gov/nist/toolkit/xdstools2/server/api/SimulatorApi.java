package gov.nist.toolkit.xdstools2.server.api;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bill on 6/15/15.
 */
public class SimulatorApi {
    Session session;

    public SimulatorApi(Session session) {
        this.session = session;
    }

    public Simulator create(String actorTypeName, String simID) throws Exception {
        return new SimulatorServiceManager(session).getNewSimulator(actorTypeName, simID);
    }

    public void delete(String simID) throws Exception {
        SimulatorConfig config = new SimulatorConfig(simID, "", null);
        new SimulatorServiceManager(session).deleteConfig(config);
    }

    public boolean exists(String simId) {
        return new SimDb().exists(simId);
    }

    public Map<String, String> getSimulatorsAndTypes() {
        Map<String, String> map = new HashMap<String, String>();
        SimDb simHook = new SimDb();

        for (String simId : simHook.getAllSimIds()) {
            String actor = simHook.getActorForSimulator();
            map.put(simId, actor);
        }

        return map;
    }
}
