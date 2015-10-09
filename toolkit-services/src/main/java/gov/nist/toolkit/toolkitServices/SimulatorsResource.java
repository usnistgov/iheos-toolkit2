package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.services.server.ToolkitApi;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("simulators")
public class SimulatorsResource {
    static Logger logger = Logger.getLogger(SimulatorsResource.class);

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
        SimId simId = simIdBean.asSimId();
        logger.info("SPI Create simulator " + simId.toString());
        String errors = simId.validateState();
        ToolkitApi api = ToolkitApi.forServiceUse();
        if (errors != null) {
            logger.error("Create simulator " + simId.toString() + " failed...\n" + errors);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            api.createSimulator(simId);
        } catch (Exception e) {
            logger.error("Create simulator " + simId.toString() + " failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }

}
