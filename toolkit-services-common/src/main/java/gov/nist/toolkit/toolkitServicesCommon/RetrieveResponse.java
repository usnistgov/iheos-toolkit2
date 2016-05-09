package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public interface RetrieveResponse {
   String getDocumentUid();
   String getRepositoryUid();
   String getHomeCommunityUid();
    String getMimeType();
    byte[] getDocumentContents();
}
