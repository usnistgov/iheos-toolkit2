package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactory2I;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.factories.TextErrorRecorderBuilder;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.xdm.XdmDecoder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.ValidationStep;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * A collection of static methods for initiating validations where each method
 * returns an instance of
 * MessageValidatorEngine which will manage the execution of the validation.
 * @author bill
 *
 */
public class CommonMessageValidatorFactory implements MessageValidatorFactory2I {
	static OMElement rootElement ;
	static Logger logger = Logger.getLogger(CommonMessageValidatorFactory.class);

//	static {
//		// this is needed to manage otherwise circular references
//		// through the class dependency graph.
//		System.out.println("Initializing MessageValidatorFactor");
//		System.out.flush();
//		MessageValidatorFactoryFactory.messageValidatorFactory2I = new CommonMessageValidatorFactory();
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
	// a reference to an instance of this class. This is necessary to getRetrievedDocumentsModel around a circular
	// reference in the build tree

	public CommonMessageValidatorFactory() {
		System.out.println("CommonMessageValidatorFactory()");
		if (MessageValidatorFactoryFactory.messageValidatorFactory2I == null) {
			MessageValidatorFactoryFactory.messageValidatorFactory2I = new CommonMessageValidatorFactory("a");
		}
	}

	public CommonMessageValidatorFactory(String a) {}

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
			ErrorRecorder er = ValUtil.report(erBuilder, mvc, "Requested Validation Context");
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
		}
//		else if (vc.isNcpdp) {
//			mvc = getValidatorForNcpdp(erBuilder, inputString, mvc, vc, rvi);
//		}
//      else if (vc.isCCDA) {
//			mvc = getValidatorForCCDA(erBuilder, input, mvc, vc);
//		}
		else if (vc.hasHttp) {
//			mvc = getValidatorForHttp(erBuilder, inputString, mvc, vc, rvi);
		}
		else {
			mvc = getValidatorForXML(erBuilder, inputString, mvc, vc, rvi);
		}


		return mvc;
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
			ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + input.substring(0, ValUtil.min(50, input.length())));
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
			ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else
				er.detail("Input looks like: " + input.substring(0, ValUtil.min(50, input.length())));
			return mvc;
		}
		return getValidatorContext(erBuilder, xml, mvc, title, vc, rvi);
	}


	/**
	 * Start a new validation on a pre-parsed XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
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
			ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
			if (input == null)
				er.detail("Input was null");
			else {
				String in = new String(input);
				er.detail("Input looks like: " + in.substring(0, ValUtil.min(50, in.length())));
			}
			return mvc;
		}
		return getValidatorContext(erBuilder, xml, mvc, title, vc, rvi);
	}

	/**
	 * Start a new validation on a pre-parsed XML
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static private MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, OMElement xml, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
		String rootElementName = xml.getLocalName();
		if (vc == null)
			vc = DefaultValidationContextFactory.validationContext();

		if (mvc == null)
			mvc = new MessageValidatorEngine();

		if (title == null)
			title = "";


		if (vc.isValid()) {
			ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Running requested validation - " +
					((vc.isMessageTypeKnown()) ? vc.getTransactionName() : "Unknown message type") +
					((vc.hasSoap) ? " with SOAP Wrapper" : ""));

			// SOAP parser will find body and schedule its validation based on
			// requested validation
//			if (vc.hasSoap || vc.hasSaml) {
//				mvc.addMessageValidator("SOAP Message Parser", new SoapMessageParser(vc, xml), erBuilder.buildNewErrorRecorder());
//				mvc.addMessageValidator("SOAP Message Validator", new SoapMessageValidator(vc, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
//				return mvc;
//			}  else {
				return ValidationContextValidationFactory.validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
//			}

		} else {
			// Parse based on rootElementName
			ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Explicit validation not requested - looking at content");
			return RootElementValidatorFactory.validateBasedOnRootElement(erBuilder, xml, mvc, vc,
					rootElementName, rvi);
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
			ValUtil.reportError(erBuilder, mvc, "Top Element Validator", "Expected " + should + " but found " + found);
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

		ValUtil.reportParseDecision(erBuilder, mvcx, "Parse Decision", "Input is a Test Log file");

		List<OMElement> testSteps = XmlUtil.childrenWithLocalName(xml, "TestStep");
		for (OMElement testStep : testSteps) {
			String id = testStep.getAttributeValue(MetadataSupport.id_qname);
			ValUtil.reportParseDecision(erBuilder, mvcx, "Test log Step", id);
			try {
				AXIOMXPath xpathExpression = new AXIOMXPath("//InputMetadata");
				List<OMNode> nodes = xpathExpression.selectNodes(testStep);
				if (nodes.size() == 0) {
					ValUtil.reportError(erBuilder, mvcx, id, "No InputMetadata section in step " + id + " of this log file");
				} else if (nodes.get(0) instanceof OMElement){
					OMElement ele = (OMElement) nodes.get(0);
					getValidatorContext(erBuilder, ele.getFirstElement(), mvcx, "Section " + id + " InputMetadata", null, rvi);
				} else {
					ValUtil.reportError(erBuilder, mvcx, id, "Child of InputMetadata element is not an XML Element in step " + id + " of this log file");
				}

			} catch (Exception e) {
				ValUtil.reportError(erBuilder, mvcx, id, "Cannot extract InputMetadata section from step " + id + " of this log file");
			}
			try {
				AXIOMXPath xpathExpression = new AXIOMXPath("//Result");
				List<OMNode> nodes = xpathExpression.selectNodes(testStep);
				if (nodes.size() == 0) {
					ValUtil.reportError(erBuilder, mvcx, id, "No Result section in step " + id + " of this log file");
				} else if (nodes.get(0) instanceof OMElement) {
					OMElement ele = (OMElement) nodes.get(0);
					getValidatorContext(erBuilder, ele.getFirstElement(), mvcx, "Section " + id + " Result", null, rvi);
				} else {
					ValUtil.reportError(erBuilder, mvcx, id, "Child of Result element is not an XML Element in step " + id + " of this log file");
				}
			} catch (Exception e) {
				ValUtil.reportError(erBuilder, mvcx, id, "Cannot extract Result section from step " + id + " of this log file");
			}
		}
		return mvcx;

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
