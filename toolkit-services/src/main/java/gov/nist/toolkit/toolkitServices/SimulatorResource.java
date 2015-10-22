package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.toolkitServicesCommon.Constants;
import gov.nist.toolkit.toolkitServicesCommon.HeaderList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfigBean;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 */

@Path("simulator")
public class SimulatorResource {
    static Logger logger = Logger.getLogger(SimulatorResource.class);

    public SimulatorResource() {
    }

    /**
     * Create sim
     */
    @POST
    @Path("/{id}")
    public Response create(SimConfigBean simConfigBean) {
        SimId simId = ToolkitFactory.asServerSimId(simConfigBean);
        logger.info("SPI Create simulator " + simId.toString());
        String errors = simId.validateState();
        ToolkitApi api = ToolkitApi.forServiceUse();
        if (errors != null) {
            logger.error("SPI Create simulator " + simId.toString() + " failed...\n" + errors);
            return Response.status(Response.Status.BAD_REQUEST)
                    .header(Constants.TOOLKIT_ERROR, errors)
                    .build();
        }
        try {
            HeaderList hdrs = new HeaderList();
            // create with default values
            Simulator sim = api.createSimulator(simId);

            // override default values with any included in request
            for (String propName : simConfigBean.getPropertyNames()) {
                boolean valueIsBoolean = simConfigBean.isBoolean(propName);
                boolean ignored = true;
                for (SimulatorConfig config : sim.getConfigs()) {
                    if (config.hasConfig(propName)) {
                        SimulatorConfigElement ele = config.getConfigEle(propName);
                        if (valueIsBoolean && ele.isBoolean()) {
                            ele.setValue(simConfigBean.asBoolean(propName));
                            ignored = false;
                        }
                        if (!valueIsBoolean && !ele.isBoolean()) {
                            ele.setValue(simConfigBean.asString(propName));
                            ignored = false;
                        }
                    }
                }
                if (ignored)
                    hdrs.add(Constants.TOOLKIT_ERROR, "Ignored " + propName);
            }
            return hdrs.addHeaders(Response.status(Response.Status.OK)).build();

        }
        catch (Exception e) {
            return SimulatorsResource.mapExceptionToResponse(e, simId, ResponseType.RESPONSE);
        }

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
            SimConfigBean bean = ToolkitFactory.asSimConfigBean(config);
            logger.info("Returning " + bean.toString());
            return Response.ok(bean).build();
        } catch (Exception e) {
            return SimulatorsResource.mapExceptionToResponse(e, simId, ResponseType.RESPONSE);
        }
//        return null;  // cannot reach
    }

//    @GET
//    @Produces(MediaType.APPLICATION_XML)
//    @Path("/{id}")
//    public SimConfigBean getSim(@PathParam("id") String id) {
//        logger.info("GET simulator/" +  id);
//        SimId simId = new SimId(id);
//        try {
//            ToolkitApi api = ToolkitApi.forServiceUse();
//            SimulatorConfig config = api.getConfig(simId);
//            if (config == null) throw new NoSimException("");
//            SimConfigBean bean = ToolkitFactory.asSimConfigBean(config);
//            logger.info("Returning " + bean.toString());
//            return bean;
//        } catch (Exception e) {
//            SimulatorsResource.mapExceptionToResponse(e, simId, ResponseType.THROW);
//        }
//        return null;  // cannot reach
//    }

}
