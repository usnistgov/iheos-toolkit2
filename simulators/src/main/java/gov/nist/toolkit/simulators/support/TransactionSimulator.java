package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;


public abstract class TransactionSimulator extends AbstractMessageValidator {
	protected SimCommon common;
	
	public TransactionSimulator(SimCommon common) {
		super(common.vc);
		this.common = common;
	}

	public SimCommon getCommon() { return common; }
}
