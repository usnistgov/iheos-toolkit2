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
public interface RetrieveImageRequest extends SimId {

   void setRetrieveImageStudyRequests(List<RetrieveImageStudyRequest> studyRequests);
   void setTransferSystaxUIDs(List<String> transferSyntaxUIDs);
   List<RetrieveImageStudyRequest> getRetrieveImageStudyRequests();
   List<String> getTransferSyntaxUIDs();
}
