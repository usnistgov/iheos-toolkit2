package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.StoredQueryRequest;

/**
 *
 */
public class XdsDocumentConsumer extends AbstractActor implements DocumentConsumer {
    @Override
    public LeafClassRegistryResponse queryForLeafClass(StoredQueryRequest request) throws ToolkitServiceException {
        return engine.queryForLeafClass(request);
    }

//    @Override
//    public RefList queryForObjectRef(String queryId, Map<String, List<String>> parameters) {
//        return null;
//    }

    @Override
    public RetrieveResponse retrieve(String repositoryUniqueId, String documentUniqueId) {
        return null;
    }
}
