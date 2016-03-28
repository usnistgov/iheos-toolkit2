/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetImgDocSetRespDocument;

/**
 * Retrieve Imaging Document Set Response Document Resource corresponds to
 * {@code <DocumentResponse>} element in {@code <RetrieveDocumentSetResponse>}
 * message. 
 * 
 * Note: Essentially duplicate of RetrieveResposeResource at this point, but
 * thinking that class may change in the future. Plus it is at a higher
 * level than this.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
@XmlRootElement
public class RetImgDocSetRespDocumentResource 
   implements RetImgDocSetRespDocument{
   String documentUid;
   String repositoryUid;
   String homeCommunityUid;
   String mimeType;
   byte[] documentContents;

 public RetImgDocSetRespDocumentResource() {}

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
