package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

/**
 *
 */
public interface InitiatingGateway extends SimConfig {
    LeafClassList FindDocuments(String patientId) throws ToolkitServiceException;
}
