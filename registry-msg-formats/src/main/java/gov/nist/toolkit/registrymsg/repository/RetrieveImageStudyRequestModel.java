/**
 * 
 */
package gov.nist.toolkit.registrymsg.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates {@code <iherad:StudyRequest>} element for {@code 
 * <iherad:RetrieveImagingDocumentSetRequest>} 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class RetrieveImageStudyRequestModel {

   String studyInstanceUID = "";
   
   List<RetrieveImageSeriesRequestModel> seriesRequests = new ArrayList<>();

   /**
    * @return the {@link #studyInstanceUID} value.
    */
   public String getStudyInstanceUID() {
      return studyInstanceUID;
   }

   /**
    * @param studyInstanceUID the {@link #studyInstanceUID} to set
    */
   public void setStudyInstanceUID(String studyInstanceUID) {
      this.studyInstanceUID = studyInstanceUID;
   }

   /**
    * @return the {@link #seriesRequests} value.
    */
   public List <RetrieveImageSeriesRequestModel> getSeriesRequests() {
      return seriesRequests;
   }

   /**
    * @param seriesRequests the {@link #seriesRequests} to set
    */
   public void setSeriesRequests(List <RetrieveImageSeriesRequestModel> seriesRequests) {
      this.seriesRequests = seriesRequests;
   }
   
   public void addSeriesRequest(RetrieveImageSeriesRequestModel sModel) {
      seriesRequests.add(sModel);
   }
}
