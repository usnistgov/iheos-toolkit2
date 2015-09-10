package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;

import java.io.IOException;

/**
 * Base class for actor simulators. This extends MessageValidator because the engine
 * MessageValidatorEngine expects it
 * @author bill
 *
 */
public abstract class AbstractDsActorSimulator {
	protected SimCommon common;
	protected DsSimCommon dsSimCommon;
	protected ErrorRecorder er;
	protected SimulatorConfig simulatorConfig;

	/**
	 * Start execution of a transaction to this actor simulator.
	 * @param transactionType transaction code
	 * @param mvc MessageValidatorEngine - execution engine for validators and simulators
	 * @param validation name of special validation to be run. Allows simulators to be extended
	 * to perform test motivated validations
	 * @return should relevant databases be updated?
	 * @throws IOException
	 */
	abstract public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException;

	// Services may need extension via hooks.  These are the hooks
	// They are meant to be overloaded
	public void onCreate() {}
	public void onDelete() {}
	public void onTransactionBegin() {}
	public void onTransactionEnd() {}
	public void onServiceStart() {}  // these two refer to Servlet engine start/stop
	public void onServiceStop() {}

	public AbstractDsActorSimulator(SimCommon common, DsSimCommon dsSimCommon) {
//		super(common.getValidationContext());
		this.common = common;
		this.dsSimCommon = dsSimCommon;
		er = common.getCommonErrorRecorder();
	}
	
	public ValidationContext getValidationContext() {
		return common.getValidationContext();
	}

	protected ErrorRecorder newER() {
		return new GwtErrorRecorderBuilder().buildNewErrorRecorder();
	}

}
