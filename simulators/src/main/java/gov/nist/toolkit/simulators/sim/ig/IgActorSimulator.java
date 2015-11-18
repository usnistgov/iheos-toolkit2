package gov.nist.toolkit.simulators.sim.ig;


import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGenerator;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.GatewaySimulatorCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.IOException;

public class IgActorSimulator extends GatewaySimulatorCommon {
//	DsSimCommon dsSimCommon;
//	SimDb db;
	OMElement messageBody;
	static Logger logger = Logger.getLogger(IgActorSimulator.class);
	AdhocQueryResponseGenerator sqs;

	public IgActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
		super(common, dsSimCommon);
		this.db = db;
		setSimulatorConfig(simulatorConfig);
	}

	public IgActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
		super(dsSimCommon.simCommon, dsSimCommon);
		this.db = dsSimCommon.simCommon.db;
        setSimulatorConfig(simulatorConfig);
	}

    public IgActorSimulator() {}

	public void init() {}

	// boolean => hasErrors?
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validationPattern) throws IOException {

        logger.info("IgActorSimulator: run - transactionType is " + transactionType);

		if (transactionType.equals(TransactionType.IG_QUERY)) {

			common.vc.isSQ = true;
			common.vc.isXC = false;
			common.vc.isRequest = true;
			common.vc.isSimpleSoap = true;
			common.vc.hasSoap = true;
			common.vc.hasHttp = true;


			
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;
			
			if (mvc.hasErrors()) {
                dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			// extract query 
			AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (mv == null || !(mv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "IG Internal Error - cannot find SoapMessageValidator instance", "InitiatingGatewayActorSimulator", "");
                dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			SoapMessageValidator smv = (SoapMessageValidator) mv;
			OMElement query = smv.getMessageBody();
			
			boolean validateOk = validateHomeCommunityId(er, query, false);
			if (!validateOk)
				return false;

			XcQuerySim xcqSim = new XcQuerySim(common, dsSimCommon, getSimulatorConfig());
			mvc.addMessageValidator("XcQuerySim", xcqSim, er);

			mvc.run();

			// Add in errors
			AdhocQueryResponseGenerator ahqrg = new AdhocQueryResponseGenerator(common, dsSimCommon, xcqSim);
			mvc.addMessageValidator("Attach Errors", ahqrg, er);
			mvc.run();
			sqs = ahqrg;

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, sqs), er);

			// this will only run the new validators
			mvc.run();
			
			return false; // no updates anyway

		} 
		else if (transactionType.equals(TransactionType.IG_RETRIEVE)) {
            er.err(Code.XDSRegistryError, "Transaction not supported " + transactionType, "InitiatingGatewayActorSimulator", "");
            dsSimCommon.sendFault("Transaction not supported " + transactionType, null);
            return true;
		}
			
		else {
			er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType, "InitiatingGatewayActorSimulator", "");
            dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
			return true;
		}
	}


}
