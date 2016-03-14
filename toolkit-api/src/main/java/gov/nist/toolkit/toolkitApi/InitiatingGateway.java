package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

/**
 *
 */
public interface InitiatingGateway extends SimConfig {
    LeafClassList FindDocuments(String patientId) throws ToolkitServiceException;
}
