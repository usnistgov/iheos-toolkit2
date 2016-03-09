package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

/**
 *
 */
public interface ImagingDocumentConsumer extends DocumentConsumer {
    RetrieveResponse retrieveImagingDocSet(RetrieveRequest request) throws ToolkitServiceException;
}
