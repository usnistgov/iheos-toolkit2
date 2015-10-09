package gov.nist.toolkit.tookitSpi;

import gov.nist.toolkit.toolkitServicesCommon.SimId;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by bill on 10/9/15.
 */
public class SimulatorBuilder {
    private WebTarget target;

    public SimulatorBuilder(String hostname, String port) {
        Client c = ClientBuilder.newClient();
        target = c.target("http://" + hostname + ":" + port + "/xdstools2/rest/");
    }

    public SimId create(String id, String user, String actorType, String environmentName) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, actorType, environmentName);
        SimIdBean bean = new SimIdBean(simId);
        Response response = target.path("simulators").request().put(Entity.xml(bean));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response.getStatus());
        return simId;
    }
}
