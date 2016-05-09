/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

/**
 * RAD-69 DocumentRequest interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReqDocument {
   void setRepositoryUniqueId(String repositoryUniqueId);
   void setDocumentUniqueId(String documentUniqueId);
   void setHomeCommunityId(String homeCommunityId);
   String getRepositoryUniqueId();
   String getDocumentUniqueId();
   String getHomeCommunityId();
}
