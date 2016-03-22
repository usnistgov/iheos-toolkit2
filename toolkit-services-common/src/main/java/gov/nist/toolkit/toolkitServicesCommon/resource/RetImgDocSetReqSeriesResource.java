/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetImgDocSetReqSeries;

/**
 * RAD-69 Series Request resource. Corresponds to the 
 * {@code <iherad:SeriesRequest/>} elements
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@XmlRootElement
public class RetImgDocSetReqSeriesResource
   implements RetImgDocSetReqSeries {
   
   String seriesInstanceUID;
   List <RetImgDocSetReqDocumentResource> retrieveImageDocumentRequests = 
      new ArrayList<>();

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageSeriesRequest#setSeriesInstanceUID(java.lang.String)
    */
   @Override
   public void setSeriesInstanceUID(String seriesInstanceUID) {
      this.seriesInstanceUID = seriesInstanceUID;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageSeriesRequest#getSeriesInstanceUID()
    */
   @Override
   public String getSeriesInstanceUID() {
      return seriesInstanceUID;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageSeriesRequest#setRetrieveImageDocumentRequests(java.util.List)
    */
   @Override
   public void setRetrieveImageDocumentRequests(
      List <RetImgDocSetReqDocumentResource> retrieveImageDocumentRequests) {
      this.retrieveImageDocumentRequests = retrieveImageDocumentRequests;
   }
   
   public void addDocumentRequest(RetImgDocSetReqDocumentResource documentRequest) {
      retrieveImageDocumentRequests.add(documentRequest);
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageSeriesRequest#getRetrieveImageDocumentRequest()
    */
   @Override
   public List <RetImgDocSetReqDocumentResource>
      getRetrieveImageDocumentRequests() {
      return retrieveImageDocumentRequests;
   }

}
