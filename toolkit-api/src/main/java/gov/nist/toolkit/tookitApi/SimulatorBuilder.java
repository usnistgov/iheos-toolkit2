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
 * Builder class for creating and updating Simulator configurations.  Includes create/delete/update/get operations
 */
public class SimulatorBuilder {
    static Logger logger = Logger.getLogger(SimulatorBuilder.class);
    private WebTarget target;

    /**
     *
     * @param hostname where test engine runs
     * @param port where test engin runs
     */
    public SimulatorBuilder(String hostname, String port) {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client c = ClientBuilder.newClient(cc);
        Configuration conf = c.getConfiguration();
        logger.info(conf.getPropertyNames());
        target = c.target("http://" + hostname + ":" + port + "/xdstools2/rest/");
    }

    /**
     * Create new simulator with default parameters. There is currently no way to create a simulator with
     * custom parameters.  The parameters defining a simulator will change over time.  To create a custom
     * configuration use this call to create the simulator with the default parameters.  Update the SimConfig
     * returned and then issue an update(SimConfig) to update the simulator configuration.
     * @param id Simulator ID
     * @param user User creating Simulator.  Same as TestSession in Toolkit UI. The simulator ID must be unique for this user.
     * @param actorType Simulator type. See {@link gov.nist.toolkit.actortransaction.SimulatorActorTypes} for valid values.
     * @param environmentName Environment defines Affinity Domain coding schemes and TLS certificate for use with client.
     * @return Simulator configuration.
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public SimConfig create(String id, String user, String actorType, String environmentName) throws ToolkitServiceException {
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
        SimConfigResource config = response.readEntity(SimConfigResource.class);
        return config;
    }

    /**
     * Not for Public Use.
     * @param parms
     * @return
     * @throws ToolkitServiceException
     */
    public SimId create(BasicSimParameters parms) throws ToolkitServiceException {
        return create(parms.getId(), parms.getUser(), parms.getActorType(), parms.getEnvironmentName());
    }

    /**
     * Update the configuration of an existing Simulator. Any properties that are passed in SimConfig that are
     * not recognized will be silently ignored. Parameters passed with wrong type (String vs. boolean) will cause
     * ToolkitServiceException.
     *
     * Expected usage is to retrieve the configuration using the get() method,
     * update the parameters, and then submit the update using this call.
     * @param config new configuration
     * @return updated SimConfig if updates made or null if no changes accepted.
     * @throws ToolkitServiceException if anything goes wrong
     */
    public SimConfig update(SimConfig config) throws ToolkitServiceException {
        Response response = target.path(String.format("simulators/%s", config.getId()))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(config));
        int status = response.getStatus();
        if (status == Response.Status.ACCEPTED.getStatusCode())
            return response.readEntity(SimConfigResource.class);
        if (status == Response.Status.NOT_MODIFIED.getStatusCode())
            return null;
        throw new ToolkitServiceException(response);
    }

    /**
     * Delete an existing simulator. There is another call available using the SimId parameter type.  This
     * parameter type contains the raw ID and USER that are used here.  The two calls function identically.
     * @param id of simulator
     * @param user of simulator
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public void delete(String id, String user) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, null, null);
        delete(simId);
    }

    /**
     * Delete an existing simulator. There is another call available using separate raw ID and USER parameters.
     * USER and ID are components of the more formal SimId type.  The two calls function identically.
     * @param simId Simulator ID
     * @throws ToolkitServiceException if anything goes wrong
     */
    public void delete(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulators/" + simId.getUser() + "__" + simId.getId()).request().delete();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
    }

    /**
     * Not for Public Use.
     * @param parms
     * @throws ToolkitServiceException
     */
    public void delete(BasicSimParameters parms) throws ToolkitServiceException {
        delete(parms.getId(), parms.getUser());
    }

    /**
     * Get full configuration of existing Simulator as defined by its Simulator ID
     * @param simId simulator ID
     * @return simulator configuration
     * @throws ToolkitServiceException if anything goes wrong
     */
    public SimConfig get(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulators/" + simId.getFullId()).request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(SimConfigResource.class);
    }
}
