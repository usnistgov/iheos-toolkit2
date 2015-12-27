package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.toolkitServicesCommon.OperationResultResource;
import gov.nist.toolkit.xdsexception.*;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 *
 */
public class ResultBuilder {
    static Logger logger = Logger.getLogger(ResultBuilder.class);

    protected Response mapExceptionToResponse(Throwable e, SimId simId, ResponseType responseType) {
        Response.Status status = null;
        String reason = "";
        int extendedCode = 0;

        if (e instanceof EnvironmentNotSelectedClientException) {
            reason = "Environment does not exist - " + e.getMessage();
            extendedCode = OperationResultResource.ENVIRONMENT_DOES_NOT_EXIST;
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof EnvironmentNotSelectedException) {
            logger.info(ExceptionUtil.exception_details(e));
            reason = "Environment does not exist - " + e.getMessage();
            extendedCode = OperationResultResource.ENVIRONMENT_DOES_NOT_EXIST;
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof ThreadPoolExhaustedException) {
            reason = "Thread pool exhausted - " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;   // 500
        }
        else if (e instanceof BadSimConfigException) {
            reason = e.getMessage();
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof BadSimRequestException) {
            reason = e.getMessage();
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof SimExistsException) {
            reason = "Sim " + simId + " already exists";
            status = Response.Status.FOUND;   // 302
        }
        else if (e instanceof NoSimException) {
            reason = "Sim " + simId + " does not exist";
            extendedCode = OperationResultResource.SIM_DOES_NOT_EXIST;
            status = Response.Status.NOT_FOUND;    // 404
        }
        else if (e instanceof SimPropertyTypeConflictException) {
            reason = e.getMessage();
            status = Response.Status.CONFLICT;   // 409
        }
        else if (e instanceof IOException) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;   // 500
        }
        else if (e instanceof AxisFault) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsConfigurationException) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof LoadKeystoreException) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsFormatException) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsInternalException) {
            reason = e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        if (status == null) {
            reason = "Create simulator " + simId.toString() + " failed - " + ExceptionUtil.exception_details(e);
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        OperationResultResource result = new OperationResultResource();
        result.setStatus(status);
        result.setExtendedCode(extendedCode);
        result.setReason(reason);

        logger.info(result);

        if (responseType == ResponseType.THROW) throw new WebApplicationException(result.asResponse().build());

        return result.asResponse().build();
    }

}
