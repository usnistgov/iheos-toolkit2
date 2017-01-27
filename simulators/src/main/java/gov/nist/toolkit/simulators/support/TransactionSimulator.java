package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;


public abstract class TransactionSimulator extends AbstractMessageValidator {
	protected SimCommon common;
	protected SimulatorConfig simulatorConfig;
	
	public TransactionSimulator(SimCommon common, SimulatorConfig simulatorConfig) {
		super(common.vc);
		this.common = common;
		this.simulatorConfig = simulatorConfig;
	}

	public SimCommon getCommon() { return common; }
}
