package gov.nist.toolkit.valccda;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.io.InputStream;

public class CcdaMessageValidator extends MessageValidator {
	InputStream in;
	ErrorRecorderBuilder erBuilder;
	String ccdaType;

	public CcdaMessageValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, InputStream ccdaInputStream) {
		super(vc);
		this.erBuilder = erBuilder;   // never used.  ErrorRecorder is passed to run()
		in = ccdaInputStream;
		this.ccdaType = vc.ccdaType;
	}

	/**
	 * 09/24/2012 : Rama Ramakrishnan : Invoking the CDA validation statically.
	 * 
	 * @param er
	 * @param mvc
	 */
	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		er.challenge("Validating the CCDA against CCDA type &lt;" + ccdaType + ">");

		try {
			CcdaValidator.validateCDA(in, ccdaType, er);
		} catch (Exception e) {
			er.err(null, e);
		}
	}

}
