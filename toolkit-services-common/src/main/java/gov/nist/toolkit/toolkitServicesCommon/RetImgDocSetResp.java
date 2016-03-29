/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

import gov.nist.toolkit.toolkitServicesCommon.resource.RetImgDocSetRespDocumentResource;

/**
 * Retrieve Imaging Document Set Response Resource corresponds to
 * {@code <RetrieveDocumentSetResponse>} message. 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetResp {
   
   public String getAbbreviatedResponse();
   public void setAbbreviatedResponse(String abbreviatedResponse);
   
   public List <RetImgDocSetRespDocumentResource> getDocuments();
   public void setDocuments(List <RetImgDocSetRespDocumentResource> documents);
}
