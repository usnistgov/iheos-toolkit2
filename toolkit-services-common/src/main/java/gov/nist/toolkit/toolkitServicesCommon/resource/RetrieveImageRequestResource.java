/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveImageStudyRequest;

/**
 * RAD-69 Request resource. Corresponds to the 
 * {@code <iherad:RetrieveImagingDocumentSetRequest/>} element
 * @author Ralph Moulton / MIR WUSTL IHE Development
 * Project <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
@XmlRootElement
public class RetrieveImageRequestResource extends SimIdResource 
   implements RetrieveImageRequest {

   List <RetrieveImageStudyRequest> studyRequests = new ArrayList <>();
   List <String> transferSyntaxUIDs = new ArrayList <>();
   RequestFlavorResource flavor = new RequestFlavorResource();

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * setRetrieveImageStudyRequests(java.util.List)
    */
   @Override
   public void setRetrieveImageStudyRequests(List <RetrieveImageStudyRequest> studyRequests) {
      this.studyRequests.addAll(studyRequests);
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * setTransferSystaxUIDs(java.util.List)
    */
   @Override
   public void setTransferSystaxUIDs(List <String> transferSyntaxUIDs) {
      this.transferSyntaxUIDs.addAll(transferSyntaxUIDs);
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * getRetrieveImageStudyRequests()
    */
   @Override
   @XmlElementWrapper
   public List <RetrieveImageStudyRequest> getRetrieveImageStudyRequests() {
      return studyRequests;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * getTransferSyntaxUIDs()
    */
   @Override
   public List <String> getTransferSyntaxUIDs() {
      return transferSyntaxUIDs;
   }

   public boolean isTls() {
      return flavor.isTls();
   }

   public void setTls(boolean tls) {
      flavor.setTls(tls);
   }

}
