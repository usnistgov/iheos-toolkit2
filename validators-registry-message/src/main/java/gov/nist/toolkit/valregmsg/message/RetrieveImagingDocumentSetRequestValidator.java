package gov.nist.toolkit.valregmsg.message;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.message.MessageBodyContainer;

/**
 * Validate a RetrieveImagingDocumentSetRequest message. (RAD-69)
 * 
 * @author bill
 */
public class RetrieveImagingDocumentSetRequestValidator extends AbstractMessageValidator {
   OMElement xml;
   ErrorRecorderBuilder erBuilder;
   MessageValidatorEngine mvc;

   @SuppressWarnings("javadoc")
   public RetrieveImagingDocumentSetRequestValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder,
      MessageValidatorEngine mvc) {
      super(vc);
      this.erBuilder = erBuilder;
      this.mvc = mvc;
   }

   @Override
   public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
      this.er = er;
      er.registerValidator(this);

      MessageBodyContainer cont = (MessageBodyContainer) mvc.findMessageValidator("MessageBodyContainer");
      xml = cont.getBody();

      /*
       * This block contains the validation code. When any error is discovered,
       * we break out of this block, ending further evaluation, and return.
       */
      validation: {

         if (xml == null) {
            er.err(XdsErrorCode.Code.XDSRepositoryError, "RetrieveImagingDocumentSetRequest: top element null", this,
               "");
            break validation;
         }

         List <OMElement> studyRequests = XmlUtil.childrenWithLocalName(xml, "StudyRequest");
         if (studyRequests.isEmpty()) {
            er.err(XdsErrorCode.Code.XDSIRequestError,
               "RetrieveImagingDocumentSetRequest: must contain as least one StudyRequest element", this,
               "RAD TF-3 4.69.5");
            break validation;
         }
         for (OMElement studyRequest : studyRequests) {
            if (StringUtils.isBlank(studyRequest.getAttributeValue(qn("studyInstanceUID")))) {
               er.err(XdsErrorCode.Code.XDSIRequestError,
                  "RetrieveImagingDocumentSetRequest StudyRequest element : must have studyInstanceUID attribute", this,
                  "RAD TF-3 4.69.5");
               break validation;
            }
            List <OMElement> seriesRequests = XmlUtil.childrenWithLocalName(studyRequest, "SeriesRequest");
            if (seriesRequests.isEmpty()) {
               er.err(XdsErrorCode.Code.XDSIRequestError,
                  "RetrieveImagingDocumentSetRequest StudyRequest element: must contain as least one SeriesRequest element",
                  this, "RAD TF-3 4.69.5");
               break validation;
            }
            for (OMElement seriesRequest : seriesRequests) {
               if (StringUtils.isBlank(seriesRequest.getAttributeValue(qn("seriesInstanceUID")))) {
                  er.err(XdsErrorCode.Code.XDSIRequestError,
                     "RetrieveImagingDocumentSetRequest SeriesRequest element : must have seriesInstanceUID attribute",
                     this, "RAD TF-3 4.69.5");
                  break validation;
               }
               List <OMElement> documentRequests = XmlUtil.childrenWithLocalName(seriesRequest, "DocumentRequest");
               if (documentRequests.isEmpty()) {
                  er.err(XdsErrorCode.Code.XDSIRequestError,
                     "RetrieveImagingDocumentSetRequest SeriesRequest element: must contain as least one DocumentRequest element",
                     this, "RAD TF-3 4.69.5");
                  break validation;
               }
               for (OMElement documentRequest : documentRequests) {
                  if (one(documentRequest, "RepositoryUniqueId")) break validation;
                  if (one(documentRequest, "DocumentUniqueId"))   break validation;
                  if (vc.isXC && one(documentRequest, "RepositoryUniqueId")) break validation;
               }
            } // EO SeriesRequest loop

         } // EO StudyRequest loop

      } // EO validation block
      
      er.unRegisterValidator(this);
      return;
      
   } // EO run method
   
   /**
    * Validates that the parent element contains one and only one child element
    * name, which itself contains some valid text, that is, not just empty,
    * blank, or whitespace.
    * @param parent element
    * @param name of child element
    * @return boolean true on error, false if all validations passed.
    */
   private boolean one(OMElement parent, String name) {
      List<OMElement> children = XmlUtil.childrenWithLocalName(parent, name);
      if (children.isEmpty()) {
         er.err(XdsErrorCode.Code.XDSIRequestError,
            "RetrieveImagingDocumentSetRequest DocumentRequest element: must contain one " + name + " element",
            this, "RAD TF-3 4.69.5");
         return true;
      }
      if (children.size() > 1) {
         er.err(XdsErrorCode.Code.XDSIRequestError,
            "RetrieveImagingDocumentSetRequest DocumentRequest element: must contain only one " + name + " element",
            this, "RAD TF-3 4.69.5");
         return true;
      }
      OMElement child = children.get(0);
      if (StringUtils.isBlank(child.getText())) {
         er.err(XdsErrorCode.Code.XDSIRequestError,
            "RetrieveImagingDocumentSetRequest DocumentRequest " + name + " element: must contain valid text",
            this, "RAD TF-3 4.69.5");
         return true;
      }
      return false;
   }

   private QName qn(String name) {
      return new QName(name);
   }

}
