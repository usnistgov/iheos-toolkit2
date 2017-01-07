package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;

/**
 *
 */
public interface RespondingGateway extends AbstractActorInterface {
    LeafClassList FindDocuments(String patientId) throws ToolkitServiceException;
}
