/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetImgDocSetResp;

/**
 * Retrieve Imaging Document Set Response Resource corresponds to
 * {@code <RetrieveDocumentSetResponse>} message. 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@XmlRootElement
public class RetImgDocSetRespResource implements RetImgDocSetResp {
   /**
    * {@code <RetrieveDocumentSetResponse>} element as String, but 
    * with document contents replaced with "..." for brevity.
    */
   String abbreviatedResponse;
   
   List<RetImgDocSetRespDocumentResource> documents = 
      new ArrayList<>();

   /**
    * @return the {@link #abbreviatedResponse} value.
    */
   @Override
   public String getAbbreviatedResponse() {
      return abbreviatedResponse;
   }

   /**
    * @param abbreviatedResponse the {@link #abbreviatedResponse} to set
    */
   @Override
   public void setAbbreviatedResponse(String abbreviatedResponse) {
      this.abbreviatedResponse = abbreviatedResponse;
   }

   /**
    * @return the {@link #documents} value.
    */
   @Override
   public List <RetImgDocSetRespDocumentResource> getDocuments() {
      return documents;
   }

   /**
    * @param documents the {@link #documents} to set
    */
   @Override
   public void setDocuments(List <RetImgDocSetRespDocumentResource> documents) {
      this.documents = documents;
   }
   
   

}
