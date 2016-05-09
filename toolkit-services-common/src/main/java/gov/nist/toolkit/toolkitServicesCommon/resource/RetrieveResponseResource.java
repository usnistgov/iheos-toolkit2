package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

/**
 *
 */
@XmlRootElement
public class RetrieveResponseResource implements RetrieveResponse {
   String documentUid;
   String repositoryUid;
   String homeCommunityUid;
    String mimeType;
    byte[] documentContents;

    public RetrieveResponseResource() {}

    @Override
   public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
   public byte[] getDocumentContents() {
        return documentContents;
    }

    public void setDocumentContents(byte[] documentContents) {
        this.documentContents = documentContents;
    }

   /**
    * @return the {@link #documentUid} value.
    */
   @Override
   public String getDocumentUid() {
      return documentUid;
   }

   /**
    * @param documentUid the {@link #documentUid} to set
    */
   public void setDocumentUid(String documentUid) {
      this.documentUid = documentUid;
   }

   /**
    * @return the {@link #repositoryUid} value.
    */
   @Override
   public String getRepositoryUid() {
      return repositoryUid;
   }

   /**
    * @param repositoryUid the {@link #repositoryUid} to set
    */
   public void setRepositoryUid(String repositoryUid) {
      this.repositoryUid = repositoryUid;
   }

   /**
    * @return the {@link #homeCommunityUid} value.
    */
   @Override
   public String getHomeCommunityUid() {
      return homeCommunityUid;
   }

   /**
    * @param homeCommunityUid the {@link #homeCommunityUid} to set
    */
   public void setHomeCommunityUid(String homeCommunityUid) {
      this.homeCommunityUid = homeCommunityUid;
   }
    
    
}
