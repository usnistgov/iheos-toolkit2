/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveImageSeriesRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest;

/**
 * RAD-69 Study Request resource. Corresponds to the 
 * {@code <iherad:StudyRequest/>} elements
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@XmlRootElement
public class RetrieveImageStudyRequestResource implements RetrieveImageStudyRequest {
   
   String studyInstanceUID = "";
   List<RetrieveImageSeriesRequest> retrieveImageSeriesRequests = new ArrayList<>();

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest#setStudyInstanceUID(java.lang.String)
    */
   @Override
   public void setStudyInstanceUID(String studyInstanceUID) {
      this.studyInstanceUID = studyInstanceUID;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest#setRetrieveImageSeriesRequests(java.util.List)
    */
   @Override
   public void setRetrieveImageSeriesRequests(List <RetrieveImageSeriesRequest> retrieveImageSeriesRequests) {
      this.retrieveImageSeriesRequests.addAll(retrieveImageSeriesRequests);
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest#getStudyInstanceUID()
    */
   @Override
   public String getStudyInstanceUID() {
      return studyInstanceUID;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest#getRetrieveImageSeriesRequest()
    */
   @Override
   public List <RetrieveImageSeriesRequest> getRetrieveImageSeriesRequests() {
      return retrieveImageSeriesRequests;
   }

}
