/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

import gov.nist.toolkit.toolkitServicesCommon.resource.RetImgDocSetReqDocumentResource;

/**
 * RAD-69 SeriesRequest interface
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetReqSeries {

   void setSeriesInstanceUID (String seriesIntanceUID);
   void setRetrieveImageDocumentRequests (List<RetImgDocSetReqDocumentResource> retrieveRequests);
   String getSeriesInstanceUID();
   List<RetImgDocSetReqDocumentResource> getRetrieveImageDocumentRequests();
}
