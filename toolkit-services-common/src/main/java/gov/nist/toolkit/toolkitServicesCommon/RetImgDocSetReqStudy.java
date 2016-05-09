/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

import gov.nist.toolkit.toolkitServicesCommon.resource.RetImgDocSetReqSeriesResource;

/**
 * RAD-69 StudyRequest interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReqStudy {
   
   void setStudyInstanceUID (String studyInstanceUID);
   void setRetrieveImageSeriesRequests(List<RetImgDocSetReqSeriesResource> retrieveImageSeriesRequests);
   String getStudyInstanceUID();
   List<RetImgDocSetReqSeriesResource> getRetrieveImageSeriesRequests();

}
