package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.toolkitServicesCommon.SimConfigResource;
import gov.nist.toolkit.toolkitServicesCommon.SimIdResource;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 */
@Path("/simulators")
public class SimulatorsController {
    static Logger logger = Logger.getLogger(SimulatorsController.class);

    public SimulatorsController() {
        ResourceConfig resourceConfig = new ResourceConfig(SimulatorsController.class);
        resourceConfig.property(ServerProperties.TRACING, "ALL");
    }

    @Context
    private UriInfo _uriInfo;

    /**
     * Create new simulator with default settings.
     * @param simId - Simulator ID
     * @return
     *     Status.OK if successful
     *     Status.BAD_REQUEST if Simulator ID is invalid
     *     Status.INTERNAL_SERVER_ERROR if necessary
     *     Simulator config if successful
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createSim(final SimIdResource simIdResource) {
        SimId simId = ToolkitFactory.asServerSimId(simIdResource);
        logger.info(String.format("Create simulator %s", simId.toString()));
        try {
            String errors = simId.validateState();
            if (errors != null)
                throw new BadSimConfigException(String.format("Create simulator %s - %s", simId.toString(), errors));
            ToolkitApi api = ToolkitApi.forServiceUse();
            Simulator simulator = api.createSimulator(simId);
            SimConfigResource bean = ToolkitFactory.asSimConfigBean(simulator.getConfig(0));
            return Response
                    .ok(bean)
                    .header("Location",
                            String.format("%s/%s", _uriInfo.getAbsolutePath().toString(),
                                    simId.getId()))
                    .build();
        }
        catch (Exception e) {
            return new ResultBuilder().mapExceptionToResponse(e, simId, ResponseType.RESPONSE);
        }
    }

    @POST
    @Path("{id}")
    public Response update(final SimConfigResource config) {
        return null;
    }

    /**
     * Delete simulator with id
     * @param id
     * @return
     */
    @DELETE
    @Path("{id}")
    public Response deleteSim(@PathParam("id") String id) {
        logger.info("Delete " + id);
        ToolkitApi api = ToolkitApi.forServiceUse();
        SimId simId = new SimId(id);
        try {
            api.deleteSimulator(simId);
        }
        catch (Throwable e) {
            return new ResultBuilder().mapExceptionToResponse(e, simId, ResponseType.THROW);
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Get full SimId given id
     * @param id
     * @return
     */
    @GET
    @Produces("application/json")
    @Path("/{id}")
    public Response getSim(@PathParam("id") String id) {
        logger.info("GET simulator/" +  id);
        SimId simId = new SimId(id);
        try {
            ToolkitApi api = ToolkitApi.forServiceUse();
            SimulatorConfig config = api.getConfig(simId);
            if (config == null) throw new NoSimException("");
            SimConfigResource bean = ToolkitFactory.asSimConfigBean(config);
            return Response.ok(bean).build();
        } catch (Exception e) {
            return new ResultBuilder().mapExceptionToResponse(e, simId, ResponseType.RESPONSE);
        }
    }


}


