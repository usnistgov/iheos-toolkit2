package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.SimId;

/**
 *
 */
public interface RetrieveRequest extends SimId {
    void setRepositoryUniqueId(String repositoryUniqueId);
    void setDocumentUniqueId(String documentUniqueId);
    void setHomeCommunityId(String homeCommunityId);
}
