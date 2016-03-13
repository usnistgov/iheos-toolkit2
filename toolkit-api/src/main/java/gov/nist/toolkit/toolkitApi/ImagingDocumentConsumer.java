package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitApi.DocumentConsumer;
import gov.nist.toolkit.toolkitApi.ToolkitServiceException;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

/**
 *
 */
public interface ImagingDocumentConsumer extends DocumentConsumer {
    RetrieveResponse retrieveImagingDocSet(RetrieveRequest request) throws ToolkitServiceException;
}
