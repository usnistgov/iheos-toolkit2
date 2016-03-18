/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest;

/**
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
@XmlRootElement
public class RetrieveImageDocumentRequestResource
   implements RetrieveImageDocumentRequest {

   String repositoryUniqueId = null;
   String documentUniqueId = null;
   String homeCommunityId = null;

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * setRepositoryUniqueId(java.lang.String)
    */
   @Override
   public void setRepositoryUniqueId(String repositoryUniqueId) {
      this.repositoryUniqueId = repositoryUniqueId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * setDocumentUniqueId(java.lang.String)
    */
   @Override
   public void setDocumentUniqueId(String documentUniqueId) {
      this.documentUniqueId = documentUniqueId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * setHomeCommunityId(java.lang.String)
    */
   @Override
   public void setHomeCommunityId(String homeCommunityId) {
      this.homeCommunityId = homeCommunityId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * getRepositoryUniqueId()
    */
   @Override
   public String getRepositoryUniqueId() {
      return repositoryUniqueId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * getDocumentUniqueId()
    */
   @Override
   public String getDocumentUniqueId() {
      return documentUniqueId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageDocumentRequest#
    * getHomeCommunityId()
    */
   @Override
   public String getHomeCommunityId() {
      return homeCommunityId;
   }

}
