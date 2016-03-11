package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

/**
 *
 */
public class XdsiImagingDocumentConsumer extends XdsDocumentConsumer implements ImagingDocumentConsumer {
    @Override
    public RetrieveResponse retrieveImagingDocSet(RetrieveRequest request) throws ToolkitServiceException {
        return engine.imagingRetrieve(request);
    }
}
