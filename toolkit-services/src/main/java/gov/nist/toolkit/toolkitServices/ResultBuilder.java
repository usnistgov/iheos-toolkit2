package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.simcommon.client.*;
import gov.nist.toolkit.simcommon.server.*;
import gov.nist.toolkit.toolkitServicesCommon.resource.OperationResultResource;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsConfigurationException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ThreadPoolExhaustedException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axis2.AxisFault;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 *
 */
public class ResultBuilder {
    static Logger logger = Logger.getLogger(ResultBuilder.class.getName());

    protected Response mapExceptionToResponse(Throwable e, String simId, ResponseType responseType) {
        Response.Status status = null;
        String reason = "";
        int extendedCode = 0;
        String stackTrace = ExceptionUtil.exception_details(e);

        if (e instanceof EnvironmentNotSelectedClientException) {
            reason = "Environment does not exist - " + e.getMessage();
            extendedCode = OperationResultResource.ENVIRONMENT_DOES_NOT_EXIST;
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof EnvironmentNotSelectedException) {
            reason = "Environment does not exist - " + e.getMessage();
            extendedCode = OperationResultResource.ENVIRONMENT_DOES_NOT_EXIST;
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof ThreadPoolExhaustedException) {
            reason = "Thread pool exhausted - " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;   // 500
        }
        else if (e instanceof BadSimConfigException) {
            reason = "BadSimConfig " + e.getMessage();
            status = Response.Status.BAD_REQUEST;   // 400
        }
        else if (e instanceof BadSimRequestException) {
            reason = "BadSimRequest " + e.getMessage();
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
        else if (e instanceof NoContentException) {
            reason =  "Content does not exist";
            extendedCode = OperationResultResource.CONTENT_DOES_NOT_EXIST;
            status = Response.Status.NOT_FOUND;    // 404
        }
        else if (e instanceof SimPropertyTypeConflictException) {
            reason = "SimProperyTypeCohflict " + e.getMessage();
            status = Response.Status.CONFLICT;   // 409
        }
        else if (e instanceof IOException) {
            reason = "IOException " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;   // 500
        }
        else if (e instanceof AxisFault) {
            reason = "AxisFault " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsConfigurationException) {
            reason = "XdsConfiguration error " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof LoadKeystoreException) {
            reason = "LoadKeystore error " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsFormatException) {
            reason = "XdsFormat error " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        else if (e instanceof XdsInternalException) {
            reason = "XdsInternal error " + e.getMessage();
            status = Response.Status.INTERNAL_SERVER_ERROR;  // TODO - better code
        }
        if (status == null) {
            reason = "Create simulator " + simId + " failed";
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        OperationResultResource result = new OperationResultResource();
        result.setStatus(status);
        result.setExtendedCode(extendedCode);
        result.setReason(reason);
        result.setStackTrace(stackTrace);

        logger.info(result.toString());

        if (responseType == ResponseType.THROW) throw new WebApplicationException(result.asResponse().build());

        return result.asResponse().build();
    }

}
