/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import gov.nist.toolkit.toolkitServicesCommon.OneImageRetrieve;

/**
 * RAD-69 One image retrieve resource
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@XmlRootElement
public class OneImageRetrieveResource extends SimIdResource implements OneImageRetrieve {
   /** Overrides the simulator endpoint if present */
   String endpoint = null;
   String studyUID = null;
   String seriesUID = null;
   String repositoryUniqueId = null;
   String documentUniqueId = null;
   String homeCommunityId = null;
   String xferSyntax = null;
   RequestFlavorResource flavor = new RequestFlavorResource();
   
   /**
    * @return the {@link #endpoint} value.
    */
   @Override
   public String getEndpoint() {
      return endpoint;
   }
   /**
    * @param endpoint the {@link #endpoint} to set
    */
   @Override
   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }
   /**
    * @return the {@link #studyUID} value.
    */
   @Override
   public String getStudyUID() {
      return studyUID;
   }
   /**
    * @param studyUID the {@link #studyUID} to set
    */
   @Override
   public void setStudyUID(String studyUID) {
      this.studyUID = studyUID;
   }
   /**
    * @return the {@link #seriesUID} value.
    */
   @Override
   public String getSeriesUID() {
      return seriesUID;
   }
   /**
    * @param seriesUID the {@link #seriesUID} to set
    */
   @Override
   public void setSeriesUID(String seriesUID) {
      this.seriesUID = seriesUID;
   }
   /**
    * @return the {@link #repositoryUniqueId} value.
    */
   @Override
   public String getRepositoryUniqueId() {
      return repositoryUniqueId;
   }
   /**
    * @param repositoryUniqueId the {@link #repositoryUniqueId} to set
    */
   @Override
   public void setRepositoryUniqueId(String repositoryUniqueId) {
      this.repositoryUniqueId = repositoryUniqueId;
   }
   /**
    * @return the {@link #documentUniqueId} value.
    */
   @Override
   public String getDocumentUniqueId() {
      return documentUniqueId;
   }
   /**
    * @param documentUniqueId the {@link #documentUniqueId} to set
    */
   @Override
   public void setDocumentUniqueId(String documentUniqueId) {
      this.documentUniqueId = documentUniqueId;
   }
   /**
    * @return the {@link #homeCommunityId} value.
    */
   @Override
   public String getHomeCommunityId() {
      return homeCommunityId;
   }
   /**
    * @param homeCommunityId the {@link #homeCommunityId} to set
    */
   @Override
   public void setHomeCommunityId(String homeCommunityId) {
      this.homeCommunityId = homeCommunityId;
   }

   /**
    * @return the {@link #xferSyntax} value.
    */
   @Override
   public String getXferSyntax() {
      return xferSyntax;
   }
   /**
    * @param xferSyntax the {@link #xferSyntax} to set
    */
   @Override
   public void setXferSyntax(String xferSyntax) {
      this.xferSyntax = xferSyntax;
   }
   public boolean isTls() {
       return flavor.isTls();
   }

   public void setTls(boolean tls) {
       flavor.setTls(tls);
   }

   @XmlTransient
   public boolean isDirect() {
      if (endpoint != null && endpoint.length() > 0) return true;
      return false;
   }

}
