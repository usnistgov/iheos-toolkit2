package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.RefList;

/**
 *
 */
public interface InitiatingGateway extends AbstractActorInterface {
    RefList FindDocuments(String patientId) throws ToolkitServiceException;
}
