package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;


public abstract class TransactionSimulator extends MessageValidator {
	protected SimCommon common;
	
	public TransactionSimulator(SimCommon common) {
		super(common.vc);
		this.common = common;
	}

	abstract public void run(ErrorRecorder er, MessageValidatorEngine mvc);

}
