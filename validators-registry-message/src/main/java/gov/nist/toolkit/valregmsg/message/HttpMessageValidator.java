package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.ErrorRecorderFactory;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

/**
 * Validate HTTP message.  Launches either MtomMessageValidator or SimpleSoapHttpHeaderValidator as appropriate.
 * @author bill
 *   
 */
public class HttpMessageValidator extends AbstractMessageValidator {
	// Either header and body OR hparser are initialized by the constructor
	String header = null;
	byte[] body;
	HttpParserBa hparser = null;
	IErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public HttpMessageValidator(ValidationContext vc, String header, byte[] body, IErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.header = header;
		this.body = body;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public HttpMessageValidator(ValidationContext vc, HttpParserBa hparser, IErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.hparser = hparser;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		//er.registerValidator(this);
		
		try {
			if (mvc == null) 
				mvc = new MessageValidatorEngine();

			er.sectionHeading("HTTP message format validation");
            if (!vc.isValid()) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA158");
				er.err(vc.getBasicErrorCode(), assertion, this, "", "");
                //er.unRegisterValidator(this);
                return;
            }

			if (header != null)
				hparser = new HttpParserBa(header.getBytes()); // since this is an exploratory parse, don't pass er
			else
				body = hparser.getBody();

			hparser.setErrorRecorder(er);
			if (hparser.isMultipart()) {
				if (vc.requiresSimpleSoap()) {
					Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA159");
					String detail = "Found 'Multipart', expected 'Simple Format'";
					er.err(XdsErrorCode.Code.NoCode, assertion, this, "", detail);
				} else {
                    er.success("", "Message format", "Multipart", "Multipart", "ITI TF Volumes 2a and 2b");
				}
				mvc.addMessageValidator("Validate MTOM", new MtomMessageValidator(vc, hparser, body, erBuilder, mvc, rvi), ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder());
			} else {
				boolean mt = vc.requiresMtom();
                if (mt) {
					Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA160");
					String detail = "Found 'Simple Format', expected 'Multipart'";
					er.err(XdsErrorCode.Code.NoCode, assertion, this, "", detail);
                } else {
                    er.success("", "Message format", "SIMPLE Format", "SIMPLE Format", "ITI TF Volumes 2a and 2b");
                }
				mvc.addMessageValidator("Parse SIMPLE SOAP message", new SimpleSoapHttpHeaderValidator(vc, hparser, body, erBuilder, mvc, rvi), ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder());
			}
		} catch (HttpParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		} catch (ParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		}
		finally {
			//er.unRegisterValidator(this);
		}

	}

    @Override
	public boolean isSystemValidator() { return true; }


}
