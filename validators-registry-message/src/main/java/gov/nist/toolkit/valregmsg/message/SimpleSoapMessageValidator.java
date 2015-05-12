package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

/**
 * Validate SIMPLE SOAP message. The input (an HTTP stream) has already been parsed
 * and the headers are in a HttpParserBa class and the body in a byte[]. This 
 * validator only evaluates the HTTP headers. Validation of the body is passed
 * off to MessageValidatorFactory.
 * @author bill
 *
 */
public class SimpleSoapMessageValidator extends MessageValidator {
	HttpParserBa hparser;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	byte[] bodyBytes;
	String charset = null;
	RegistryValidationInterface rvi;

	public SimpleSoapMessageValidator(ValidationContext vc, HttpParserBa hparser, byte[] body, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.hparser = hparser;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
		this.bodyBytes = body;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		String contentTypeString = hparser.getHttpMessage().getHeader("content-type");
		try {
			HttpHeader contentTypeHeader = new HttpHeader(contentTypeString);
			String contentTypeValue = contentTypeHeader.getValue();
			if (contentTypeValue == null) contentTypeValue = "";
			if (!"application/soap+xml".equals(contentTypeValue.toLowerCase()))
				err("Content-Type header must have value application/soap+xml - found instead " + contentTypeValue,"http://www.w3.org/TR/soap12-part0 - Section 4.1.2");

			charset = contentTypeHeader.getParam("charset");
			if (charset == null || charset.equals("")) {
				charset = "UTF-8";
				er.detail("No message CharSet found in Content-Type header, assuming " + charset);
			} else {
				er.detail("Message CharSet is " + charset);
			}

//			String body = new String(bodyBytes, charset);
			vc.isSimpleSoap = true;
			vc.hasSoap = true;

			er.detail("Scheduling validation of SOAP wrapper");
			MessageValidatorFactory.getValidatorContext(erBuilder, bodyBytes, mvc, "Validate SOAP", vc, rvi);

		} catch (ParseException e) {
			err(e);
//		} catch (UnsupportedEncodingException e) {
//			err(e);
		}

	}
	
	void err(String msg, String ref) {
		er.err(XdsErrorCode.Code.NoCode, msg, this, ref);
	}
	
	void err(Exception e) {
		er.err(XdsErrorCode.Code.NoCode, e);
	}



}
