package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

/**
 * Validate HTTP message.  Launches either MtomMessageValidator or SimpleSoapMessageValidator as appropriate.
 * @author bill
 *   
 */
public class HttpMessageValidator extends MessageValidator {
	// Either header and body OR hparser are initialized by the constructor
	String header = null;
	byte[] body;
	HttpParserBa hparser = null;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;

	public HttpMessageValidator(ValidationContext vc, String header, byte[] body, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.header = header;
		this.body = body;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public HttpMessageValidator(ValidationContext vc, HttpParserBa hparser, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.hparser = hparser;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		try {
			if (mvc == null) 
				mvc = new MessageValidatorEngine();

			er.challenge("Parsing HTTP message");
			
			if (header != null)
				hparser = new HttpParserBa(header.getBytes()); // since this is an exploratory parse, don't pass er
			else
				body = hparser.getBody();
			
			hparser.setErrorRecorder(er);
			if (hparser.isMultipart()) {
				if (vc.isValid() && vc.requiresSimpleSoap()) 
					er.err(vc.getBasicErrorCode(), "Requested message type requires SIMPLE SOAP format message - MTOM format found", this, "ITI TF Volumes 2a and 2b");
				else
					er.detail("Message is Multipart format");
				er.detail("Scheduling MTOM parser");
				mvc.addMessageValidator("Validate MTOM", new MtomMessageValidator(vc, hparser, body, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
//				er.err("MTOM parser not available","");
			} else {
				boolean val = vc.isValid(); 
				boolean mt = vc.requiresMtom();
				if (!val && mt)
					er.err(vc.getBasicErrorCode(), "Invalid message format: " + vc, this, "ITI TF Volumes 2a and 2b");
				if (mt)
					er.err(vc.getBasicErrorCode(), "Request Message is SIMPLE SOAP but MTOM is required", this, "ITI TF Volumes 2a and 2b");
				else
					er.detail("Request Message is SIMPLE SOAP format");
				er.detail("Scheduling SIMPLE SOAP parser");
				mvc.addMessageValidator("Validate SIMPLE SOAP", new SimpleSoapMessageValidator(vc, hparser, body, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
//				SimpleSoapMessageValidator val = new SimpleSoapMessageValidator(vc, hparser, body, erBuilder, mvc);
//				val.run(er);
			}
		} catch (HttpParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		} catch (ParseException e) {
			er.err(vc.getBasicErrorCode(), e);
		}

	}

}
