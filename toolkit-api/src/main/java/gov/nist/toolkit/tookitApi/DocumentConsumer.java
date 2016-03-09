package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.StoredQueryRequest;

/**
 *
 */
public interface DocumentConsumer extends AbstractActorInterface {

    LeafClassRegistryResponse queryForLeafClass(StoredQueryRequest request) throws ToolkitServiceException;
//    RefList queryForObjectRef(String queryId, Map<String, List<String>> parameters);
    RetrieveResponse retrieve(String repositoryUniqueId, String documentUniqueId);
}
