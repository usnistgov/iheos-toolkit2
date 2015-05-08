package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactory2I;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.factories.TextErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmsg.message.DocumentAttachmentMapper;
import gov.nist.toolkit.valregmsg.message.DocumentElementValidator;
import gov.nist.toolkit.valregmsg.message.HttpMessageValidator;
import gov.nist.toolkit.valregmsg.message.MetadataMessageValidator;
import gov.nist.toolkit.valregmsg.message.MetadataOrderValidator;
import gov.nist.toolkit.valregmsg.message.QueryRequestMessageValidator;
import gov.nist.toolkit.valregmsg.message.QueryResponseValidator;
import gov.nist.toolkit.valregmsg.message.RegistryResponseValidator;
import gov.nist.toolkit.valregmsg.message.RetrieveRequestValidator;
import gov.nist.toolkit.valregmsg.message.RetrieveResponseValidator;
import gov.nist.toolkit.valregmsg.message.SAMLMessageValidator;
import gov.nist.toolkit.valregmsg.message.SchemaValidator;
import gov.nist.toolkit.valregmsg.message.SchematronValidator;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.message.WrapperValidator;
import gov.nist.toolkit.valregmsg.xdm.XdmDecoder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine.ValidationStep;
import gov.nist.toolkit.valsupport.message.NullMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;

/**
 * A collection of static methods for initiating validations where each method 
 * returns an instance of 
 * MessageValidatorEngine which will manage the execution of the validation.
 * @author bill
 *
 */
