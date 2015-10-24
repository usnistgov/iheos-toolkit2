package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.OperationResultResource;

import javax.ws.rs.core.Response;

/**
 * Created by bill on 10/9/15.
 */
public class ToolkitServiceException extends Exception {
    OperationResultResource result = null;

    public ToolkitServiceException(OperationResultResource result) {
        super(result.getReason());
        this.result = result;
    }

    public ToolkitServiceException(Response response) {
        result = new OperationResultResource(response);
    }

    public int getCode() {
        return result.getStatus().getStatusCode();
    }

    public OperationResultResource getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
