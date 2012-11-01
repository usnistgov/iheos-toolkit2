package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.valsupport.message.MessageValidator;

public abstract class Simulator extends MessageValidator {
	protected SimCommon common;
	
	public Simulator(SimCommon common) {
		super(common.vc);
		this.common = common;
	}

}
