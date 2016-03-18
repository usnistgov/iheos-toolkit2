/**
 * 
 */
package gov.nist.toolkit.registrymsg.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates {@code <iherad:RetrieveImagingDocumentSetRequest>} 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetrieveImageRequestModel {
   
   List<RetrieveImageStudyRequestModel> studyRequests = new ArrayList<>();
   
   List<String> transferSyntaxUIDs = new ArrayList<>();

   /**
    * @return the {@link #studyRequests} value.
    */
   public List <RetrieveImageStudyRequestModel> getStudyRequests() {
      return studyRequests;
   }

   /**
    * @param studyRequests the {@link #studyRequests} to set
    */
   public void setStudyRequests(List <RetrieveImageStudyRequestModel> studyRequests) {
      this.studyRequests = studyRequests;
   }
   
   public void addStudyRequest(RetrieveImageStudyRequestModel sModel) {
      studyRequests.add(sModel);
   }

   /**
    * @return the {@link #transferSyntaxUIDs} value.
    */
   public List <String> getTransferSyntaxUIDs() {
      return transferSyntaxUIDs;
   }

   /**
    * @param transferSyntaxUIDs the {@link #transferSyntaxUIDs} to set
    */
   public void setTransferSyntaxUIDs(List <String> transferSyntaxUIDs) {
      this.transferSyntaxUIDs = transferSyntaxUIDs;
   }
   
   public void addTransferSyntaxUID(String uid) {
      transferSyntaxUIDs.add(uid);
   }
   
   

}
