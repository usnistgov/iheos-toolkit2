package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;
import gov.nist.toolkit.toolkitServicesCommon.resource.OneImageRetrieveResource;

/**
 *
 */
public interface ImagingDocumentConsumer extends DocumentConsumer {
    RetrieveResponse retrieveImagingDocSet(OneImageRetrieveResource request) throws ToolkitServiceException;
}
