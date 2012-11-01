package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * A validator with and empty run method.  Used as a place to hang
 * an isolated ErrorRecorder.
 * @author bill
 *
 */
public class NullMessageValidator extends MessageValidator {
	
	public NullMessageValidator(ValidationContext vc) {
		super(vc);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
	}

}
