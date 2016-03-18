package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;
import gov.nist.toolkit.toolkitServicesCommon.resource.OneImageRetrieveResource;

/**
 *
 */
public class XdsiImagingDocumentConsumer extends XdsDocumentConsumer implements ImagingDocumentConsumer {
    @Override
    public RetrieveResponse retrieveImagingDocSet(OneImageRetrieveResource request) throws ToolkitServiceException {
        return getEngine().imagingRetrieve(request);
    }
}