public class MessageValidatorFactory implements MessageValidatorFactory2I {
	static OMElement rootElement ;
	static Logger logger = Logger.getLogger(MessageValidatorFactory.class);
	
//	static {
//		// this is needed to manage otherwise circular references
//		// through the class dependency graph.
//		System.out.println("Initializing MessageValidatorFactor");
//		System.out.flush();
//		MessageValidatorFactoryFactory.messageValidatorFactory2I = new MessageValidatorFactory();
//	}
	/**
	 * Start a new validation where the input comes from a file.
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input input file
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return new MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, File input, RegistryValidationInterface rvi) {
		try {
			OMElement xml = Util.parse_xml(input);
			return getValidatorContext(erBuilder, xml, null, rvi);
		} catch (Exception e) {  }
		return null;
	}
	
	// Next two constructors exist to initialize MessageValidatorFactoryFactory which olds
	// a reference to an instance of this class. This is necessary to get around a circular
	// reference in the build tree
	
	public MessageValidatorFactory() {
		System.out.println("MessageValidatorFactory()");
		if (MessageValidatorFactoryFactory.messageValidatorFactory2I == null) {
			MessageValidatorFactoryFactory.messageValidatorFactory2I = new MessageValidatorFactory("a");
		}
	}
	
	public MessageValidatorFactory(String a) {}

	/**
	 * Start a new validation where the input comes from a string.
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input input string
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return new MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, byte[] input, byte[] directCertInput, ValidationContext vc, RegistryValidationInterface rvi) {

		MessageValidatorEngine mvc = new MessageValidatorEngine();
		if (erBuilder != null) {
			ErrorRecorder er = report(erBuilder, mvc, "Requested Validation Context");
			er.detail(vc.toString());
		}
		
		logger.debug("ValidationContext is " + vc.toString());

		String inputString = new String(input).trim();

		if (!vc.isMessageTypeKnown() && vc.updateable) {
			if (inputString.startsWith("POST"))
				vc.hasHttp = true;
		}

		if (vc.isXDM) {
			mvc = getValidatorForXDM(erBuilder, input, mvc, vc, rvi);
		} else if (vc.isNcpdp) {
			mvc = getValidatorForNcpdp(erBuilder, inputString, mvc, vc, rvi);
//		} else if (vc.isCCDA) {
//			mvc = getValidatorForCCDA(erBuilder, input, mvc, vc);
		} else if (vc.hasHttp) {
			mvc = getValidatorForHttp(erBuilder, inputString, mvc, vc, rvi);
		} else {
			mvc = getValidatorForXML(erBuilder, inputString, mvc, vc, rvi);
		}


		return mvc;
	}

	/**
	 * Start a new validation where input is known to be an HTTP message
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param httpInput HTTP input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorForHttp(ErrorRecorderBuilder erBuilder, String httpInput, MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		try {
			HttpParserBa hparser = new HttpParserBa(httpInput.getBytes());
			mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
			mvc.addMessageValidator("HTTP Validator", new HttpMessageValidator(vc, hparser, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		} catch (HttpParseException e) {
			mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
			String msg = "Input does not parse as an HTTP stream: " + ExceptionUtil.exception_details(e);
			reportError(erBuilder, mvc, "HTTP Parser", msg + e.getMessage());
			return mvc;
		} catch (ParseException e) {
			mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
			String msg = "Input does not parse as an HTTP stream: " + ExceptionUtil.exception_details(e);
			reportError(erBuilder, mvc, "HTTP Parser", msg + e.getMessage());
			return mvc;
		}
	}

	static public MessageValidatorEngine getValidatorForXDM(ErrorRecorderBuilder erBuilder, byte[] input, MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
		mvc.addMessageValidator("XDM Validator", new XdmDecoder(vc, erBuilder, Io.bytesToInputStream(input)), erBuilder.buildNewErrorRecorder());
		return mvc;
	}
	/**
	 * Start a new validation where input is known to be XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input XML input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorForNcpdp(ErrorRecorderBuilder erBuilder, String input, MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		//		if(EdiToXml.isEDI(input)){
		//			EdiToXml edx = new EdiToXml(input);
		//			edx.run();
		//			input = edx.getGeneratedOutput();
		//		} 
		return getValidatorForXML(erBuilder, input, mvc, vc, rvi);
	}
	/**
	 * Start a new validation where input is known to be XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input XML input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorForXML(ErrorRecorderBuilder erBuilder, String input, MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		OMElement xml = null;
		try {
			// for now all the message inputs are XML - later some will be HTTP wrapped around XML
			xml = Util.parse_xml(input);
			rootElement = xml ;
			xml.build();
		} catch (Exception e) {  
			mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
			ErrorRecorder er = reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + input.substring(0, min(50, input.length())));
			return mvc;
		}
		return getValidatorContext(erBuilder, xml, mvc, null, vc, rvi);
	}


//	static public MessageValidatorEngine getValidatorForCCDA(ErrorRecorderBuilder erBuilder, byte[] input, MessageValidatorEngine mvc, ValidationContext vc) {
//		mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
//		if (vc != null && vc.ccdaType != null && !vc.ccdaType.equals("")) {
//			// The value "Non-CCDA content" is hard coded in
//			// gov/nist/toolkit/xdstools2/client/tabs/messageValidator/CcdaTypeSelection.java
//			if (!vc.ccdaType.contains("Non-CCDA content"))
//				mvc.addMessageValidator("CCDA Validator", new CcdaMessageValidator(vc, erBuilder, Io.bytesToInputStream(input)), erBuilder.buildNewErrorRecorder());
//		}
//		return mvc;
//	}

	/**
	 * Start a new validation where input is known to be XML.  Actually a duplicate of the previous method.  Some day
	 * this method will be more generic.
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input XML input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, String input, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
		OMElement xml = null;
		try {
			// for now all the message inputs are XML - later some will be HTTP wrapped around XML
			xml = Util.parse_xml(input);
			xml.build();
		} catch (Exception e) {  
			if (mvc == null)
				mvc = new MessageValidatorEngine();
			ErrorRecorder er = reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + input.substring(0, min(50, input.length())));
			return mvc;
		}
		return getValidatorContext(erBuilder, xml, mvc, title, vc, rvi);
	}

	static int min(int a, int b) {
		if (a < b) return a;
		return b;
	}

	/**
	 * Start a new validation on a pre-parsed XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input XML input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, OMElement xml, ValidationContext vc, RegistryValidationInterface rvi) {
		return getValidatorContext(erBuilder, xml, null, null, vc, rvi);
	}

	/**
	 * Start a new validation where the input comes from a byte[]
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input byte[]
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, byte[] input, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
		OMElement xml = null;
		try {
			// for now all the message inputs are XML - later some will be HTTP wrapped around XML
			String inputString = new String(input);
			xml = Util.parse_xml(inputString);
			xml.build();
		} catch (Exception e) {  
			if (mvc == null)
				mvc = new MessageValidatorEngine();
			ErrorRecorder er = reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else {
				String in = new String(input);
				er.detail("Input looks like: " + in.substring(0, min(50, in.length())));
			}
			return mvc;
		}
		return getValidatorContext(erBuilder, xml, mvc, title, vc, rvi);
	}

	/**
	 * Start a new validation on a pre-parsed XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input XML
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, OMElement xml, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
		String rootElementName = xml.getLocalName();
		if (vc == null)
			vc = new ValidationContext();

		if (mvc == null) 
			mvc = new MessageValidatorEngine();

		if (title == null) 
			title = "";


		if (vc.isValid()) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Running requested validation - " + 
					((vc.isMessageTypeKnown()) ? vc.getTransactionName() : "Unknown message type") +
					((vc.hasSoap) ? " with SOAP Wrapper" : ""));

			// SOAP parser will find body and schedule its validation based on 
			// requested validation
			if (vc.hasSoap || vc.hasSaml) {
				mvc.addMessageValidator("SOAP Message", new SoapMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
				return mvc;
			}  else {
				return validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
			}

		} else {
			// Parse based on rootElementName
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Explicit validation not requested - looking at content");
			return validateBasedOnRootElement(erBuilder, xml, mvc, vc,
					rootElementName, rvi);
		}
	}

	/**
	 * Start a new validation on a String input and run the validation based on the rules coded in the ValidationContext
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param body input XML string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return the MessageValidatorEngine after it has run to completion
	 */
	public static MessageValidatorEngine validateBasedOnValidationContext(
			ErrorRecorderBuilder erBuilder, String body,
			MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		OMElement xml = null;
		try {
			// for now all the message inputs are XML - later some will be HTTP wrapped around XML
			xml = Util.parse_xml(body);
			xml.build();
		} catch (Exception e) {  
			ErrorRecorder er = reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (body == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + body.substring(0, min(100, body.length())));
			return mvc;
		}
		return validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
	}


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

