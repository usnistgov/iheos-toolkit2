package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimId;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */

@Path("/simulators/{id}")
public class SimulatorResource {

    @GET
    public SimIdBean getSim(@PathParam("id") String id) {
        return new SimIdBean(new SimId(id));
    }

}
