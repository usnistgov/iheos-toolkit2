/**
 * 
 */
package gov.nist.toolkit.toolkitServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.toolkitServicesCommon.TParameters;

/**
 * Restful server class.
 * Allows running transactions as test steps using the {@link ToolkitApi}
 * @author ralph moulton wustl 3/2/16
 */
@Path("/transaction")
public class TransactionsController {
   ToolkitApi api;
   
   static Logger logger = Logger.getLogger(TransactionsController.class);

   @Context
   private UriInfo _uriInfo;
   
   public TransactionsController() {
      logger.info("***** creating TransactionsController *****");
      api = ToolkitApi.forServiceUse();
   }
   
   @GET
   @Path("/RetrieveImagingDocSet")
   // @Consumes("application/json")
   @Produces("application/json")
   public Response retrieveImagingDocSet(@BeanParam TParameters tPars) {
      try {
      TestInstance testInstance = new TestInstance("RetrieveImagingDocSet");
      Map<String, String> params = new HashMap<>();
      params.put("repuid", "1.5.3.12.543.167.12");
      List<Result> results = api.runTest("ralph", "IDS", testInstance, null, params, true);
      return Response.ok(results).build();
      } catch (Exception e) {
         return new ResultBuilder().mapExceptionToResponse(e, "IDS", ResponseType.RESPONSE);
      }
   }

}
