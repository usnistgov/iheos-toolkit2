package gov.nist.toolkit.validatorsSoapMessage.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
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
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;
	
	public HttpParserBa getHttpParserBa() {
	   return hparser;
	}

	public HttpMessageValidator(ValidationContext vc, String header, byte[] body, 
	   ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, 
	   RegistryValidationInterface rvi) {
		super(vc);
		this.header = header;
		this.body = body;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public HttpMessageValidator(ValidationContext vc, HttpParserBa hparser, 
	   ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, 
	   RegistryValidationInterface rvi) {
		super(vc);
		this.hparser = hparser;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		
		try {
			if (mvc == null) 
				mvc = new MessageValidatorEngine();

			er.sectionHeading("HTTP message format validation");
            if (!vc.isValid()) {
                er.err(vc.getBasicErrorCode(), "Internal Error: Invalid message format: " + vc, this, "");
                er.unRegisterValidator(this);
                return;
            }

			if (header != null)
            if (vc.isRad55) hparser = new HttpParserBa(header.getBytes(), new String[] {"GET"});
            else hparser = new HttpParserBa(header.getBytes()); // since this is an exploratory parse, don't pass er
			else
				body = hparser.getBody(); 

			hparser.setErrorRecorder(er);
			if (hparser.isMultipart()) {
				if (vc.requiresSimpleSoap()) {
                    er.error("", "Message Format", "Multipart", "SIMPLE Format", "ITI TF Volumes 2a and 2b");
				} else {
                    er.success("", "Message format", "Multipart", "Multipart", "ITI TF Volumes 2a and 2b");
				}
				mvc.addMessageValidator("Validate MTOM", new MtomMessageValidator(vc, hparser, body, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			} else {
				boolean mt = vc.requiresMtom();
                if (mt) {
                    er.error("", "Message Format", "SIMPLE Format", "Multipart", "ITI TF Volumes 2a and 2b");
                } else {
                    er.success("", "Message format", "SIMPLE Format", "SIMPLE Format", "ITI TF Volumes 2a and 2b");
                }
                if (!vc.isRad55)
				mvc.addMessageValidator("Parse SIMPLE SOAP message", new SimpleSoapHttpHeaderValidator(vc, hparser, body, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			}
		} catch (HttpParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		} catch (ParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		}
		finally {
			er.unRegisterValidator(this);
		}

	}

    @Override
	public boolean isSystemValidator() { return true; }


}
