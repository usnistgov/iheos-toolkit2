package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.*;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
public class SimulatorBuilder {
    static Logger logger = Logger.getLogger(SimulatorBuilder.class);
    private WebTarget target;

    public SimulatorBuilder(String hostname, String port) {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client c = ClientBuilder.newClient(cc);
        Configuration conf = c.getConfiguration();
        logger.info(conf.getPropertyNames());
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
        SimIdResource bean = new SimIdResource(simId);
        Response response = target.path("simulators").request(MediaType.APPLICATION_JSON).post(Entity.json(bean));
        if (response.getStatus() != 200) {
            logger.error("status is " + response.getStatus());
            for (String key : response.getHeaders().keySet()) {
                logger.error(key + ": " + response.getHeaderString(key));
            }
            throw new ToolkitServiceException(response.readEntity(OperationResultResource.class));
        }
        return simId;
    }

    public SimId create(BasicSimParameters parms) throws ToolkitServiceException {
        return create(parms.getId(), parms.getUser(), parms.getActorType(), parms.getEnvironmentName());
    }

    public void delete(String id, String user) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, null, null);
        SimIdResource bean = new SimIdResource(simId);
        Response response = target.path("simulators/" + user + "__" + id).request().delete();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
    }

    public void delete(BasicSimParameters parms) throws ToolkitServiceException {
        delete(parms.getId(), parms.getUser());
    }

    public SimConfigResource getSimConfig(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulators/" + simId.getFullId()).request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(SimConfigResource.class);
    }
}
