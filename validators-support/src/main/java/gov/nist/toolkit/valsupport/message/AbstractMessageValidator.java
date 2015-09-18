package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;


/**
 * Abstract class that all validators and simulators are based on. It's primary
 * significance is forcing validators to support the run method so they
 * can be called when their turn comes on the validation queue.
 * @author bill
 *
 */
abstract public class AbstractMessageValidator {
	protected ValidationContext vc; 
	public ErrorRecorder er;
	
	abstract public void run(ErrorRecorder er, MessageValidatorEngine mvc);

	// System level validators, those that trigger soap faults, should
	// override this method and return true
	public boolean isSystemValidator() { return false; }

	public AbstractMessageValidator(ValidationContext vc) {
		this.vc = vc;
	}
	
	public ErrorRecorder getErrorRecorder() {
		return er;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("\tVC: ").append(vc.toString());
		
		return buf.toString();
	}
	

}
