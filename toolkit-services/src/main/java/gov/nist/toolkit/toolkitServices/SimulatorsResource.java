package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.BadSimConfigException;
import gov.nist.toolkit.actorfactory.client.SimExistsException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
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
@Path("simulators")
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
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
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
        catch (EnvironmentNotSelectedClientException e) {
            logger.error("SPI - environment not selected - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        catch (ThreadPoolExhaustedException e) {
            logger.error("SPI - thread pool exhausted - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        catch (BadSimConfigException e) {
            logger.info("SPI Bad sim config - " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (SimExistsException e) {
            logger.info("SPI Sim " + simId + " already exists");
            return Response.status(Response.Status.FOUND).build();
        }
        catch (Exception e) {
            logger.error("SPI Create simulator " + simId.toString() + " failed - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }

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


