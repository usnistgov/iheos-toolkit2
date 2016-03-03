package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class RetrieveRequestResource extends SimIdResource {
    String repositoryUniqueId = null;
    String documentUniqueId = null;
    String homeCommunityId = null;
    RequestFlavorResource flavor;

    public RetrieveRequestResource() {}

    public String getRepositoryUniqueId() {
        return repositoryUniqueId;
    }

    public void setRepositoryUniqueId(String repositoryUniqueId) {
        this.repositoryUniqueId = repositoryUniqueId;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setDocumentUniqueId(String documentUniqueId) {
        this.documentUniqueId = documentUniqueId;
    }

    public String getHomeCommunityId() {
        return homeCommunityId;
    }

    public void setHomeCommunityId(String homeCommunityId) {
        this.homeCommunityId = homeCommunityId;
    }

    public boolean isTls() {
        return flavor.isTls();
    }

    public void setTls(boolean tls) {
        flavor.setTls(tls);
    }
}
