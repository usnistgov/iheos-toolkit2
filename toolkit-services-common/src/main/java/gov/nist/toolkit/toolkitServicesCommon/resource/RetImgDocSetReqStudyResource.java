/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetImgDocSetReqStudy;

/**
 * RAD-69 Study Request resource. Corresponds to the 
 * {@code <iherad:StudyRequest/>} elements
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@XmlRootElement
public class RetImgDocSetReqStudyResource implements RetImgDocSetReqStudy {
   
   String studyInstanceUID = "";
   List<RetImgDocSetReqSeriesResource> retrieveImageSeriesRequests = new ArrayList<>();

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
   public void setRetrieveImageSeriesRequests(List <RetImgDocSetReqSeriesResource> retrieveImageSeriesRequests) {
      this.retrieveImageSeriesRequests = retrieveImageSeriesRequests;
   }
   
   public void addSeriesRequest(RetImgDocSetReqSeriesResource seriesRequest) {
      retrieveImageSeriesRequests.add(seriesRequest);
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
   public List <RetImgDocSetReqSeriesResource> getRetrieveImageSeriesRequests() {
      return retrieveImageSeriesRequests;
   }

}
