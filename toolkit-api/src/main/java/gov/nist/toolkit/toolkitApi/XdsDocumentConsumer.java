package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;
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
    public RetrieveResponse retrieve(RetrieveRequest request) throws ToolkitServiceException {
        return engine.retrieve(request);
    }
}
