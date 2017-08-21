package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.BaseActorSimulator;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base class for actor simulators. This extends MessageValidator because the engine
 * MessageValidatorEngine expects it
 * @author bill
 *
 */
public abstract class BaseDsActorSimulator extends BaseActorSimulator {
//    protected SimDb db;
//    protected SimCommon common = null;
	protected DsSimCommon dsSimCommon = null;
	protected ErrorRecorder er = null;
	public HttpServletResponse response;

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
	abstract public void init();


	public BaseDsActorSimulator(SimCommon common, DsSimCommon dsSimCommon) {
		super(common);
//		super(common.getValidationContext());
//		this.common = common;
		this.dsSimCommon = dsSimCommon;
		er = common.getCommonErrorRecorder();
	}

	public BaseDsActorSimulator() {}

	public void init(DsSimCommon c, SimulatorConfig config) {
		super.init(config);
		dsSimCommon = c;
		common = dsSimCommon.simCommon;
		if (c == null) return;
//		common = c.common;
		er = c.simCommon.getCommonErrorRecorder();
		db = c.simCommon.db;
		dsSimCommon.setSimulatorConfig(config);
		response = dsSimCommon.simCommon.response;
		init();
	}



	public ValidationContext getValidationContext() {
		return common.getValidationContext();
	}

	protected ErrorRecorder newER() {
		return new GwtErrorRecorderBuilder().buildNewErrorRecorder();
	}

    public SimulatorConfig getSimulatorConfig() { return dsSimCommon.simulatorConfig; }
    public void setSimulatorConfig(SimulatorConfig config) { dsSimCommon.simulatorConfig = config; }

	public SimCommon getCommon() {
		return common;
	}

	public DsSimCommon getDsSimCommon() { return dsSimCommon; }
}
