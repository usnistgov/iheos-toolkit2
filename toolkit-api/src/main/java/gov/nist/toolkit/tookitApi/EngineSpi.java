package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.actortransaction.SimulatorActorType;
import gov.nist.toolkit.toolkitServicesCommon.*;
import gov.nist.toolkit.toolkitServicesCommon.resource.*;
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
 * Builder class for building and using Simulator configurations.  Simulators come in two flavors:
 * client and server.  Client sims represent Actors that initiate transactions.  Server
 * sims represent Actors that start their work by accepting transactions. Basic operations on simulators include
 * create and delete sims; and getting and updating the configurations.
 *
 * There are a second set of operations that initiate specific transactions from Client sims.  An example is sendXdr()
 * which initiates an XDR Provide and Register transaction from a Document Source sim.
 */
public class EngineSpi {
    static Logger logger = Logger.getLogger(EngineSpi.class);
    private WebTarget target;

    /**
     * This will initialize the SPI to contact the test engine at
     * http://hostname:port/xdstools2
     * @param urlRoot URL Root - http://hostname:port/xdstools2 for example
     */
    public EngineSpi(String urlRoot) {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client c = ClientBuilder.newClient(cc);
        Configuration conf = c.getConfiguration();
        logger.info(conf.getPropertyNames());
        logger.info("target is " + urlRoot + "/rest/");
        target = c.target(urlRoot + "/rest/");
    }

    public WebTarget getTarget() { return target; }

    public SimConfig create(String id, String user, SimulatorActorType actorType, String environmentName) throws ToolkitServiceException {
        String actorTypeString = actorType.getName();
        SimId simId = ToolkitFactory.newSimId(id, user, actorTypeString, environmentName);
        SimIdResource bean = new SimIdResource(simId);
        logger.info(bean.describe());
        Response response = target
                .path("simulators")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(bean));
        if (response.getStatus() != 200) {
            logger.error("status is " + response.getStatus());
            for (String key : response.getHeaders().keySet()) {
                logger.error(key + ": " + response.getHeaderString(key));
            }
            throw new ToolkitServiceException(response.readEntity(OperationResultResource.class));
        }
        return response.readEntity(SimConfigResource.class);
    }

    /**
     * Not for Public Use.
     * @param parms
     * @return
     * @throws ToolkitServiceException
     */
    public SimConfig create(BasicSimParameters parms) throws ToolkitServiceException {
        return create(parms.getId(), parms.getUser(), parms.getActorType(), parms.getEnvironmentName());
    }

    public SimConfig update(SimConfig config) throws ToolkitServiceException {
        Response response = target
                .path(String.format("simulators/%s", config.getId()))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(config));
        int status = response.getStatus();
        if (status == Response.Status.ACCEPTED.getStatusCode())
            return response.readEntity(SimConfigResource.class);
        if (status == Response.Status.NOT_MODIFIED.getStatusCode())
            return null;
        logger.error("Update returned " + response.getStatusInfo());
        throw new ToolkitServiceException(response);
    }

    public void delete(String id, String user) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, null, null);
        delete(simId);
    }

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

    public SimConfig get(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulators/" + simId.getFullId()).request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(SimConfigResource.class);
    }

    /**
     * Send an XDR Provide and Register transaction.  The engine is identified by parameters to the class
     * constructor.  The simulator id is contained in the SendRequest object.
     * @param request SendRequest object
     * @return
     * @throws ToolkitServiceException
     */
    public RawSendResponse sendXdr(RawSendRequest request) throws ToolkitServiceException {
        request.setTransactionName("xdrpr");
        Response response = target
                .path(String.format("simulators/%s/xdr", request.getFullId()))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(RawSendResponseResource.class);
    }

    public LeafClassRegistryResponse queryForLeafClass(StoredQueryRequest request) throws ToolkitServiceException {
        Response response = target
                .path(String.format("simulators/%s/xds/QueryForLeafClass", request.getFullId()))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(LeafClassRegistryResponseResource.class);
    }

}
