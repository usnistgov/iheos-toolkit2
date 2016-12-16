/**
 * 
 */
package gov.nist.toolkit.registrymsgold.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates {@code <iherad:SeriesRequest>} element for {@code 
 * <iherad:RetrieveImagingDocumentSetRequest>} 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetrieveImageSeriesRequestModel {
   
   String seriesInstanceUID = "";
   
   /** 
    * encapsulates {@code <ihe:DocumentRequest> } elements.
    */
   List<RetrieveItemRequestModel> documentRequests = new ArrayList<>();

   /**
    * @return the {@link #seriesInstanceUID} value.
    */
   public String getSeriesInstanceUID() {
      return seriesInstanceUID;
   }

   /**
    * @param seriesInstanceUID the {@link #seriesInstanceUID} to set
    */
   public void setSeriesInstanceUID(String seriesInstanceUID) {
      this.seriesInstanceUID = seriesInstanceUID;
   }

   /**
    * @return the {@link #documentRequests} value.
    */
   public List <RetrieveItemRequestModel> getDocumentRequests() {
      return documentRequests;
   }

   /**
    * @param documentRequests the {@link #documentRequests} to set
    */
   public void setDocumentRequests(List <RetrieveItemRequestModel> documentRequests) {
      this.documentRequests = documentRequests;
   }

   public void addDocumentRequest(RetrieveItemRequestModel iModel) {
      documentRequests.add(iModel);
   }
}
