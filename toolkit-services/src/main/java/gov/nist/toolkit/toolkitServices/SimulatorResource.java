package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 *
 */

@Path("simulator")
public class SimulatorResource {
    static Logger logger = Logger.getLogger(SimulatorResource.class);

    public SimulatorResource() {
        ResourceConfig resourceConfig = new ResourceConfig(SimulatorResource.class);
        resourceConfig.property(ServerProperties.TRACING, "ALL");
    }

    @GET
    @Path("/{id}")
    public SimIdBean getSim(@PathParam("id") String id) {
        logger.info("getSim id is " + id);
        return ToolkitFactory.asSimIdBean(new SimId(id));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id)  {
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
