package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;
import gov.nist.toolkit.toolkitServicesCommon.StoredQueryRequest;

/**
 *
 */
public interface DocumentConsumer extends AbstractActorInterface {

    LeafClassRegistryResponse queryForLeafClass(StoredQueryRequest request) throws ToolkitServiceException;
//    RefList queryForObjectRef(String queryId, Map<String, List<String>> parameters);
    RetrieveResponse retrieve(RetrieveRequest request) throws ToolkitServiceException;
}
