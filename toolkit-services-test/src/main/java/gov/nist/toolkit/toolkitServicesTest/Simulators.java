package gov.nist.toolkit.toolkitServicesTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/simulators")
public class Simulators {
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response setSim(final SimIdBean simId) {
        System.out.println("Create new fake simulator " + simId.asSimId().toString());
        return Response.status(Response.Status.OK).build();
    }

}
