/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import gov.nist.toolkit.toolkitServicesCommon.RetImgDocSetReq;

/**
 * RAD-69 Request resource. Corresponds to the 
 * {@code <iherad:RetrieveImagingDocumentSetRequest/>} element
 * @author Ralph Moulton / MIR WUSTL IHE Development
 * Project <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
@XmlRootElement
public class RetImgDocSetReqResource extends SimIdResource 
   implements RetImgDocSetReq {
   /**
    * IDS endpoint URL. If present, this overrides the sim ID, and is
    * referred to as a "direct" query.
    */
   String endpoint = null;
   /**
    * Message directory. If present, indicates the message directory for the
    * transaction. The SOAP request and response headers and the SOAP Request
    * Body are put in this directory.
    */
   String messageDir = null;

   List <RetImgDocSetReqStudyResource> studyRequests = new ArrayList <>();
   List <String> transferSyntaxUIDs = new ArrayList <>();
   RequestFlavorResource flavor = new RequestFlavorResource();

   
   /**
    * @return the {@link #messageDir} value.
    */
   public String getMessageDir() {
      return messageDir;
   }

   /**
    * @param messageDir the {@link #messageDir} to set
    */
   public void setMessageDir(String messageDir) {
      this.messageDir = messageDir;
   }

   /**
    * @return the {@link #endpoint} value.
    */
   public String getEndpoint() {
      return endpoint;
   }

   /**
    * @param endpoint the {@link #endpoint} to set
    */
   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }
   
   @XmlTransient
   public boolean isDirect() {
      return endpoint != null && endpoint.length() > 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * setRetrieveImageStudyRequests(java.util.List)
    */
   @Override
   public void setRetrieveImageStudyRequests(List <RetImgDocSetReqStudyResource> studyRequests) {
      this.studyRequests = studyRequests;
   }
   
   public void addStudyRequest(RetImgDocSetReqStudyResource studyRequest) {
      studyRequests.add(studyRequest);
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * setTransferSystaxUIDs(java.util.List)
    */
   @Override
   public void setTransferSyntaxUIDs(List <String> transferSyntaxUIDs) {
      this.transferSyntaxUIDs = transferSyntaxUIDs;
   }
   
   public void addTransferSyntaxUid(String transferSyntaxUID) {
      transferSyntaxUIDs.add(transferSyntaxUID);
   }

   /*
    * (non-Javadoc)
    * 
    * @see gov.nist.toolkit.toolkitServicesCommon.RetrieveImageRequest#
    * getRetrieveImageStudyRequests()
    */
   @Override
   public List <RetImgDocSetReqStudyResource> getRetrieveImageStudyRequests() {
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
