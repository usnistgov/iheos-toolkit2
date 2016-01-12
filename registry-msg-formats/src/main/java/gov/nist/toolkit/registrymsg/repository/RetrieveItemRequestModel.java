package gov.nist.toolkit.registrymsg.repository;

/**
 *
 */
public class RetrieveItemRequestModel {
    String homeId = "";
    String repositoryId = "";
    String documentId = "";

    public String getHomeId() {
        return homeId;
    }

    public boolean hasHomeId() { return homeId != null && !homeId.equals(""); }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
