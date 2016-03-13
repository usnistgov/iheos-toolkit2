package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

/**
 *
 */
public class XdsiImagingDocumentConsumer extends XdsDocumentConsumer implements ImagingDocumentConsumer {
    @Override
    public RetrieveResponse retrieveImagingDocSet(RetrieveRequest request) throws ToolkitServiceException {
        return getEngine().imagingRetrieve(request);
    }
}