		String rootElementName = null;

		if (xml != null)
			rootElementName = xml.getLocalName();

		//XcpdInit, XcpdResp and C32 won't use these schema validators 

		if (!vc.isXcpd && !vc.isC32 && !vc.isNcpdp ) {

			if (vc.hasMetadata()) {
				mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				mvc.addMessageValidator("Validate Metadata Element Ordering", new MetadataOrderValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			}

			if (vc.containsDocuments()) {
				mvc.addMessageValidator(DocumentAttachmentMapper.class.getSimpleName(), new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
			}

			mvc.addMessageValidator("Schema Validator", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
		}
		if( vc.hasSaml )
			mvc.addMessageValidator("SAML Validator", new SAMLMessageValidator(vc, rootElement, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());

		// parse based on Validation Context
		if (vc.isPnR) {
			if (vc.isXDR) {
				if (vc.isRequest) {
					validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
					mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
					return mvc;
				} else {
					validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
					mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
					return mvc;
				}
			} else {
				if (vc.isRequest) {
					validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
					mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
					mvc.addMessageValidator("DocumentElementValidator", new DocumentElementValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
					return mvc;
				} else {
					validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
					mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
					return mvc;
				}
			}
		} else if (vc.isR) {
			if (vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
				mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else {
				validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
				mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			}
		} else if (vc.isMU) {
			if (vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
				mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else {
				validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
				mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			}
		} else if (vc.isRet) {
			if (vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetRequest", rootElementName);
				mvc.addMessageValidator("RetrieveDocumentSetRequest", new RetrieveRequestValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else {
				validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetResponse", rootElementName);
				mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
				return mvc;
			}
		} else if (vc.isSQ) {
			if (vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "AdhocQueryRequest", rootElementName);
				mvc.addMessageValidator("QueryRequest", new QueryRequestMessageValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else {
				validateToplevelElement(erBuilder, mvc, "AdhocQueryResponse", rootElementName);
				mvc.addMessageValidator("AdhocQueryResponse", new QueryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				mvc.addMessageValidator("Contained Metadata", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
				return mvc;
			}
		} else if (vc.isXDM) {
			validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
			mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (vc.isXcpd || vc.isNwHINxcpd) {
			if (vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "PRPA_IN201305UV02", rootElementName);
				mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else {
				validateToplevelElement(erBuilder, mvc, "PRPA_IN201306UV02", rootElementName);
				mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			}
		} else if (vc.isNcpdp) {
			if(vc.isRequest) {
				validateToplevelElement(erBuilder, mvc, "Message", rootElementName);
				mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
				return mvc;
			} else{
				return mvc;
			}
		} else if (vc.isC32) {
			validateToplevelElement(erBuilder, mvc, "ClinicalDocument", rootElementName);
			mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;			
		} else {
			reportError(erBuilder, mvc, "ValidationContext", "Don't know how to parse this: " + vc.toString());
			return mvc;
		}
	}

	/**
	 * Validate top level element name and log error if wrong
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param should expected value
	 * @param found real value to compare
	 */
	static void validateToplevelElement(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String should, String found) {
		if (!should.equals(found))
			reportError(erBuilder, mvc, "Top Element Validator", "Expected " + should + " but found " + found);
	}

	/**
	 * Start a new validation on a string input and run the validation inferred from the name of the XML root element
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param body input string form of the XML
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return the MessageValidatorEngine after it has run to completion
	 */
	public static MessageValidatorEngine validateBasedOnRootElement(
			ErrorRecorderBuilder erBuilder, String body,
			MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
		OMElement xml = null;
		try {
			xml = Util.parse_xml(body);
			xml.build();
		} catch (Exception e) {  
			ErrorRecorder er = reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (body == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + body.substring(0, min(100, body.length())));
			return mvc;
		}
		String rootElementName = xml.getLocalName();
		return validateBasedOnRootElement(erBuilder, xml, mvc, vc, rootElementName, rvi);

	}

	/**
	 * Start a new validation on an XML input and run the validation inferred from the name of the XML root element
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param xml input XML
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return the MessageValidatorEngine after it has run to completion
	 */
	public static MessageValidatorEngine validateBasedOnRootElement(
			ErrorRecorderBuilder erBuilder, OMElement xml,
			MessageValidatorEngine mvc, ValidationContext vc,
			String rootElementName, RegistryValidationInterface rvi) {
		if (rootElementName.equals("ProvideAndRegisterDocumentSetRequest")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Provide and Register request");
			vc.isPnR = true;
			vc.isRequest = true;
			mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("DocumentContentsExtraction", new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("SubmitObjectsRequest")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Register request");
			vc.isR = true;
			vc.isRequest = true;
			mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("RegistryResponse")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a RegistryResponse");
			vc.isR = true; // could also be PnR or XDR - doesn't matter
			vc.isResponse = true;
			mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("AdhocQueryRequest")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Stored Query request");
			vc.isSQ = true;
			vc.isRequest = true;
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("QueryRequest", new QueryRequestMessageValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("AdhocQueryResponse")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Stored Query response");
			vc.isSQ = true;
			vc.isRequest = false;
			vc.isResponse = true;
			mvc.addMessageValidator("AdhocQueryResponse", new QueryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			// need to inspect WSAction to know if it is XC
			mvc.addMessageValidator("Contained Metadata", new MetadataMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("RetrieveDocumentSetRequest")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Retrieve Document Set request");
			vc.isRet = true;
			vc.isRequest = true;
			mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("RetrieveDocumentSetRequest", new RetrieveRequestValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("RetrieveDocumentSetResponse")) {
			reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Retrieve Document Set response");
			vc.isRet = true;
			vc.isResponse = true;
			mvc.addMessageValidator("DocumentContentsExtraction", new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("TestResults")) {
			return getValidatorContextForTestLog(erBuilder, xml, rvi);
		} else if (rootElementName.equals("Envelope")) {
			// schema validation of SOAP envelope is useless
			//			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			// don't know what ValidationContext to set - let this validator choose
			mvc.addMessageValidator("SOAP Wrapper", new SoapMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;
		}else if (rootElementName.equals("Assertion")) {
			// schema validation of SOAP envelope is useless
			//			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			// don't know what ValidationContext to set - let this validator choose
			mvc.addMessageValidator("SAML Wrapper", new SAMLMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			return mvc;

		} else if (rootElementName.equals("PRPA_IN201305UV02")) {
			mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("PRPA_IN201306UV02")) {
			mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else if (rootElementName.equals("ClinicalDocument")) {
			mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
			return mvc;
		} else {
			reportError(erBuilder, mvc, rootElementName, "Don't know how to parse this.");
			return mvc;
		}
	}

	/**
	 * Run validation engine on contents of each step in a log.xml file produced by the test engine
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param xml XML form of the log.xml file
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are inferred from the contents.
	 */
	static MessageValidatorEngine getValidatorContextForTestLog(ErrorRecorderBuilder erBuilder, OMElement xml, RegistryValidationInterface rvi) {
		MessageValidatorEngine mvcx = new MessageValidatorEngine();

		reportParseDecision(erBuilder, mvcx, "Parse Decision", "Input is a Test Log file");

		List<OMElement> testSteps = MetadataSupport.childrenWithLocalName(xml, "TestStep");
		for (OMElement testStep : testSteps) {
			String id = testStep.getAttributeValue(MetadataSupport.id_qname);
			reportParseDecision(erBuilder, mvcx, "Test log Step", id);
			try {
				AXIOMXPath xpathExpression = new AXIOMXPath("//InputMetadata");
				List<OMNode> nodes = xpathExpression.selectNodes(testStep);
				if (nodes.size() == 0) {
					reportError(erBuilder, mvcx, id, "No InputMetadata section in step " + id + " of this log file");
				} else if (nodes.get(0) instanceof OMElement){
					OMElement ele = (OMElement) nodes.get(0);
					getValidatorContext(erBuilder, ele.getFirstElement(), mvcx, "Section " + id + " InputMetadata", null, rvi);
				} else {
					reportError(erBuilder, mvcx, id, "Child of InputMetadata element is not an XML Element in step " + id + " of this log file");
				}

			} catch (Exception e) {
				reportError(erBuilder, mvcx, id, "Cannot extract InputMetadata section from step " + id + " of this log file");
			}
			try {
				AXIOMXPath xpathExpression = new AXIOMXPath("//Result");
				List<OMNode> nodes = xpathExpression.selectNodes(testStep);
				if (nodes.size() == 0) {
					reportError(erBuilder, mvcx, id, "No Result section in step " + id + " of this log file");
				} else if (nodes.get(0) instanceof OMElement) {
					OMElement ele = (OMElement) nodes.get(0);
					getValidatorContext(erBuilder, ele.getFirstElement(), mvcx, "Section " + id + " Result", null, rvi);
				} else {
					reportError(erBuilder, mvcx, id, "Child of Result element is not an XML Element in step " + id + " of this log file");
				}
			} catch (Exception e) {
				reportError(erBuilder, mvcx, id, "Cannot extract Result section from step " + id + " of this log file");
			}
		}
		return mvcx;

	}

	/**
	 * Report an error in a newly constructed ErrorRecorder
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use
	 * @param title title of new ErrorRecorder section to be built
	 * @param error text of error
	 * @return new ErrorRecorder
	 */
	static ErrorRecorder reportError(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title, String error) {
		ErrorRecorder er = erBuilder.buildNewErrorRecorder();
		er.err(XdsErrorCode.Code.XDSRegistryError, error, "MessageValidatorFactory", "");
		mvc.addMessageValidator(title, new NullMessageValidator(new ValidationContext()), er);
		return er;
	}

	/**
	 * Create a a newly constructed ErrorRecorder and assign it a title
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use
	 * @param title title of new ErrorRecorder section to be built
	 * @return new ErrorRecorder
	 */
	static ErrorRecorder report(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title) {
		ErrorRecorder er = erBuilder.buildNewErrorRecorder();
		mvc.addMessageValidator(title, new NullMessageValidator(new ValidationContext()), er);
		return er;
	}

	/**
	 * Create new ErrorRecorder and use it to report a significant parse decision
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use
	 * @param title name of decision
	 * @param text text of decision
	 */
	static void reportParseDecision(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title, String text) {
		ErrorRecorder er = erBuilder.buildNewErrorRecorder();
		//		er.info1(text);
		mvc.addMessageValidator(title + " - " + text, new NullMessageValidator(new ValidationContext()), er);
	}

	public static void main(String[] args) {
		MessageValidatorEngine mvc = getValidator(new TextErrorRecorderBuilder(), new File(args[0]), null);
		mvc.run();

		for (int i=0; i< mvc.getValidationStepCount(); i++) {
			ValidationStep vs = mvc.getValidationStep(i);
			System.out.println("========== " + vs.getStepName() + " =============");
			vs.getErrorRecorder().showErrorInfo();
		}
	}

}
