package gov.nist.toolkit.transactionNotificationService;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 */

@Path("toolkitcallback")
public class CallbackResource {
    static final Logger logger = Logger.getLogger(CallbackResource.class);

    public CallbackResource() {
        logger.info("CallbackResouce loaded");
        ResourceConfig resourceConfig = new ResourceConfig(CallbackResource.class);
        resourceConfig.property(ServerProperties.TRACING, "ALL");
    }

    @POST
    public Response callback(TransactionLogBean log)  {
//        TransactionLogBean log = new TransactionLogBean();
        String callbackClassName;
        Class<?> genericCallbackClass;
        try {
            logger.info("Toolkit Transaction Notification...");
            callbackClassName = log.getCallbackClassName();
            logger.info("...classname - " + callbackClassName);
            if (callbackClassName == null)
                return Response.status(Response.Status.BAD_REQUEST).header("X-Toolkit-Error", "Callback class is null").build();
            try {
                genericCallbackClass = getClass().getClassLoader().loadClass(callbackClassName);
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).header("X-Toolkit-Error", "Callback class " + callbackClassName + " does not exist").build();
            }
            logger.info("...exists");
            Class<TransactionNotification> callbackClass;
            callbackClass = (Class<TransactionNotification>) genericCallbackClass;
            logger.info("...implements Callback interface");
            TransactionNotification transactionNotificationInstance;
            try {
                transactionNotificationInstance = callbackClass.newInstance();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).header("X-Toolkit-Error", "Cannot create instance of Callback class " + callbackClassName + " - requires no argument constructor").build();
            }
            logger.info("...instance built - calling");
            transactionNotificationInstance.notify(log);
            logger.info("...Done");
        }
        catch (Exception e) {
            logger.error("Callback error - " + e.getClass().getName());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
