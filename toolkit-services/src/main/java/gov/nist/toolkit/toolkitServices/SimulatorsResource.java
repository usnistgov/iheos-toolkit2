package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.BadSimConfigException;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimExistsException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.ThreadPoolExhaustedException;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 *
 */
@Path("/simulators")
public class SimulatorsResource {
    static Logger logger = Logger.getLogger(SimulatorsResource.class);

    public SimulatorsResource() {
        ResourceConfig resourceConfig = new ResourceConfig(SimulatorResource.class);
        resourceConfig.property(ServerProperties.TRACING, "ALL");
    }



    /**
     * Create new simulator with default settings.
     * @param simId - Simulator ID
     * @return
     *     Status.OK if successful
     *     Status.BAD_REQUEST if Simulator ID is invalid
     *     Status.INTERNAL_SERVER_ERROR if necessary
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response setSim(final SimIdBean simIdBean) {
        SimId simId = ToolkitFactory.asServerSimId(simIdBean);
        logger.info("SPI Create simulator " + simId.toString());
        String errors = simId.validateState();
        ToolkitApi api = ToolkitApi.forServiceUse();
        if (errors != null) {
            logger.error("SPI Create simulator " + simId.toString() + " failed...\n" + errors);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            api.createSimulator(simId);
        }
        catch (Exception e) {
            return mapExceptionToResponse(e, simId, ResponseType.RESPONSE);
        }
        return Response.status(Response.Status.OK).build();
    }

    static public Response mapExceptionToResponse(Throwable e, SimId simId, ResponseType responseType) {
        logger.info("Map Exception to HTTP Response - " + e.getClass().getName() + " - " + e.getMessage());
        Response.Status status = null;
        if (e instanceof EnvironmentNotSelectedClientException) {
            logger.error("SPI - environment not selected - " + e.getMessage());
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        if (e instanceof ThreadPoolExhaustedException) {
            logger.error("SPI - thread pool exhausted - " + e.getMessage());
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        if (e instanceof BadSimConfigException) {
            logger.info("SPI Bad sim config - " + e.getMessage());
            status = Response.Status.BAD_REQUEST;
        }
        if (e instanceof SimExistsException) {
            logger.info("SPI Sim " + simId + " already exists");
            status = Response.Status.FOUND;
        }
        if (e instanceof NoSimException) {
            logger.info("SPI Sim " + simId + " does not exist");
            status = Response.Status.NOT_FOUND;
        }
        if (status == null) {
            logger.error("SPI Create simulator " + simId.toString() + " failed - " + ExceptionUtil.exception_details(e));
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        if (responseType == ResponseType.THROW) throw new WebApplicationException(status);
        return Response.status(status).build();
    }


    /**
     * Delete simulator with id
     * @param id
     * @return
     */
    @POST
    @Path("_delete/{id}")
    public Response deleteSim(@PathParam("id") String id) {
        logger.info("Delete " + id);
        ToolkitApi api = ToolkitApi.forServiceUse();
        try {
            api.deleteSimulatorIfItExists(new SimId(id));
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }



}


