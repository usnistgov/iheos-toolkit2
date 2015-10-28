package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.OperationResultResource;

import javax.ws.rs.core.Response;

/**
 * All Toolkit API calls may return this exception if there is a problem reported by the server (test engine).
 */
public class ToolkitServiceException extends Exception {
    OperationResultResource result = null;

    protected ToolkitServiceException(OperationResultResource result) {
        super(result.getReason());
        this.result = result;
    }

    protected ToolkitServiceException(Response response) {
        result = new OperationResultResource(response);
    }

    /**
     * Get HTTP status code
     * @return HTTP status code
     */
    public int getCode() {
        return result.getStatus().getStatusCode();
    }

    /**
     * Get extended status code defined by test engine. Each API call defines its own extended codes.
     * @return Extended status code defined by test engine for the call that failed.
     */
    public int getExtendedCode() {
        return result.getExtendedCode();
    }

    /**
     * Get description of error.
     * @return  Description of error.
     */
    public String getReason() {
        return result.getReason();
    }

    protected OperationResultResource getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
