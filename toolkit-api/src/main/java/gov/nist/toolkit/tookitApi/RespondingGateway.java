package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;

/**
 * Created by bill on 12/22/15.
 */
public interface RespondingGateway {
    LeafClassList FindDocuments(String patientId) throws ToolkitServiceException;
}
