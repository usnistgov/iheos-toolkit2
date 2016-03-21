/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

/**
 * RAD-69 SeriesRequest interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReqSeries {

   void setSeriesInstanceUID (String seriesIntanceUID);
   void setRetrieveImageDocumentRequests (List<RetImgDocSetReqDocument> retrieveRequests);
   String getSeriesInstanceUID();
   List<RetImgDocSetReqDocument> getRetrieveImageDocumentRequests();
}
