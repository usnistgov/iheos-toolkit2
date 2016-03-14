package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * This defines a new, usually external, service request.  It holds
 * the top-level ValidationContext and ErrorRecorder. It
 * does no validation.  It is used by subsequent steps to get
 * access to these two objects.
 * @author bill
 *
 */
public class ServiceRequestContainer extends AbstractMessageValidator {
	
	public ServiceRequestContainer(ValidationContext vc) {
		super(vc);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
        er.registerValidator(this);
        er.unRegisterValidator(this);
	}

}
