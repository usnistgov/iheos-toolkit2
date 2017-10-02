package gov.nist.toolkit.toolkitApi;


import gov.nist.toolkit.configDatatypes.server.SimulatorActorType;
import gov.nist.toolkit.toolkitServicesCommon.*;
import gov.nist.toolkit.toolkitServicesCommon.resource.*;
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmReport;
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmReportResource;
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmRequest;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
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
    static Logger logger = Logger.getLogger("SYSTEM");
    
    private WebTarget target;

    /**
     * This will initialize the SPI to contact the test engine at
     * http://hostname:port/xdstools2
     * @param urlRoot URL Root - http://hostname:port/xdstools2 for example
     */
    public EngineSpi(String urlRoot) {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client c = ClientBuilder.newClient(cc);
        c.register(new LoggingFilter(java.util.logging.Logger.getLogger("SYSTEM"), true));
        Configuration conf = c.getConfiguration();
        logger.info(conf.getPropertyNames());
        logger.info("target is " + urlRoot + "/rest/");
        target = c.target(urlRoot + "/rest/");
    }

    public WebTarget getTarget() { return target; }

    public SimConfigResource create(String id, String user, SimulatorActorType actorType, String environmentName) throws ToolkitServiceException {
        String actorTypeString = actorType.getName();
        SimId simId = ToolkitFactory.newSimId(id, user, actorTypeString, environmentName, actorType == SimulatorActorType.FHIR_SERVER);
        SimIdResource bean = new SimIdResource(simId);
        logger.info(bean.describe());
        Response response = target
                .path("simulators")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(bean));
        exceptionOnNot200(response);
//        if (response.getStatus() != 200) {
//            logger.error("status is " + response.getStatus());
//            for (String key : response.getHeaders().keySet()) {
//                logger.error(key + ": " + response.getHeaderString(key));
//            }
//            throw new ToolkitServiceException(response.readEntity(OperationResultResource.class));
//        }
        SimConfigResource scr = response.readEntity(SimConfigResource.class);
        return scr;
    }

    private void exceptionOnNot200(Response response) throws ToolkitServiceException {
        if (response.getStatus() != 200) {
            logger.error("status is " + response.getStatus());
            for (String key : response.getHeaders().keySet()) {
                logger.error(key + ": " + response.getHeaderString(key));
            }
            throw new ToolkitServiceException(response.readEntity(OperationResultResource.class));
        }
    }

    /**
     * Not for Public Use.
     * @param parms BasicSimParameters
     * @return new SimConfig instance
     * @throws ToolkitServiceException on error
     */
    public SimConfig create(BasicSimParameters parms) throws ToolkitServiceException {
        return create(parms.getId(), parms.getUser(), parms.getActorType(), parms.getEnvironmentName());
    }

    public SimConfigResource update(SimConfig config) throws ToolkitServiceException {
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

    /**
     * This cannot be used to delete a FHIR sim
     * @param id
     * @param user
     * @throws ToolkitServiceException
     */
    public void delete(String id, String user) throws ToolkitServiceException {
        SimId simId = ToolkitFactory.newSimId(id, user, null, null);
        delete(simId);
    }

    /**
     * You must use this call to delete a FHIR sim
     * @param simId
     * @throws ToolkitServiceException
     */
    public void delete(SimId simId) throws ToolkitServiceException {
        if (simId.isFhir()) {
            SimIdResource bean = new SimIdResource(simId);
            logger.info(bean.describe());
            Response response = target
                    .path("simulators/_delete")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bean));
            exceptionOnNot200(response);
//            if (response.getStatus() != 200) {
//                logger.error("status is " + response.getStatus());
//                for (String key : response.getHeaders().keySet()) {
//                    logger.error(key + ": " + response.getHeaderString(key));
//                }
//                throw new ToolkitServiceException(response.readEntity(OperationResultResource.class));
//            }

        } else {
            Response response = target.path("simulators/" + simId.getUser() + "__" + simId.getId()).request().delete();
            if (response.getStatus() != 200)
                throw new ToolkitServiceException(response);
        }
    }

    /**
     * Not for Public Use.
     * @param parms BasicSimParameters for simulator to delete
     * @throws ToolkitServiceException on error, for example, no such simulator.
     */
    public void delete(BasicSimParameters parms) throws ToolkitServiceException {
        delete(parms.getId(), parms.getUser());
    }

    /**
     * Returns the SimConfig for an existing simulator.
    * @param simId simulator id for sim to fetch.
    * @return SimConfig instance
    * @throws ToolkitServiceException on error, for example if the simulator
    * does not exist.
    */
   public SimConfig get(SimId simId) throws ToolkitServiceException {
        Response response = target.path("simulators/" + simId.getFullId()).request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(SimConfigResource.class);
    }

    /**
     * Send an XDR Provide and Register transaction.  The engine is identified by parameters to the class
     * constructor.  The simulator id is contained in the SendRequest model.
     * @param request SendRequest model
     * @return Response Object
     * @throws ToolkitServiceException on error
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

    public RetrieveResponse retrieve(RetrieveRequest request) throws ToolkitServiceException {
        Response response = target
                .path(String.format("simulators/%s/xds/retrieve", request.getFullId()))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(RetrieveResponseResource.class);
    }

  // public RetrieveResponse imagingRetrieve(RetrieveImageRequestResource request)
   public RetImgDocSetRespResource imagingRetrieve(RetImgDocSetReqResource request, String type)
      throws ToolkitServiceException {
      Entity <RetImgDocSetReqResource> entity = Entity.json(request);
      String path = String.format("simulators/%s/xdsi/retrieve/%s", request.getFullId(), type);
      WebTarget t = target.path(path);
      Builder b = t.request(MediaType.APPLICATION_JSON);
      Response response = b.post(entity);
      if (response.getStatus() != 200)
         throw new ToolkitServiceException(response);
      return response.readEntity(RetImgDocSetRespResource.class);
   }

    public XdmReport validateXDM(XdmRequest request) throws ToolkitServiceException {
        Response response = target
                .path("simulators/xdmValidation")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(XdmReportResource.class);

    }

}
