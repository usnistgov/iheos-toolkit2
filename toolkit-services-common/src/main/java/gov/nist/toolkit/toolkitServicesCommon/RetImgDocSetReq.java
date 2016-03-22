/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

import gov.nist.toolkit.toolkitServicesCommon.resource.RetImgDocSetReqStudyResource;

/**
 * RAD-69 Image Request interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReq extends SimId {

   void setRetrieveImageStudyRequests(List<RetImgDocSetReqStudyResource> studyRequests);
   void setTransferSyntaxUIDs(List<String> transferSyntaxUIDs);
   List<RetImgDocSetReqStudyResource> getRetrieveImageStudyRequests();
   List<String> getTransferSyntaxUIDs();
}
