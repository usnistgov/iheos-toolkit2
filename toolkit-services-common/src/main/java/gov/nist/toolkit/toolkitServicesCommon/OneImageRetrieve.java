/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

/**
 * RAD-69 One image retrieve interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface OneImageRetrieve {
   void setEndpoint(String endpoint);
   void setRepositoryUniqueId(String repositoryUniqueId);
   void setDocumentUniqueId(String documentUniqueId);
   void setHomeCommunityId(String homeCommunityId);
   void setStudyUID(String studyUID);
   void setSeriesUID(String seriesUID);
   void setXferSyntax(String xferSyntax);

   String getEndpoint();
   String getRepositoryUniqueId();
   String getDocumentUniqueId();
   String getHomeCommunityId();
   String getStudyUID();
   String getSeriesUID();
   String getXferSyntax();
}
