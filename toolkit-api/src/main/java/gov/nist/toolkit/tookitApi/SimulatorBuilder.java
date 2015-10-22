package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.SimConfigBean;
import gov.nist.toolkit.toolkitServicesCommon.SimId;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory;
import org.apache.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
public class SimulatorBuilder {
    static Logger logger = Logger.getLogger(SimulatorBuilder.class);
    private WebTarget target;

    public SimulatorBuilder(String hostname, String port) {
        Client c = ClientBuilder.newClient();
        target = c.target("http://" + hostname + ":" + port + "/xdstools2/rest/");
    }

    /**
     * Create simulator
     * @param id
     * @param user
     * @param actorType
     * @param environmentName
     * @return
     * @throws ToolkitServiceException
     */
    public SimId create(String id, String user, String actorType, String environmentName) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, actorType, environmentName);
        SimIdBean bean = new SimIdBean(simId);
        Response response = target.path("simulators").request(MediaType.APPLICATION_XML).post(Entity.xml(bean));
        if (response.getStatus() != 200) {
            logger.error("status is " + response.getStatus());
            for (String key : response.getHeaders().keySet()) {
                logger.error(key + ": " + response.getHeaderString(key));
            }
            throw new ToolkitServiceException(response.getStatus());
        }
        return simId;
    }

    public SimId create(BasicSimParameters parms) throws ToolkitServiceException {
        return create(parms.getId(), parms.getUser(), parms.getActorType(), parms.getEnvironmentName());
    }

    public void delete(String id, String user) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, null, null);
        SimIdBean bean = new SimIdBean(simId);
        Response response = target.path("simulators/_delete/" + user + "__" + id).request().post(Entity.xml(bean));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response.getStatus());
    }

    public void delete(BasicSimParameters parms) throws ToolkitServiceException {
        delete(parms.getId(), parms.getUser());
    }

    public SimConfigBean getSimConfig(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulator/" + simId.getFullId()).request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response.getStatus());
        return response.readEntity(SimConfigBean.class);
    }

}
