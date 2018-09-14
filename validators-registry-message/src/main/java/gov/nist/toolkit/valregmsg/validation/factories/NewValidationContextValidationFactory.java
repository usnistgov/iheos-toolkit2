package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.valregmsg.message.*;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageBody;
import gov.nist.toolkit.valsupport.message.MessageBodyContainer;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;

/**
 * Run validation based on a ValidationContext.
 */
public class NewValidationContextValidationFactory {
    /**
     * Start a new validation on an XML input and run the validation based on the rules coded in the ValidationContext
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param xml input XML
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return the MessageValidatorEngine after it has run to completion
     */
    public static MessageValidatorEngine validateBasedOnValidationContext(
            ErrorRecorderBuilder erBuilder, OMElement xml,
            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        RootElementValidatorFactory.logger.debug("messageValidatorEngine#validateBasedOnValidationContext");
        RootElementValidatorFactory.logger.debug(" VC: " + vc.toString());

        String rootElementName = null;

        if (xml != null)
            rootElementName = xml.getLocalName();

        //XcpdInit, XcpdResp and C32 won't use these schema validators

        if (!vc.isXcpd && !vc.isC32 && !vc.isNcpdp ) {

            if (vc.hasMetadata()) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Validate Metadata Element Ordering", new MetadataOrderValidator(vc), erBuilder.buildNewErrorRecorder());
            }

            if (vc.containsDocuments()) {
                mvc.addMessageValidator(DocumentAttachmentMapper.class.getSimpleName(), new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
            }

            mvc.addMessageValidator("Schema Validator", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
        }

        // parse based on Validation Context
        if (vc.isPnR) {
            if (vc.isXDR) {
                if (vc.isRequest) {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
                    mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                    return mvc;
                } else {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                    mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                    return mvc;
                }
            } else {
                if (vc.isRequest) {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
                    mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                    if (vc.hasHttp)
                        mvc.addMessageValidator("DocumentElementValidator", new DocumentElementValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                    return mvc;
                } else {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                    mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                    return mvc;
                }
            }
        } else if (vc.isR || vc.isRODDE) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
                mvc.addMessageValidator("SubmitObjectsRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isMU || vc.isRMU) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
                mvc.addMessageValidator("SubmitObjectsRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isRet) {
            if (vc.isRequest) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetRequest", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetRequest", new RetrieveRequestValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetResponse", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            }

        } else if (vc.isRad69) {
            if (vc.isRequest) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveImagingDocumentSetRequest", rootElementName);
                mvc.addMessageValidator("RetrieveImagingDocumentSetRequest", new RetrieveImagingDocumentSetRequestValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetResponse", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            }



        } else if (vc.isSQ) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "AdhocQueryRequest", rootElementName);
                mvc.addMessageValidator("QueryRequest", new QueryRequestMessageValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "AdhocQueryResponse", rootElementName);
                mvc.addMessageValidator("AdhocQueryResponse", new QueryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Contained Metadata", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isXDM) {
            CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
            mvc.addMessageValidator("SubmitObjectsRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        }
        else {
            ValUtil.reportError(erBuilder, mvc, "ValidationContext", "Don't know how to parse this: " + vc.toString());
            return mvc;
        }
    }
}
