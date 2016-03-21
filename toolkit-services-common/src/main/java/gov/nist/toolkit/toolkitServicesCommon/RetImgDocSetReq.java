/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

/**
 * RAD-69 Image Request interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReq extends SimId {

   void setRetrieveImageStudyRequests(List<RetImgDocSetReqStudy> studyRequests);
   void setTransferSystaxUIDs(List<String> transferSyntaxUIDs);
   List<RetImgDocSetReqStudy> getRetrieveImageStudyRequests();
   List<String> getTransferSyntaxUIDs();
}
